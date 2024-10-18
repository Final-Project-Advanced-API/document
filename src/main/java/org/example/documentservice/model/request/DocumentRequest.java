package org.example.documentservice.model.request;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DocumentRequest {
    private UUID workspaceId;
    private String title;
    private List<Object> contents;
    private Boolean isPrivate;
    private Boolean isDeleted;
}
