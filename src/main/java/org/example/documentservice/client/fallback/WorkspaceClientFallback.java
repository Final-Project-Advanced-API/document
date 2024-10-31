package org.example.documentservice.client.fallback;

import org.example.documentservice.client.WorkspaceClient;
import org.example.documentservice.model.response.ApiResponse;
import org.example.documentservice.model.response.UserWorkspaceResponse;
import org.springframework.stereotype.Component;
import java.util.UUID;


@Component
public class WorkspaceClientFallback implements WorkspaceClient {
	@Override
	public ApiResponse<UserWorkspaceResponse> getUserByUserIdAndWorkspaceId(String authorization, UUID userId, UUID workspaceId) {
		return null;
	}
}
