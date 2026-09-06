 package com.pragma.jamarlesf.model.franchisemodel;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class FranchiseModel {
    FranchiseModelId id;
    String name;
}
