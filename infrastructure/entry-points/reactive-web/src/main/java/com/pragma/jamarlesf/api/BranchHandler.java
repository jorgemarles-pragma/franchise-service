package com.pragma.jamarlesf.api;

import com.pragma.jamarlesf.api.dto.request.BranchRequest;
import com.pragma.jamarlesf.api.dto.response.BranchResponse;
import com.pragma.jamarlesf.model.branchmodel.BranchModel;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import com.pragma.jamarlesf.usecase.addbranchtofranchise.AddBranchToFranchiseUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BranchHandler {

    private final AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;

    public Mono<ServerResponse> addBranch(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        return request.bodyToMono(BranchRequest.class)
                .map(req -> BranchModel.builder()
                        .name(req.name())
                        .build())
                .flatMap(branch -> addBranchToFranchiseUseCase.execute(new FranchiseModelId(franchiseId), branch))
                .map(savedBranch -> new BranchResponse(
                        savedBranch.getId() != null ? savedBranch.getId().value() : null,
                        savedBranch.getName(),
                        savedBranch.getFranchiseId() != null ? savedBranch.getFranchiseId().value() : null
                ))
                .flatMap(response -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(response));
    }
}
