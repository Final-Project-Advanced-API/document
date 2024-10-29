package org.example.documentservice.client;
import org.example.documentservice.config.FeignClientConfig;
import org.example.documentservice.model.response.ApiResponse;
import org.example.documentservice.model.response.UserWorkspaceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.UUID;

@FeignClient(name = "workspace-service",url = "localhost:8084",configuration = FeignClientConfig.class)
public interface WorkspaceClient {
    @GetMapping("/api/v1/userworkspaces")
    ApiResponse<UserWorkspaceResponse> getUserByUserIdAndWorkspaceId(@RequestParam UUID userId, @RequestParam UUID workspaceId);
}
