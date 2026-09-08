package com.pragma.jamarlesf.r2dbc.product;

import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import com.pragma.jamarlesf.model.productmodel.ProductModel;
import com.pragma.jamarlesf.model.productmodel.ProductModelId;
import com.pragma.jamarlesf.model.productmodel.gateways.ProductModelRepository;
import com.pragma.jamarlesf.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ProductRepositoryAdapter
        extends ReactiveAdapterOperations<ProductModel, ProductData, Long, ProductReactiveRepository>
        implements ProductModelRepository {

    private final ProductMapper productMapper;

    public ProductRepositoryAdapter(ProductReactiveRepository repository, ObjectMapper mapper, ProductMapper productMapper) {
        super(repository, mapper, productMapper::toModel);
        this.productMapper = productMapper;
    }

    @Override
    protected ProductData toData(ProductModel model) {
        return productMapper.toData(model);
    }

    @Override
    public Mono<ProductModel> create(ProductModel product) {
        return this.save(product);
    }

    @Override
    public Mono<ProductModel> findById(ProductModelId id) {
        return Mono.justOrEmpty(id)
                .map(ProductModelId::value)
                .filter(val -> !val.isBlank())
                .map(Long::valueOf)
                .onErrorResume(NumberFormatException.class, ex -> Mono.empty())
                .flatMap(this::findById);
    }

    @Override
    public Mono<ProductModel> update(ProductModel product) {
        return this.save(product);
    }

    @Override
    public Flux<ProductModel> findHighestStockByFranchiseId(FranchiseModelId franchiseId) {
        return Mono.justOrEmpty(franchiseId)
                .map(FranchiseModelId::value)
                .filter(val -> !val.isBlank())
                .map(Long::valueOf)
                .onErrorResume(NumberFormatException.class, ex -> Mono.empty())
                .flatMapMany(repository::findHighestStockByFranchiseId)
                .map(productMapper::toModel);
    }

    @Override
    public Mono<Void> deleteById(ProductModelId id) {
        return Mono.justOrEmpty(id)
                .map(ProductModelId::value)
                .filter(val -> !val.isBlank())
                .map(Long::valueOf)
                .onErrorResume(NumberFormatException.class, ex -> Mono.empty())
                .flatMap(repository::deleteById);
    }
}

