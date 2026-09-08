package com.pragma.jamarlesf.usecase.updatebranchname;

import com.pragma.jamarlesf.model.branchmodel.BranchModel;
import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.branchmodel.gateways.BranchModelRepository;
import com.pragma.jamarlesf.model.constant.ErrorMessageConstants;
import com.pragma.jamarlesf.model.exception.BranchNotFoundException;
import com.pragma.jamarlesf.model.exception.InvalidBranchNameException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateBranchNameUseCase {

    private final BranchModelRepository branchModelRepository;

    public Mono<BranchModel> execute(BranchModelId branchId, String newName) {
        return Mono.justOrEmpty(newName)
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .switchIfEmpty(Mono.error(new InvalidBranchNameException()))
                .flatMap(validName -> findBranchById(branchId)
                        .map(existingBranch -> existingBranch.toBuilder()
                                .name(validName)
                                .build()))
                .flatMap(branchModelRepository::update);
    }

    private Mono<BranchModel> findBranchById(BranchModelId branchId) {
        return branchModelRepository.findById(branchId)
                .switchIfEmpty(Mono.error(new BranchNotFoundException(branchId != null ? branchId.value() : ErrorMessageConstants.NULL_ID_VALUE)));
    }
}
