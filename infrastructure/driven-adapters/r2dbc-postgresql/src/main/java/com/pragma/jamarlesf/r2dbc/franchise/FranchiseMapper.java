package com.pragma.jamarlesf.r2dbc.franchise;

import com.pragma.jamarlesf.model.franchisemodel.FranchiseModel;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface FranchiseMapper {

    @Mapping(target = "id", source = "id", qualifiedByName = "toDataId")
    FranchiseData toData(FranchiseModel model);

    @Mapping(target = "id", source = "id", qualifiedByName = "toModelId")
    FranchiseModel toModel(FranchiseData data);

    @Named("toDataId")
    default Long toDataId(FranchiseModelId id) {
        if (id == null || id.value() == null || id.value().isBlank()) {
            return null;
        }
        return Long.valueOf(id.value());
    }

    @Named("toModelId")
    default FranchiseModelId toModelId(Long id) {
        if (id == null) {
            return null;
        }
        return new FranchiseModelId(String.valueOf(id));
    }
}
