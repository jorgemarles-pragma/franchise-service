package com.pragma.jamarlesf.api;

import com.pragma.jamarlesf.api.constant.ApiTestConstants;
import com.pragma.jamarlesf.api.dto.request.FranchiseRequest;
import com.pragma.jamarlesf.api.dto.request.UpdateFranchiseNameRequest;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModel;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import com.pragma.jamarlesf.usecase.createfranchise.CreateFranchiseUseCase;
import com.pragma.jamarlesf.usecase.updatefranchisename.UpdateFranchiseNameUseCase;
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

import static com.pragma.jamarlesf.api.RouterConstants.PATH_VAR_FRANCHISE_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseHandlerTest {

    @Mock
    private CreateFranchiseUseCase createFranchiseUseCase;

    @Mock
    private UpdateFranchiseNameUseCase updateFranchiseNameUseCase;

    @Mock
    private ServerRequest serverRequest;

    private FranchiseHandler franchiseHandler;

    @BeforeEach
    void setUp() {
        franchiseHandler = new FranchiseHandler(createFranchiseUseCase, updateFranchiseNameUseCase);
    }

    @Test
    @DisplayName("Should return HTTP 201 when franchise is created successfully")
    void shouldReturn201WhenFranchiseIsCreatedSuccessfully() {
        FranchiseRequest requestDto = new FranchiseRequest(ApiTestConstants.FRANCHISE_NAME_DEFAULT);
        FranchiseModel createdFranchise = FranchiseModel.builder()
                .id(new FranchiseModelId(ApiTestConstants.ID_ONE))
                .name(ApiTestConstants.FRANCHISE_NAME_DEFAULT)
                .build();

        when(serverRequest.bodyToMono(FranchiseRequest.class)).thenReturn(Mono.just(requestDto));
        when(createFranchiseUseCase.execute(any(FranchiseModel.class))).thenReturn(Mono.just(createdFranchise));

        Mono<ServerResponse> responseMono = franchiseHandler.createFranchise(serverRequest);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(HttpStatus.CREATED, response.statusCode());
                })
                .verifyComplete();

        verify(serverRequest).bodyToMono(FranchiseRequest.class);
        verify(createFranchiseUseCase).execute(any(FranchiseModel.class));
    }

    @Test
    @DisplayName("Should return HTTP 200 when franchise name is updated successfully")
    void shouldReturn200WhenFranchiseNameIsUpdatedSuccessfully() {
        UpdateFranchiseNameRequest requestDto = new UpdateFranchiseNameRequest(ApiTestConstants.FRANCHISE_NAME_UPDATED);
        FranchiseModel updatedFranchise = FranchiseModel.builder()
                .id(new FranchiseModelId(ApiTestConstants.ID_ONE))
                .name(ApiTestConstants.FRANCHISE_NAME_UPDATED)
                .build();

        when(serverRequest.pathVariable(PATH_VAR_FRANCHISE_ID)).thenReturn(ApiTestConstants.ID_ONE);
        when(serverRequest.bodyToMono(UpdateFranchiseNameRequest.class)).thenReturn(Mono.just(requestDto));
        when(updateFranchiseNameUseCase.execute(new FranchiseModelId(ApiTestConstants.ID_ONE), ApiTestConstants.FRANCHISE_NAME_UPDATED))
                .thenReturn(Mono.just(updatedFranchise));

        Mono<ServerResponse> responseMono = franchiseHandler.updateFranchiseName(serverRequest);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(HttpStatus.OK, response.statusCode());
                })
                .verifyComplete();

        verify(serverRequest).pathVariable(PATH_VAR_FRANCHISE_ID);
        verify(serverRequest).bodyToMono(UpdateFranchiseNameRequest.class);
        verify(updateFranchiseNameUseCase).execute(new FranchiseModelId(ApiTestConstants.ID_ONE), ApiTestConstants.FRANCHISE_NAME_UPDATED);
    }
}
