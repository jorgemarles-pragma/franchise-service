package com.pragma.jamarlesf.usecase.gethigheststockproductsbyfranchise;

import com.pragma.jamarlesf.model.exception.FranchiseNotFoundException;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import com.pragma.jamarlesf.model.franchisemodel.gateways.FranchiseModelRepository;
import com.pragma.jamarlesf.model.productmodel.ProductModel;
import com.pragma.jamarlesf.model.productmodel.gateways.ProductModelRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetHighestStockProductsByFranchiseUseCase {

    private final FranchiseModelRepository franchiseModelRepository;
    private final ProductModelRepository productModelRepository;

    public Flux<ProductModel> execute(FranchiseModelId franchiseId) {
        return Mono.justOrEmpty(franchiseId)
                .filter(id -> id.value() != null && !id.value().isBlank())
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException(franchiseId != null ? franchiseId.value() : "null")))
                .flatMap(franchiseModelRepository::findById)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException(franchiseId != null ? franchiseId.value() : "null")))
                .flatMapMany(franchise -> productModelRepository.findHighestStockByFranchiseId(franchise.getId()));
    }
}
