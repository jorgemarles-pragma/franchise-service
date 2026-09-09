package com.pragma.jamarlesf.api.dto.response;

import lombok.Builder;

@Builder(toBuilder = true)
public record ProductResponse(String id, String name, Integer stock, String branchId) {
}
