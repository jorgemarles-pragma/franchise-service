package com.pragma.jamarlesf.api;

import com.pragma.jamarlesf.api.dto.request.FranchiseRequest;
import com.pragma.jamarlesf.api.dto.request.UpdateFranchiseNameRequest;
import com.pragma.jamarlesf.api.dto.response.FranchiseResponse;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModel;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import com.pragma.jamarlesf.usecase.createfranchise.CreateFranchiseUseCase;
import com.pragma.jamarlesf.usecase.updatefranchisename.UpdateFranchiseNameUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FranchiseHandler {

    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final UpdateFranchiseNameUseCase updateFranchiseNameUseCase;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(FranchiseRequest.class)
                .map(req -> FranchiseModel.builder()
                        .name(req.name())
                        .build())
                .flatMap(createFranchiseUseCase::execute)
                .map(franchise -> FranchiseResponse.builder()
                        .id(franchise.getId() != null ? franchise.getId().value() : null)
                        .name(franchise.getName())
                        .build())
                .flatMap(response -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(response));
    }

    public Mono<ServerResponse> updateFranchiseName(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        return request.bodyToMono(UpdateFranchiseNameRequest.class)
                .flatMap(req -> updateFranchiseNameUseCase.execute(new FranchiseModelId(franchiseId), req.name()))
                .map(updatedFranchise -> FranchiseResponse.builder()
                        .id(updatedFranchise.getId() != null ? updatedFranchise.getId().value() : null)
                        .name(updatedFranchise.getName())
                        .build())
                .flatMap(response -> ServerResponse
                        .ok()
                        .bodyValue(response));
    }
}
