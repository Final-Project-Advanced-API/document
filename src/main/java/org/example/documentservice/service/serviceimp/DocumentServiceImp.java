package org.example.documentservice.service.serviceimp;

import org.example.documentservice.exception.NotFoundException;
import org.example.documentservice.model.entity.DocumentElasticEntity;
import org.example.documentservice.model.entity.DocumentEntity;
import org.example.documentservice.model.request.DocumentRequest;
import org.example.documentservice.repository.DocumentElasticRepository;
import org.example.documentservice.repository.DocumentRepository;
import org.example.documentservice.service.DocumentService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DocumentServiceImp implements DocumentService {
    private final DocumentRepository documentRepository;
    private final DocumentElasticRepository documentElasticRepository;
    private final ModelMapper modelMapper;

    public DocumentServiceImp(DocumentRepository documentRepository, DocumentElasticRepository documentElasticRepository, ModelMapper modelMapper) {
        this.documentRepository = documentRepository;
        this.documentElasticRepository = documentElasticRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public DocumentElasticEntity createDocument(DocumentRequest documentRequest) {
        DocumentEntity doc = modelMapper.map(documentRequest, DocumentEntity.class);
        doc.setDocumentId(UUID.randomUUID());
        doc.setCreatedAt(LocalDateTime.now());
        doc.setUpdatedAt(LocalDateTime.now());
        DocumentEntity saveDoc = documentRepository.save(doc);
        // Save to Elasticsearch
        DocumentElasticEntity elasticDoc = modelMapper.map(saveDoc, DocumentElasticEntity.class);
        elasticDoc.setCreatedAt(LocalDate.from(doc.getCreatedAt()));
        elasticDoc.setUpdatedAt(LocalDate.from(doc.getUpdatedAt()));
        documentElasticRepository.save(elasticDoc);
        return elasticDoc;
    }

    @Override
    public List<DocumentElasticEntity> getAllDocument() {
        Iterable<DocumentElasticEntity> elasticDoc = documentElasticRepository.findAll();
        List<DocumentElasticEntity> elasticEntityList = new ArrayList<>();
        elasticDoc.forEach(elasticEntityList::add);
        if (elasticEntityList.isEmpty()) {
            return Collections.emptyList();
        }
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
        existDocument.setUpdatedAt(LocalDate.now());
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
        existElasticDoc.setUpdatedAt(LocalDate.now());
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
        existElasticDoc.setUpdatedAt(LocalDate.now());
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
