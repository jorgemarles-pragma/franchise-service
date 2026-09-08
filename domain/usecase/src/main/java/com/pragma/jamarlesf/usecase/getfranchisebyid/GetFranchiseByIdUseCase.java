package com.pragma.jamarlesf.usecase.getfranchisebyid;

import com.pragma.jamarlesf.model.exception.FranchiseNotFoundException;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModel;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import com.pragma.jamarlesf.model.franchisemodel.gateways.FranchiseModelRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetFranchiseByIdUseCase {

    private final FranchiseModelRepository franchiseModelRepository;

    public Mono<FranchiseModel> execute(FranchiseModelId id) {
        return franchiseModelRepository.findById(id)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException(id != null ? id.value() : null)));
    }
}
