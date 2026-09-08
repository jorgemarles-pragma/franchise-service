package com.pragma.jamarlesf.api.dto.response;

import lombok.Builder;

@Builder(toBuilder = true)
public record BranchResponse(String id, String name, String franchiseId) {
}
