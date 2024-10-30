package org.example.documentservice.model.entity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Document(collection = "documents")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DocumentEntity {
    @Id
    private UUID documentId;
    private UUID workspaceId;
    private String title;
    private List<Object> contents;
    private Boolean isPrivate;
    private Boolean isDeleted;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
