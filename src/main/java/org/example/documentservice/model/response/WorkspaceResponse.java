package org.example.documentservice.model.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WorkspaceResponse {
    private UUID workspaceId;
    private String workspaceName;
    private Boolean isPrivate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<UserResponse> users;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}