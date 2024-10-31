package org.example.documentservice.client;

import org.example.documentservice.client.fallback.WorkspaceClientFallback;
import org.example.documentservice.model.response.ApiResponse;
import org.example.documentservice.model.response.UserWorkspaceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.UUID;

@FeignClient(name = "workspace-service", url = "http://localhost:8084",fallback = WorkspaceClientFallback.class)
@Primary
public interface WorkspaceClient {
	@GetMapping("/api/v1/userworkspaces")
	ApiResponse<UserWorkspaceResponse> getUserByUserIdAndWorkspaceId(@RequestHeader("Authorization") String authorization, @RequestParam UUID userId, @RequestParam UUID workspaceId);
}

