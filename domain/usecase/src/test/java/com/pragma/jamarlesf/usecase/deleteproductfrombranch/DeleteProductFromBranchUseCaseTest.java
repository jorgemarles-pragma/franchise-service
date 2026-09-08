package com.pragma.jamarlesf.usecase.deleteproductfrombranch;

import com.pragma.jamarlesf.model.branchmodel.BranchModel;
import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.branchmodel.gateways.BranchModelRepository;
import com.pragma.jamarlesf.model.exception.BranchNotFoundException;
import com.pragma.jamarlesf.model.exception.ProductNotFoundException;
import com.pragma.jamarlesf.model.productmodel.ProductModel;
import com.pragma.jamarlesf.model.productmodel.ProductModelId;
import com.pragma.jamarlesf.model.productmodel.gateways.ProductModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteProductFromBranchUseCaseTest {

    @Mock
    private BranchModelRepository branchModelRepository;

    @Mock
    private ProductModelRepository productModelRepository;

    private DeleteProductFromBranchUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new DeleteProductFromBranchUseCase(branchModelRepository, productModelRepository);
    }

    @Test
    @DisplayName("Should delete product successfully when branch and product exist and product belongs to branch")
    void shouldDeleteProductSuccessfullyWhenBranchAndProductExistAndBelongToBranch() {
        BranchModelId branchId = new BranchModelId("10");
        ProductModelId productId = new ProductModelId("100");

        BranchModel existingBranch = BranchModel.builder()
                .id(branchId)
                .name("Sucursal Norte")
                .build();

        ProductModel existingProduct = ProductModel.builder()
                .id(productId)
                .name("Hamburguesa Doble")
                .stock(50)
                .branchId(branchId)
                .build();

        when(branchModelRepository.findById(branchId)).thenReturn(Mono.just(existingBranch));
        when(productModelRepository.findById(productId)).thenReturn(Mono.just(existingProduct));
        when(productModelRepository.deleteById(productId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(branchId, productId))
                .verifyComplete();

        verify(branchModelRepository).findById(branchId);
        verify(productModelRepository).findById(productId);
        verify(productModelRepository).deleteById(productId);
    }

    @Test
    @DisplayName("Should emit BranchNotFoundException when branch does not exist")
    void shouldEmitBranchNotFoundExceptionWhenBranchDoesNotExist() {
        BranchModelId branchId = new BranchModelId("999");
        ProductModelId productId = new ProductModelId("100");

        when(branchModelRepository.findById(branchId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(branchId, productId))
                .expectErrorMatches(throwable -> throwable instanceof BranchNotFoundException
                        && throwable.getMessage().contains("999"))
                .verify();

        verify(branchModelRepository).findById(branchId);
        verify(productModelRepository, never()).findById(any(ProductModelId.class));
        verify(productModelRepository, never()).deleteById(any(ProductModelId.class));
    }

    @Test
    @DisplayName("Should emit ProductNotFoundException when product does not exist")
    void shouldEmitProductNotFoundExceptionWhenProductDoesNotExist() {
        BranchModelId branchId = new BranchModelId("10");
        ProductModelId productId = new ProductModelId("999");

        BranchModel existingBranch = BranchModel.builder()
                .id(branchId)
                .name("Sucursal Norte")
                .build();

        when(branchModelRepository.findById(branchId)).thenReturn(Mono.just(existingBranch));
        when(productModelRepository.findById(productId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(branchId, productId))
                .expectErrorMatches(throwable -> throwable instanceof ProductNotFoundException
                        && throwable.getMessage().contains("999"))
                .verify();

        verify(branchModelRepository).findById(branchId);
        verify(productModelRepository).findById(productId);
        verify(productModelRepository, never()).deleteById(any(ProductModelId.class));
    }

    @Test
    @DisplayName("Should emit ProductNotFoundException when product belongs to another branch")
    void shouldEmitProductNotFoundExceptionWhenProductBelongsToAnotherBranch() {
        BranchModelId branchId = new BranchModelId("10");
        BranchModelId otherBranchId = new BranchModelId("20");
        ProductModelId productId = new ProductModelId("100");

        BranchModel existingBranch = BranchModel.builder()
                .id(branchId)
                .name("Sucursal Norte")
                .build();

        ProductModel productInOtherBranch = ProductModel.builder()
                .id(productId)
                .name("Hamburguesa Doble")
                .stock(50)
                .branchId(otherBranchId)
                .build();

        when(branchModelRepository.findById(branchId)).thenReturn(Mono.just(existingBranch));
        when(productModelRepository.findById(productId)).thenReturn(Mono.just(productInOtherBranch));

        StepVerifier.create(useCase.execute(branchId, productId))
                .expectErrorMatches(throwable -> throwable instanceof ProductNotFoundException
                        && throwable.getMessage().contains("does not belong to the specified branch"))
                .verify();

        verify(branchModelRepository).findById(branchId);
        verify(productModelRepository).findById(productId);
        verify(productModelRepository, never()).deleteById(any(ProductModelId.class));
    }

    @Test
    @DisplayName("Should propagate error when database fails during deletion")
    void shouldPropagateErrorWhenDatabaseFailsDuringDeletion() {
        BranchModelId branchId = new BranchModelId("10");
        ProductModelId productId = new ProductModelId("100");

        BranchModel existingBranch = BranchModel.builder()
                .id(branchId)
                .name("Sucursal Norte")
                .build();

        ProductModel existingProduct = ProductModel.builder()
                .id(productId)
                .name("Hamburguesa Doble")
                .stock(50)
                .branchId(branchId)
                .build();

        when(branchModelRepository.findById(branchId)).thenReturn(Mono.just(existingBranch));
        when(productModelRepository.findById(productId)).thenReturn(Mono.just(existingProduct));
        when(productModelRepository.deleteById(productId)).thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(useCase.execute(branchId, productId))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Database error"))
                .verify();

        verify(branchModelRepository).findById(branchId);
        verify(productModelRepository).findById(productId);
        verify(productModelRepository).deleteById(productId);
    }
}
