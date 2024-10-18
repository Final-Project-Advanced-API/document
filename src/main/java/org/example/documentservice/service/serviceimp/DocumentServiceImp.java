package org.example.documentservice.service.serviceimp;

import lombok.AllArgsConstructor;
import org.example.documentservice.exception.NotFoundException;
import org.example.documentservice.model.entity.DocumentElasticEntity;
import org.example.documentservice.model.entity.DocumentEntity;
import org.example.documentservice.model.request.DocumentRequest;
import org.example.documentservice.repository.DocumentElasticRepository;
import org.example.documentservice.repository.DocumentRepository;
import org.example.documentservice.service.DocumentService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class DocumentServiceImp implements DocumentService {
    private final DocumentRepository documentRepository;
    private final DocumentElasticRepository documentElasticRepository;
    private ModelMapper modelMapper;

    @Override
    public DocumentEntity createDocument(DocumentRequest documentRequest) {
        DocumentEntity doc = modelMapper.map(documentRequest, DocumentEntity.class);
        doc.setDocumentId(UUID.randomUUID());
        doc.setCreatedAt(LocalDateTime.now());
        doc.setUpdatedAt(LocalDateTime.now());
        // Save to Elasticsearch
        DocumentEntity saveDoc = documentRepository.save(doc);
        DocumentElasticEntity elasticDoc = modelMapper.map(saveDoc, DocumentElasticEntity.class);
        documentElasticRepository.save(elasticDoc);
        return saveDoc;
    }

    @Override
    public List<DocumentElasticEntity> getAllDocument() {
        Iterable<DocumentElasticEntity> elasticDoc = documentElasticRepository.findAll();
        List<DocumentElasticEntity> elasticEntityList = new ArrayList<>();
        elasticDoc.forEach(elasticEntityList::add);
        return elasticEntityList;
    }

    @Override
    public DocumentElasticEntity getDocument(UUID documentId) {
        return documentElasticRepository.findById(documentId).orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found"));
    }

    @Override
    public Void deleteDocument(List<UUID> documentId) {
        for (UUID uuid : documentId) {
            documentRepository.deleteById(uuid);
            documentElasticRepository.deleteById(uuid);
        }
        return null;
    }

    @Override
    public Optional<DocumentElasticEntity> updateDocument(UUID documentId, DocumentRequest documentRequest) {
        Optional<DocumentElasticEntity> elasticDoc = documentElasticRepository.findById(documentId);
        elasticDoc.orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found"));
        DocumentElasticEntity existDocument = elasticDoc.get();
        existDocument.setTitle(documentRequest.getTitle());
        existDocument.setIsDeleted(documentRequest.getIsDeleted());
        existDocument.setIsPrivate(documentRequest.getIsPrivate());
        existDocument.setContents(documentRequest.getContents());
        existDocument.setUpdatedAt(LocalDateTime.now().toString());
        documentElasticRepository.save(existDocument);
        DocumentEntity existDoc = documentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found"));
        existDoc.setTitle(documentRequest.getTitle());
        existDoc.setIsDeleted(documentRequest.getIsDeleted());
        existDoc.setIsPrivate(documentRequest.getIsPrivate());
        existDoc.setContents(documentRequest.getContents());
        existDoc.setUpdatedAt(LocalDateTime.now());
        documentRepository.save(existDoc);
        return elasticDoc;
    }

    @Override
    public DocumentElasticEntity updateStatusDocument(UUID documentId, Boolean isPrivate) {
        DocumentElasticEntity existElasticDoc = documentElasticRepository.findById(documentId).orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found"));
        existElasticDoc.setIsPrivate(isPrivate);
        existElasticDoc.setUpdatedAt(LocalDateTime.now().toString());
        documentElasticRepository.save(existElasticDoc);
        DocumentEntity existDoc = documentRepository.findById(documentId).orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found"));
        existDoc.setIsPrivate(isPrivate);
        existDoc.setUpdatedAt(LocalDateTime.now());
        documentRepository.save(existDoc);
        return existElasticDoc;
    }

    @Override
    public DocumentElasticEntity updateStatusDelete(UUID documentId, Boolean isDelete) {
        DocumentElasticEntity existElasticDoc = documentElasticRepository.findById(documentId).orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found"));
        existElasticDoc.setIsDeleted(isDelete);
        existElasticDoc.setUpdatedAt(LocalDateTime.now().toString());
        documentElasticRepository.save(existElasticDoc);
        DocumentEntity existDoc = documentRepository.findById(documentId).orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found"));
        existDoc.setIsDeleted(isDelete);
        existDoc.setUpdatedAt(LocalDateTime.now());
        documentRepository.save(existDoc);
        return existElasticDoc;
    }

    @Override
    public List<DocumentElasticEntity> getAllDocumentByWorkspaceId(UUID workspaceId) {
        List<DocumentElasticEntity> elasticDocs = documentElasticRepository.findAllByWorkspaceId(workspaceId);
        if (elasticDocs == null || elasticDocs.isEmpty()) {
            throw new NotFoundException("Find all document by workspace id " + workspaceId + " not found");
        }
        return elasticDocs;
    }

    @Override
    public Void deleteDocumentByWorkspaceId(UUID workspaceId) {
        List<DocumentEntity> lstDoc = documentRepository.findAllByWorkspaceId(workspaceId);
        if (lstDoc.isEmpty()) {
            throw new NotFoundException("Document by workspace id not found");
        }
        documentRepository.deleteByWorkspaceId(workspaceId);
        documentElasticRepository.deleteByWorkspaceId(workspaceId);
        return null;
    }

}
