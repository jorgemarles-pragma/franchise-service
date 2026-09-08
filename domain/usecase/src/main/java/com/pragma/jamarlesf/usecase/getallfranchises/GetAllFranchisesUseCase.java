package com.pragma.jamarlesf.usecase.getallfranchises;

import com.pragma.jamarlesf.model.franchisemodel.FranchiseModel;
import com.pragma.jamarlesf.model.franchisemodel.gateways.FranchiseModelRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class GetAllFranchisesUseCase {

    private final FranchiseModelRepository franchiseModelRepository;

    public Flux<FranchiseModel> execute() {
        return franchiseModelRepository.findAll();
    }
}
