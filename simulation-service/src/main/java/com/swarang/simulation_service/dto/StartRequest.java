package com.swarang.simulation_service.dto;

import java.util.Set;

public record StartRequest(
        Set<AllocationRequest> allocations
) { }
