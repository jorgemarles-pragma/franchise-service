package com.pragma.jamarlesf.r2dbc.product;

import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.productmodel.ProductModel;
import com.pragma.jamarlesf.model.productmodel.ProductModelId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", source = "id", qualifiedByName = "toDataId")
    @Mapping(target = "branchId", source = "branchId", qualifiedByName = "toBranchDataId")
    ProductData toData(ProductModel model);

    @Mapping(target = "id", source = "id", qualifiedByName = "toModelId")
    @Mapping(target = "branchId", source = "branchId", qualifiedByName = "toBranchModelId")
    ProductModel toModel(ProductData data);

    @Named("toDataId")
    default Long toDataId(ProductModelId id) {
        if (id == null || id.value() == null || id.value().isBlank()) {
            return null;
        }
        return Long.valueOf(id.value());
    }

    @Named("toModelId")
    default ProductModelId toModelId(Long id) {
        if (id == null) {
            return null;
        }
        return new ProductModelId(String.valueOf(id));
    }

    @Named("toBranchDataId")
    default Long toBranchDataId(BranchModelId branchId) {
        if (branchId == null || branchId.value() == null || branchId.value().isBlank()) {
            return null;
        }
        return Long.valueOf(branchId.value());
    }

    @Named("toBranchModelId")
    default BranchModelId toBranchModelId(Long branchId) {
        if (branchId == null) {
            return null;
        }
        return new BranchModelId(String.valueOf(branchId));
    }
}
