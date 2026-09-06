package com.pragma.jamarlesf.api;

import com.pragma.jamarlesf.api.dto.request.FranchiseRequest;
import com.pragma.jamarlesf.api.dto.response.FranchiseResponse;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModel;
import com.pragma.jamarlesf.usecase.createfranchise.CreateFranchiseUseCase;
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

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(FranchiseRequest.class)
                .map(req -> FranchiseModel.builder()
                        .name(req.name())
                        .build())
                .flatMap(createFranchiseUseCase::execute)
                .map(franchise -> new FranchiseResponse(
                        franchise.getId() != null ? franchise.getId().value() : null,
                        franchise.getName()
                ))
                .flatMap(response -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(response));
    }
}
