package com.pragma.jamarlesf.r2dbc.branch;

import com.pragma.jamarlesf.model.branchmodel.BranchModel;
import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface BranchMapper {

    @Mapping(target = "id", source = "id", qualifiedByName = "toDataId")
    @Mapping(target = "franchiseId", source = "franchiseId", qualifiedByName = "toFranchiseDataId")
    BranchData toData(BranchModel model);

    @Mapping(target = "id", source = "id", qualifiedByName = "toModelId")
    @Mapping(target = "franchiseId", source = "franchiseId", qualifiedByName = "toFranchiseModelId")
    BranchModel toModel(BranchData data);

    @Named("toDataId")
    default Long toDataId(BranchModelId id) {
        if (id == null || id.value() == null || id.value().isBlank()) {
            return null;
        }
        return Long.valueOf(id.value());
    }

    @Named("toModelId")
    default BranchModelId toModelId(Long id) {
        if (id == null) {
            return null;
        }
        return new BranchModelId(String.valueOf(id));
    }

    @Named("toFranchiseDataId")
    default Long toFranchiseDataId(FranchiseModelId franchiseId) {
        if (franchiseId == null || franchiseId.value() == null || franchiseId.value().isBlank()) {
            return null;
        }
        return Long.valueOf(franchiseId.value());
    }

    @Named("toFranchiseModelId")
    default FranchiseModelId toFranchiseModelId(Long franchiseId) {
        if (franchiseId == null) {
            return null;
        }
        return new FranchiseModelId(String.valueOf(franchiseId));
    }
}
