package com.pragma.jamarlesf.model.productmodel;

import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class ProductModel {
    private ProductModelId id;
    private String name;
    private Integer stock;
    private BranchModelId branchId;
}
