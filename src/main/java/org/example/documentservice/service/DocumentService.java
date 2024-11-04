package org.example.documentservice.service;

import org.example.documentservice.model.entity.DocumentElasticEntity;
import org.example.documentservice.model.enums.SortBy;
import org.example.documentservice.model.enums.SortDirection;
import org.example.documentservice.model.request.DocumentRequest;
import org.example.documentservice.model.request.DocumentUpdateRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentService {
    Object createDocument(DocumentRequest documentRequest);
    List<DocumentElasticEntity> getAllDocument(Integer pageNo, Integer pageSize, SortBy sortBy, SortDirection sortDirection);
    DocumentElasticEntity getDocument(UUID documentId);
    Void deleteDocument(List<UUID> documentId);
    Optional<DocumentElasticEntity> updateDocument(UUID documentId, DocumentUpdateRequest documentUpdateRequest);
    Void updateStatusDocument(List<UUID> documentId);
    Void updateStatusDelete(List<UUID> documentId);
    List<DocumentElasticEntity> getAllDocumentByWorkspaceId(UUID workspaceId);
    Void deleteDocumentByWorkspaceId(UUID workspaceId);
    List<DocumentElasticEntity> getAllTrashDocument(Integer pageNo, Integer pageSize, SortBy sortBy, SortDirection sortDirection);

}
