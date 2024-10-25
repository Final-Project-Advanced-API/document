package org.example.documentservice.client;
import org.example.documentservice.model.response.ApiResponse;
import org.example.documentservice.model.response.UserWorkspaceResponse;
import org.example.documentservice.model.response.WorkspaceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "workspace-service",url = "localhost:8084")
public interface WorkspaceClient {
    @GetMapping("/api/v1/workspaces/{workspaceId}")
    ApiResponse<WorkspaceResponse> getWorkspace(@PathVariable UUID workspaceId);
    @GetMapping("/api/v1/userworkspaces")
    ApiResponse<UserWorkspaceResponse> getUserByUserIdAndWorkspaceId(@RequestParam UUID userId, @RequestParam UUID workspaceId);
}
