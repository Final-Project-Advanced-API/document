package org.example.documentservice.service.serviceimp;

import feign.FeignException;
import lombok.AllArgsConstructor;
import org.example.documentservice.client.WorkspaceClient;
import org.example.documentservice.exception.ForbiddenException;
import org.example.documentservice.exception.NotFoundException;
import org.example.documentservice.model.entity.DocumentElasticEntity;
import org.example.documentservice.model.entity.DocumentEntity;
import org.example.documentservice.model.enums.SortBy;
import org.example.documentservice.model.enums.SortDirection;
import org.example.documentservice.model.request.DocumentRequest;
import org.example.documentservice.model.request.DocumentUpdateRequest;
import org.example.documentservice.model.response.ApiResponse;
import org.example.documentservice.model.response.UserWorkspaceResponse;
import org.example.documentservice.repository.DocumentElasticRepository;
import org.example.documentservice.repository.DocumentRepository;
import org.example.documentservice.service.DocumentService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class DocumentServiceImp implements DocumentService {
    private final DocumentRepository documentRepository;
    private final DocumentElasticRepository documentElasticRepository;
    private final ModelMapper modelMapper;
    private final WorkspaceClient workspaceClient;

    public String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public DocumentElasticEntity createDocument(DocumentRequest documentRequest) {
        ApiResponse<UserWorkspaceResponse> workspace;
        try {
            workspace = workspaceClient.getUserByUserIdAndWorkspaceId(UUID.fromString(getCurrentUser()), documentRequest.getWorkspaceId());
            if (workspace.getPayload() == null) {
                throw new NotFoundException("Workspace id " + documentRequest.getWorkspaceId() + " not found!");
            }
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("Workspace id " + documentRequest.getWorkspaceId() + " not found!");
        }
        if (!workspace.getPayload().getIsAdmin()) {
            throw new ForbiddenException("User not allowed to create this document!");
        }

        DocumentEntity document = modelMapper.map(documentRequest, DocumentEntity.class);
        document.setDocumentId(UUID.randomUUID());
        document.setIsPrivate(true);
        document.setIsDeleted(false);
        document.setCreatedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());
        documentRepository.save(document);
        DocumentElasticEntity elasticDocument = modelMapper.map(document, DocumentElasticEntity.class);
        documentElasticRepository.save(elasticDocument);
        return elasticDocument;
    }

    @Override
    public List<DocumentElasticEntity> getAllDocument(Integer pageNo, Integer pageSize, SortBy sortBy, SortDirection sortDirection) {
        UUID currentUserId = UUID.fromString(getCurrentUser());
        Sort.Direction direction = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, direction, sortBy.getFieldName());
        Page<DocumentElasticEntity> page = documentElasticRepository.findAll(pageable);
        List<DocumentElasticEntity> lstDocs = new ArrayList<>();
        for (DocumentElasticEntity document : page.getContent()) {
            try {
                ApiResponse<UserWorkspaceResponse> userInWorkspace = workspaceClient.getUserByUserIdAndWorkspaceId(currentUserId, document.getWorkspaceId());
                if (userInWorkspace.getPayload() != null) {
                    lstDocs.add(document);
                }
                if (userInWorkspace.getPayload() == null) {
                    return Collections.emptyList();
                }
            } catch (FeignException.NotFound ignored) {
            }
        }
        return lstDocs;
    }


    @Override
    public DocumentElasticEntity getDocument(UUID documentId) {
        return documentElasticRepository.findById(documentId).orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found!"));
    }

    @Override
    public Void deleteDocument(UUID documentId) {
        DocumentElasticEntity elasticDocument = documentElasticRepository.findById(documentId).orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found!"));
        ApiResponse<UserWorkspaceResponse> workspace;
        try {
            workspace = workspaceClient.getUserByUserIdAndWorkspaceId(UUID.fromString(getCurrentUser()), elasticDocument.getWorkspaceId());
            if (workspace.getPayload() == null) {
                throw new NotFoundException("Document id " + elasticDocument.getWorkspaceId() + " not found!");
            }
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("Document id " + elasticDocument.getWorkspaceId() + " not found!");
        }
        if (!workspace.getPayload().getIsAdmin()) {
            throw new ForbiddenException("User not allowed to create this document!");
        }
        documentRepository.deleteById(documentId);
        documentElasticRepository.deleteById(documentId);
        return null;
    }

    @Override
    public Optional<DocumentElasticEntity> updateDocument(UUID documentId, DocumentUpdateRequest documentUpdateRequest) {
        Optional<DocumentElasticEntity> elasticDoc = documentElasticRepository.findById(documentId);
        elasticDoc.orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found!"));
        ApiResponse<UserWorkspaceResponse> workspace;
        try {
            workspace = workspaceClient.getUserByUserIdAndWorkspaceId(UUID.fromString(getCurrentUser()), elasticDoc.get().getWorkspaceId());
            if (workspace.getPayload() == null) {
                throw new NotFoundException("Document id " + elasticDoc.get().getDocumentId() + " not found!");
            }
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("Document id " + elasticDoc.get().getDocumentId() + " not found!");
        }
        if (!workspace.getPayload().getIsAdmin()) {
            throw new ForbiddenException("User not allowed to update this document!");
        }

        DocumentElasticEntity existDocument = elasticDoc.get();
        existDocument.setTitle(documentUpdateRequest.getTitle());
        existDocument.setContents(documentUpdateRequest.getContents());
        existDocument.setUpdatedAt(LocalDateTime.now());
        documentElasticRepository.save(existDocument);
        DocumentEntity existDoc = documentRepository.findById(documentId).orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found!"));
        existDoc.setTitle(documentUpdateRequest.getTitle());
        existDoc.setContents(documentUpdateRequest.getContents());
        existDoc.setUpdatedAt(LocalDateTime.now());
        documentRepository.save(existDoc);
        return elasticDoc;
    }

    @Override
    public Void updateStatusDocument(UUID documentId, Boolean isPrivate) {
        DocumentElasticEntity existElasticDoc = documentElasticRepository.findById(documentId).orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found!"));
        ApiResponse<UserWorkspaceResponse> workspace;
        try {
            workspace = workspaceClient.getUserByUserIdAndWorkspaceId(UUID.fromString(getCurrentUser()), existElasticDoc.getWorkspaceId());
            if (workspace.getPayload() == null) {
                throw new NotFoundException("Document id " + existElasticDoc.getDocumentId() + " not found!");
            }
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("Document id " + existElasticDoc.getDocumentId() + " not found!");
        }
        if (!workspace.getPayload().getIsAdmin()) {
            throw new ForbiddenException("User not allowed to update this document!");
        }
        existElasticDoc.setIsPrivate(isPrivate);
        existElasticDoc.setUpdatedAt(LocalDateTime.now());
        documentElasticRepository.save(existElasticDoc);
        DocumentEntity existDoc = documentRepository.findById(documentId).orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found!"));
        existDoc.setIsPrivate(isPrivate);
        existDoc.setUpdatedAt(LocalDateTime.now());
        documentRepository.save(existDoc);
        return null;
    }

    @Override
    public Void updateStatusDelete(UUID documentId, Boolean isDelete) {
        DocumentElasticEntity existElasticDoc = documentElasticRepository.findById(documentId).orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found"));
        ApiResponse<UserWorkspaceResponse> workspace;
        try {
            workspace = workspaceClient.getUserByUserIdAndWorkspaceId(UUID.fromString(getCurrentUser()), existElasticDoc.getWorkspaceId());
            if (workspace.getPayload() == null) {
                throw new NotFoundException("Document id " + existElasticDoc.getDocumentId() + " not found!");
            }
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("Document id " + existElasticDoc.getDocumentId() + " not found!");
        }
        if (!workspace.getPayload().getIsAdmin()) {
            throw new ForbiddenException("User not allowed to update this document!");
        }
        existElasticDoc.setIsDeleted(isDelete);
        existElasticDoc.setUpdatedAt(LocalDateTime.now());
        documentElasticRepository.save(existElasticDoc);
        DocumentEntity existDoc = documentRepository.findById(documentId).orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found"));
        existDoc.setIsDeleted(isDelete);
        existDoc.setUpdatedAt(LocalDateTime.now());
        documentRepository.save(existDoc);
        return null;
    }

    @Override
    public List<DocumentElasticEntity> getAllDocumentByWorkspaceId(UUID workspaceId) {
        List<DocumentElasticEntity> lstDocs = documentElasticRepository.findAllByWorkspaceId(workspaceId);
        List<DocumentElasticEntity> docs = new ArrayList<>();
        for (DocumentElasticEntity document : lstDocs) {
            try {
                ApiResponse<UserWorkspaceResponse> userInWorkspace = workspaceClient.getUserByUserIdAndWorkspaceId(UUID.fromString(getCurrentUser()), document.getWorkspaceId());
                if (userInWorkspace.getPayload() != null) {
                    docs.add(document);
                } else {
                    throw new NotFoundException("Document id " + document.getDocumentId() + " not found!");
                }
            } catch (FeignException.NotFound ignored) {
            }
        }
        return docs;
    }

    @Override
    public Void deleteDocumentByWorkspaceId(UUID workspaceId) {
        List<DocumentEntity> lstDoc = documentRepository.findAllByWorkspaceId(workspaceId);
        if (lstDoc.isEmpty()) {
            throw new NotFoundException("Workspace id " + workspaceId + " not found!");
        }
        ApiResponse<UserWorkspaceResponse> workspace;
        try {
            workspace = workspaceClient.getUserByUserIdAndWorkspaceId(UUID.fromString(getCurrentUser()), lstDoc.getFirst().getWorkspaceId());
            if (workspace.getPayload() == null) {
                throw new NotFoundException("Document id " + lstDoc.getFirst().getWorkspaceId() + " not found!");
            }
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("Document id " + lstDoc.getFirst().getWorkspaceId() + " not found!");
        }
        if (!workspace.getPayload().getIsAdmin()) {
            throw new ForbiddenException("User not allowed to delete this document!");
        }
        documentRepository.deleteByWorkspaceId(workspaceId);
        documentElasticRepository.deleteByWorkspaceId(workspaceId);
        return null;
    }


}
