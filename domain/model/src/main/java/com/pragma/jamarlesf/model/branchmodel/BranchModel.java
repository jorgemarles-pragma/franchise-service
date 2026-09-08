package com.pragma.jamarlesf.model.branchmodel;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class BranchModel {
    private BranchModelId id;
    private String name;
    private FranchiseModelId franchiseId;
}
