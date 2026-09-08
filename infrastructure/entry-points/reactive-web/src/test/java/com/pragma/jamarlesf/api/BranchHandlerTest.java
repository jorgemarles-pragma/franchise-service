package com.pragma.jamarlesf.api;

import com.pragma.jamarlesf.api.dto.request.BranchRequest;
import com.pragma.jamarlesf.api.dto.request.UpdateBranchNameRequest;
import com.pragma.jamarlesf.model.branchmodel.BranchModel;
import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import com.pragma.jamarlesf.usecase.addbranchtofranchise.AddBranchToFranchiseUseCase;
import com.pragma.jamarlesf.usecase.updatebranchname.UpdateBranchNameUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchHandlerTest {

    @Mock
    private AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;

    @Mock
    private UpdateBranchNameUseCase updateBranchNameUseCase;

    @Mock
    private ServerRequest serverRequest;

    private BranchHandler branchHandler;

    @BeforeEach
    void setUp() {
        branchHandler = new BranchHandler(addBranchToFranchiseUseCase, updateBranchNameUseCase);
    }

    @Test
    @DisplayName("Should return HTTP 201 when branch is created successfully")
    void shouldReturn201WhenBranchIsCreatedSuccessfully() {
        BranchRequest requestDto = new BranchRequest("Sucursal Norte");
        BranchModel createdBranch = BranchModel.builder()
                .id(new BranchModelId("10"))
                .name("Sucursal Norte")
                .franchiseId(new FranchiseModelId("1"))
                .build();

        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(serverRequest.bodyToMono(BranchRequest.class)).thenReturn(Mono.just(requestDto));
        when(addBranchToFranchiseUseCase.execute(eq(new FranchiseModelId("1")), any(BranchModel.class)))
                .thenReturn(Mono.just(createdBranch));

        Mono<ServerResponse> responseMono = branchHandler.addBranch(serverRequest);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(HttpStatus.CREATED, response.statusCode());
                })
                .verifyComplete();

        verify(serverRequest).pathVariable("franchiseId");
        verify(serverRequest).bodyToMono(BranchRequest.class);
        verify(addBranchToFranchiseUseCase).execute(eq(new FranchiseModelId("1")), any(BranchModel.class));
    }

    @Test
    @DisplayName("Should return HTTP 200 when branch name is updated successfully")
    void shouldReturn200WhenBranchNameIsUpdatedSuccessfully() {
        UpdateBranchNameRequest requestDto = new UpdateBranchNameRequest("Sucursal Poblado");
        BranchModel updatedBranch = BranchModel.builder()
                .id(new BranchModelId("10"))
                .name("Sucursal Poblado")
                .franchiseId(new FranchiseModelId("1"))
                .build();

        when(serverRequest.pathVariable("branchId")).thenReturn("10");
        when(serverRequest.bodyToMono(UpdateBranchNameRequest.class)).thenReturn(Mono.just(requestDto));
        when(updateBranchNameUseCase.execute(eq(new BranchModelId("10")), eq("Sucursal Poblado")))
                .thenReturn(Mono.just(updatedBranch));

        Mono<ServerResponse> responseMono = branchHandler.updateBranchName(serverRequest);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(HttpStatus.OK, response.statusCode());
                })
                .verifyComplete();

        verify(serverRequest).pathVariable("branchId");
        verify(serverRequest).bodyToMono(UpdateBranchNameRequest.class);
        verify(updateBranchNameUseCase).execute(eq(new BranchModelId("10")), eq("Sucursal Poblado"));
    }
}
