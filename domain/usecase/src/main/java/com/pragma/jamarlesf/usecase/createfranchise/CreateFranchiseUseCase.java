package com.pragma.jamarlesf.usecase.createfranchise;

import com.pragma.jamarlesf.model.franchisemodel.FranchiseModel;
import com.pragma.jamarlesf.model.franchisemodel.gateways.FranchiseModelRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateFranchiseUseCase {

    private final FranchiseModelRepository franchiseModelRepository;

    public Mono<FranchiseModel> execute(FranchiseModel franchise) {
        return franchiseModelRepository.create(franchise);
    }
}
