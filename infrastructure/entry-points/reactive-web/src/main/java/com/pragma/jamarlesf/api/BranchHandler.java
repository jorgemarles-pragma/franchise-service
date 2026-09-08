package com.pragma.jamarlesf.api;

import com.pragma.jamarlesf.api.dto.request.BranchRequest;
import com.pragma.jamarlesf.api.dto.request.UpdateBranchNameRequest;
import com.pragma.jamarlesf.api.dto.response.BranchResponse;
import com.pragma.jamarlesf.model.branchmodel.BranchModel;
import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import com.pragma.jamarlesf.usecase.addbranchtofranchise.AddBranchToFranchiseUseCase;
import com.pragma.jamarlesf.usecase.getbranchbyid.GetBranchByIdUseCase;
import com.pragma.jamarlesf.usecase.getbranchesbyfranchise.GetBranchesByFranchiseUseCase;
import com.pragma.jamarlesf.usecase.updatebranchname.UpdateBranchNameUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.pragma.jamarlesf.api.RouterConstants.PATH_VAR_BRANCH_ID;
import static com.pragma.jamarlesf.api.RouterConstants.PATH_VAR_FRANCHISE_ID;

@Component
@RequiredArgsConstructor
public class BranchHandler {

    private final AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;
    private final UpdateBranchNameUseCase updateBranchNameUseCase;
    private final GetBranchByIdUseCase getBranchByIdUseCase;
    private final GetBranchesByFranchiseUseCase getBranchesByFranchiseUseCase;

    public Mono<ServerResponse> addBranch(ServerRequest request) {
        String franchiseId = request.pathVariable(PATH_VAR_FRANCHISE_ID);
        return request.bodyToMono(BranchRequest.class)
                .map(req -> BranchModel.builder()
                        .name(req.name())
                        .build())
                .flatMap(branch -> addBranchToFranchiseUseCase.execute(new FranchiseModelId(franchiseId), branch))
                .map(savedBranch -> BranchResponse.builder()
                        .id(savedBranch.getId() != null ? savedBranch.getId().value() : null)
                        .name(savedBranch.getName())
                        .franchiseId(savedBranch.getFranchiseId() != null ? savedBranch.getFranchiseId().value() : null)
                        .build())
                .flatMap(response -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(response));
    }

    public Mono<ServerResponse> updateBranchName(ServerRequest request) {
        String branchId = request.pathVariable(PATH_VAR_BRANCH_ID);
        return request.bodyToMono(UpdateBranchNameRequest.class)
                .flatMap(req -> updateBranchNameUseCase.execute(new BranchModelId(branchId), req.name()))
                .map(updatedBranch -> BranchResponse.builder()
                        .id(updatedBranch.getId() != null ? updatedBranch.getId().value() : null)
                        .name(updatedBranch.getName())
                        .franchiseId(updatedBranch.getFranchiseId() != null ? updatedBranch.getFranchiseId().value() : null)
                        .build())
                .flatMap(response -> ServerResponse
                        .ok()
                        .bodyValue(response));
    }

    public Mono<ServerResponse> getBranchById(ServerRequest request) {
        String branchId = request.pathVariable(PATH_VAR_BRANCH_ID);
        return getBranchByIdUseCase.execute(new BranchModelId(branchId))
                .map(branch -> BranchResponse.builder()
                        .id(branch.getId() != null ? branch.getId().value() : null)
                        .name(branch.getName())
                        .franchiseId(branch.getFranchiseId() != null ? branch.getFranchiseId().value() : null)
                        .build())
                .flatMap(response -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }

    public Mono<ServerResponse> getBranchesByFranchise(ServerRequest request) {
        String franchiseId = request.pathVariable(PATH_VAR_FRANCHISE_ID);
        Flux<BranchResponse> flux = getBranchesByFranchiseUseCase.execute(new FranchiseModelId(franchiseId))
                .map(branch -> BranchResponse.builder()
                        .id(branch.getId() != null ? branch.getId().value() : null)
                        .name(branch.getName())
                        .franchiseId(branch.getFranchiseId() != null ? branch.getFranchiseId().value() : null)
                        .build());
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(flux, BranchResponse.class);
    }
}
