package com.pragma.jamarlesf.api.dto.request;

import lombok.Builder;

@Builder
public record UpdateProductStockRequest(Integer stock) {
}
