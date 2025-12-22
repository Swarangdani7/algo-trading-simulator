package com.swarang.simulation_service.api;

import com.swarang.simulation_service.application.SimulationService;
import com.swarang.simulation_service.domain.SimulationRun;
import com.swarang.simulation_service.dto.StartRequest;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/simulation")
@RequiredArgsConstructor
public class SimulationController {

    private final SimulationService simulationService;

    @PostMapping("/start")
    public SimulationRun simulationStart(@RequestBody StartRequest request, Authentication authentication) {
        String userId = "dummyUser@123"; //= authentication.getName(); Will add during auth service creation
        return simulationService.startSimulation(userId, request.allocations());
    }

    @PostMapping("/stop/{runId}")
    public void simulationStop(@PathVariable String runId) {
        simulationService.stopSimulation(runId);
    }
}
