package org.example.documentservice.service;

import org.example.documentservice.model.entity.DocumentElasticEntity;
import org.example.documentservice.model.entity.DocumentEntity;
import org.example.documentservice.model.request.DocumentRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentService {
    DocumentElasticEntity createDocument(DocumentRequest documentRequest);
    List<DocumentElasticEntity> getAllDocument();
    DocumentElasticEntity getDocument(UUID documentId);
    Void deleteDocument(List<UUID> documentId);
    Optional<DocumentElasticEntity> updateDocument(UUID documentId, DocumentRequest documentRequest);
    DocumentElasticEntity updateStatusDocument(UUID documentId, Boolean isPrivate);
    DocumentElasticEntity updateStatusDelete(UUID documentId, Boolean isDelete);
    List<DocumentElasticEntity> getAllDocumentByWorkspaceId(UUID workspaceId);
    Void deleteDocumentByWorkspaceId(UUID workspaceId);
}
