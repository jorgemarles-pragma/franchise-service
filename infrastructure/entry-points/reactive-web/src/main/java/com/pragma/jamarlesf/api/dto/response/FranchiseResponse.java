package com.pragma.jamarlesf.api.dto.response;

import lombok.Builder;

@Builder(toBuilder = true)
public record FranchiseResponse(String id, String name) {
}
