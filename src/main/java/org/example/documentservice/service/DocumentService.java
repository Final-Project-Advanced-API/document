package org.example.documentservice.service;

import org.example.documentservice.model.entity.DocumentEntity;
import org.example.documentservice.model.request.DocumentRequest;

import java.util.List;
import java.util.UUID;

public interface DocumentService {
    DocumentEntity createDocument(DocumentRequest documentRequest);
    List<DocumentEntity> getAllDocument();
    DocumentEntity getDocument(UUID documentId);
    Void deleteDocument(UUID documentId);
    DocumentEntity updateDocument(UUID documentId,DocumentRequest documentRequest);
    DocumentEntity updateStatusDocument(UUID documentId, Boolean isPrivate);

    DocumentEntity updateStatusDelete(UUID documentId, Boolean isDelete);

    List<DocumentEntity> getAllDocumentByWorkspaceId(UUID workspaceId);

}
