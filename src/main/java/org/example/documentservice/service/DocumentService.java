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
    DocumentElasticEntity createDocument(DocumentRequest documentRequest);
    List<DocumentElasticEntity> getAllDocument(Integer pageNo, Integer pageSize, SortBy sortBy, SortDirection sortDirection);
    DocumentElasticEntity getDocument(UUID documentId);
    Void deleteDocument(UUID documentId);
    Optional<DocumentElasticEntity> updateDocument(UUID documentId, DocumentUpdateRequest documentUpdateRequest);
    Void updateStatusDocument(UUID documentId, Boolean isPrivate);
    Void updateStatusDelete(UUID documentId, Boolean isDelete);
    List<DocumentElasticEntity> getAllDocumentByWorkspaceId(UUID workspaceId);
    Void deleteDocumentByWorkspaceId(UUID workspaceId);
}
