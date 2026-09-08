package com.pragma.jamarlesf.usecase.getbranchbyid;

import com.pragma.jamarlesf.model.branchmodel.BranchModel;
import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.branchmodel.gateways.BranchModelRepository;
import com.pragma.jamarlesf.model.constant.ErrorMessageConstants;
import com.pragma.jamarlesf.model.exception.BranchNotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetBranchByIdUseCase {

    private final BranchModelRepository branchModelRepository;

    public Mono<BranchModel> execute(BranchModelId id) {
        return branchModelRepository.findById(id)
                .switchIfEmpty(Mono.error(new BranchNotFoundException(id != null ? id.value() : ErrorMessageConstants.NULL_ID_VALUE)));
    }
}
