package com.pragma.jamarlesf.model.branchmodel;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
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
public class BranchModel {
    private BranchModelId id;
    private String name;
    private FranchiseModelId franchiseId;
}
