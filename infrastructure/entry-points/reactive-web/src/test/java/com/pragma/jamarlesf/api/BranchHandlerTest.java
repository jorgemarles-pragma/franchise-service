package com.pragma.jamarlesf.api;

import com.pragma.jamarlesf.api.constant.ApiTestConstants;
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

import static com.pragma.jamarlesf.api.RouterConstants.PATH_VAR_BRANCH_ID;
import static com.pragma.jamarlesf.api.RouterConstants.PATH_VAR_FRANCHISE_ID;
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
        BranchRequest requestDto = new BranchRequest(ApiTestConstants.BRANCH_NAME_DEFAULT);
        BranchModel createdBranch = BranchModel.builder()
                .id(new BranchModelId(ApiTestConstants.ID_TEN))
                .name(ApiTestConstants.BRANCH_NAME_DEFAULT)
                .franchiseId(new FranchiseModelId(ApiTestConstants.ID_ONE))
                .build();

        when(serverRequest.pathVariable(PATH_VAR_FRANCHISE_ID)).thenReturn(ApiTestConstants.ID_ONE);
        when(serverRequest.bodyToMono(BranchRequest.class)).thenReturn(Mono.just(requestDto));
        when(addBranchToFranchiseUseCase.execute(eq(new FranchiseModelId(ApiTestConstants.ID_ONE)), any(BranchModel.class)))
                .thenReturn(Mono.just(createdBranch));

        Mono<ServerResponse> responseMono = branchHandler.addBranch(serverRequest);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(HttpStatus.CREATED, response.statusCode());
                })
                .verifyComplete();

        verify(serverRequest).pathVariable(PATH_VAR_FRANCHISE_ID);
        verify(serverRequest).bodyToMono(BranchRequest.class);
        verify(addBranchToFranchiseUseCase).execute(eq(new FranchiseModelId(ApiTestConstants.ID_ONE)), any(BranchModel.class));
    }

    @Test
    @DisplayName("Should return HTTP 200 when branch name is updated successfully")
    void shouldReturn200WhenBranchNameIsUpdatedSuccessfully() {
        UpdateBranchNameRequest requestDto = new UpdateBranchNameRequest(ApiTestConstants.BRANCH_NAME_UPDATED);
        BranchModel updatedBranch = BranchModel.builder()
                .id(new BranchModelId(ApiTestConstants.ID_TEN))
                .name(ApiTestConstants.BRANCH_NAME_UPDATED)
                .franchiseId(new FranchiseModelId(ApiTestConstants.ID_ONE))
                .build();

        when(serverRequest.pathVariable(PATH_VAR_BRANCH_ID)).thenReturn(ApiTestConstants.ID_TEN);
        when(serverRequest.bodyToMono(UpdateBranchNameRequest.class)).thenReturn(Mono.just(requestDto));
        when(updateBranchNameUseCase.execute(eq(new BranchModelId(ApiTestConstants.ID_TEN)), eq(ApiTestConstants.BRANCH_NAME_UPDATED)))
                .thenReturn(Mono.just(updatedBranch));

        Mono<ServerResponse> responseMono = branchHandler.updateBranchName(serverRequest);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(HttpStatus.OK, response.statusCode());
                })
                .verifyComplete();

        verify(serverRequest).pathVariable(PATH_VAR_BRANCH_ID);
        verify(serverRequest).bodyToMono(UpdateBranchNameRequest.class);
        verify(updateBranchNameUseCase).execute(eq(new BranchModelId(ApiTestConstants.ID_TEN)), eq(ApiTestConstants.BRANCH_NAME_UPDATED));
    }
}
