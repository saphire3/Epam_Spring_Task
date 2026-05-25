package com.epam.training.client;

import com.epam.training.dto.TrainerWorkloadRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "trainer-workload-service",
        fallback = WorkloadClientFallback.class
)
public interface TrainerWorkloadClient {

    @PostMapping("/api/trainer-workload")
    void updateWorkload(@RequestHeader("Authorization") String bearerToken,
                        @RequestHeader("X-Transaction-Id") String transactionId,
                        @RequestBody TrainerWorkloadRequest request);
}