package com.pragma.jamarlesf.usecase.createfranchise;

import com.pragma.jamarlesf.model.exception.InvalidFranchiseNameException;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModel;
import com.pragma.jamarlesf.model.franchisemodel.gateways.FranchiseModelRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateFranchiseUseCase {

    private final FranchiseModelRepository franchiseModelRepository;

    public Mono<FranchiseModel> execute(FranchiseModel franchise) {
        return Mono.justOrEmpty(franchise)
                .flatMap(f -> Mono.justOrEmpty(f.getName()))
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .switchIfEmpty(Mono.error(new InvalidFranchiseNameException()))
                .map(validName -> franchise.toBuilder().name(validName).build())
                .flatMap(franchiseModelRepository::create);
    }
}
