package com.pragma.jamarlesf.r2dbc.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("products")
public class ProductData {
    @Id
    private Long id;
    private String name;
    private Integer stock;
    @Column("branch_id")
    private Long branchId;
}
