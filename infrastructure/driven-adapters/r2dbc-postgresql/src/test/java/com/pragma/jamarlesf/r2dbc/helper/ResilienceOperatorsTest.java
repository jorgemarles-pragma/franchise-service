package com.pragma.jamarlesf.r2dbc.helper;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.TransientDataAccessException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResilienceOperatorsTest {

    private CircuitBreaker circuitBreaker;
    private Retry retry;
    private TimeLimiter timeLimiter;
    private ResilienceOperators operators;

    @BeforeEach
    void setUp() {
        circuitBreaker = CircuitBreakerRegistry.of(CircuitBreakerConfig.custom()
                .slidingWindowSize(4)
                .minimumNumberOfCalls(2)
                .failureRateThreshold(50.0f)
                .waitDurationInOpenState(Duration.ofSeconds(5))
                .build()).circuitBreaker("testCB");

        retry = RetryRegistry.of(RetryConfig.custom()
                .maxAttempts(3)
                .intervalFunction(IntervalFunction.of(Duration.ofMillis(10)))
                .retryExceptions(TransientDataAccessException.class)
                .build()).retry("testRetry");

        timeLimiter = TimeLimiterRegistry.of(TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofMillis(200))
                .build()).timeLimiter("testTL");

        operators = new ResilienceOperators(circuitBreaker, retry, timeLimiter);
    }

    @Test
    @DisplayName("Should successfully emit value when upstream succeeds within timeout")
    void shouldEmitValueWhenUpstreamSucceeds() {
        Mono<String> source = Mono.just("data");

        StepVerifier.create(operators.apply(source))
                .expectNext("data")
                .verifyComplete();
    }

    @Test
    @DisplayName("Should successfully emit flux items when upstream succeeds")
    void shouldEmitFluxItemsWhenUpstreamSucceeds() {
        Flux<String> source = Flux.just("item1", "item2");

        StepVerifier.create(operators.apply(source))
                .expectNext("item1", "item2")
                .verifyComplete();
    }

    @Test
    @DisplayName("Should retry up to max attempts on transient database exception")
    void shouldRetryOnTransientException() {
        AtomicInteger attempts = new AtomicInteger(0);

        Mono<String> source = Mono.defer(() -> {
            int current = attempts.incrementAndGet();
            if (current < 3) {
                return Mono.error(new TransientDataAccessException("DB connection lost") {});
            }
            return Mono.just("recovered");
        });

        StepVerifier.create(operators.apply(source))
                .expectNext("recovered")
                .verifyComplete();

        assertEquals(3, attempts.get());
    }

    @Test
    @DisplayName("Should trigger timeout when Mono exceeds configured duration")
    void shouldTriggerTimeoutWhenExceedsDuration() {
        Mono<String> slowMono = Mono.delay(Duration.ofMillis(500))
                .map(l -> "late");

        StepVerifier.create(operators.apply(slowMono))
                .expectErrorMatches(throwable -> throwable instanceof java.util.concurrent.TimeoutException)
                .verify();
    }

    @Test
    @DisplayName("Should transition CircuitBreaker to OPEN when failure threshold is exceeded")
    void shouldOpenCircuitBreakerOnFailures() {
        Mono<String> failingMono = Mono.error(new RuntimeException("Fatal DB error"));

        // Call 1 fails
        StepVerifier.create(operators.apply(failingMono))
                .expectError(RuntimeException.class)
                .verify();

        // Call 2 fails -> minimum calls (2) reached, 100% failure rate -> CB opens!
        StepVerifier.create(operators.apply(failingMono))
                .expectError(RuntimeException.class)
                .verify();

        assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());

        // Call 3 should fail-fast with CallNotPermittedException
        StepVerifier.create(operators.apply(Mono.just("wont-execute")))
                .expectErrorMatches(throwable -> throwable instanceof CallNotPermittedException)
                .verify();
    }
}
