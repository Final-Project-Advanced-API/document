package org.example.documentservice.service.serviceimp;

import lombok.AllArgsConstructor;
import org.example.documentservice.exception.NotFoundException;
import org.example.documentservice.model.entity.DocumentEntity;
import org.example.documentservice.model.request.DocumentRequest;
import org.example.documentservice.repository.DocumentRepository;
import org.example.documentservice.service.DocumentService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DocumentServiceImp implements DocumentService {
    private DocumentRepository documentRepository;
    private ModelMapper modelMapper;

    @Override
    public DocumentEntity createDocument(DocumentRequest documentRequest) {
        DocumentEntity documentEntity = modelMapper.map(documentRequest, DocumentEntity.class);
        documentEntity.setDocumentId(UUID.randomUUID());
        documentEntity.setCreatedAt(LocalDateTime.now());
        documentEntity.setUpdatedAt(LocalDateTime.now());
        return documentRepository.save(documentEntity);
    }

    @Override
    public List<DocumentEntity> getAllDocument() {
        return documentRepository.findAll();
    }

    @Override
    public DocumentEntity getDocument(UUID documentId) {
        return documentRepository.findById(documentId).orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found"));
    }

    @Override
    public Void deleteDocument(UUID documentId) {
        DocumentEntity document = documentRepository.findById(documentId).orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found"));
        documentRepository.delete(document);
        return null;
    }

    @Override
    public DocumentEntity updateDocument(UUID documentId, DocumentRequest documentRequest) {
        DocumentEntity documentEntity = documentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found"));
        documentEntity.setUpdatedAt(LocalDateTime.now());
        modelMapper.map(documentRequest, documentEntity);
        return documentRepository.save(documentEntity);
    }

    @Override
    public DocumentEntity updateStatusDocument(UUID documentId, Boolean isPrivate) {
        DocumentEntity documentEntity = documentRepository.findById(documentId).orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found"));
        documentEntity.setIsPrivate(isPrivate);
        documentEntity.setUpdatedAt(LocalDateTime.now());
        documentRepository.save(documentEntity);
        return documentEntity;
    }

    @Override
    public DocumentEntity updateStatusDelete(UUID documentId, Boolean isDelete) {
        DocumentEntity documentEntity = documentRepository.findById(documentId).orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found"));
        documentEntity.setIsDeleted(isDelete);
        documentEntity.setUpdatedAt(LocalDateTime.now());
        documentRepository.save(documentEntity);
        return documentEntity;
    }

    @Override
    public List<DocumentEntity> getAllDocumentByWorkspaceId(UUID workspaceId) {
        return documentRepository.findAllByWorkspaceId(workspaceId).orElseThrow(() -> new NotFoundException("Find document by workspace id "+workspaceId+ "not found"));
    }

}
