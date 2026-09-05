package com.pragma.jamarlesf.model.productmodel;

import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
//import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ProductModel {
    ProductModelId id;
    String name;
    Integer stock;
    BranchModelId branchId;
}
