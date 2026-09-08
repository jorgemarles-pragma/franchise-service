package com.pragma.jamarlesf.usecase.updatefranchisename;

import com.pragma.jamarlesf.model.exception.FranchiseNotFoundException;
import com.pragma.jamarlesf.model.exception.InvalidFranchiseNameException;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModel;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import com.pragma.jamarlesf.model.franchisemodel.gateways.FranchiseModelRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateFranchiseNameUseCase {

    private final FranchiseModelRepository franchiseModelRepository;

    public Mono<FranchiseModel> execute(FranchiseModelId franchiseId, String newName) {
        return Mono.justOrEmpty(newName)
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .switchIfEmpty(Mono.error(new InvalidFranchiseNameException("Franchise name cannot be empty or null")))
                .flatMap(validName -> findFranchiseById(franchiseId)
                        .map(existingFranchise -> existingFranchise.toBuilder()
                                .name(validName)
                                .build()))
                .flatMap(franchiseModelRepository::update);
    }

    private Mono<FranchiseModel> findFranchiseById(FranchiseModelId franchiseId) {
        return franchiseModelRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException(franchiseId != null ? franchiseId.value() : "null")));
    }
}
