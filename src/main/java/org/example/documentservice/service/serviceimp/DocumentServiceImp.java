package org.example.documentservice.service.serviceimp;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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

	public UUID getCurrentUser() {
		return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
	}

	private String retrieveToken() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof JwtAuthenticationToken jwtAuthToken) {
			return jwtAuthToken.getToken().getTokenValue();
		}
		return null;
	}

	public ApiResponse<UserWorkspaceResponse> getUserWorkspace(UUID userId, UUID workspaceId) {
		String tokenValue = retrieveToken();
		return workspaceClient.getUserByUserIdAndWorkspaceId("Bearer " + tokenValue, userId, workspaceId);
	}


	@Override
	public DocumentElasticEntity createDocument(DocumentRequest documentRequest) {
		ApiResponse<UserWorkspaceResponse> workspace = getUserWorkspace(getCurrentUser(), documentRequest.getWorkspaceId());
		if (workspace == null) {
			throw new NotFoundException("Workspace id not found!");
		} else {
			if (!workspace.getPayload().getIsAdmin()) {
				throw new ForbiddenException("You don't have permission to create this document!");
			}
		}
		DocumentEntity document = modelMapper.map(documentRequest, DocumentEntity.class);
		document.setDocumentId(UUID.randomUUID());
		document.setIsPrivate(true);
		document.setIsDeleted(false);
		document.setCreatedAt(LocalDateTime.now());
		document.setUpdatedAt(LocalDateTime.now());
		documentRepository.save(document);
		DocumentElasticEntity elasticDocument = modelMapper.map(document, DocumentElasticEntity.class);
		elasticDocument.setCreatedBy(getCurrentUser());
		documentElasticRepository.save(elasticDocument);
		elasticDocument.setCreatedBy(null);
		return elasticDocument;
	}

	@Override
	public List<DocumentElasticEntity> getAllDocument(Integer pageNo, Integer pageSize, SortBy sortBy, SortDirection sortDirection) {
		Sort.Direction direction = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
		String sortField = sortBy.getFieldName();
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, direction, sortField);
		Page<DocumentElasticEntity> page = documentElasticRepository.findAll(pageable);
		List<DocumentElasticEntity> lstDocs = new ArrayList<>();
		for (DocumentElasticEntity document : page.getContent()) {
			if (Boolean.FALSE.equals(document.getIsDeleted())) {
				ApiResponse<UserWorkspaceResponse> userWorkspace = getUserWorkspace(getCurrentUser(), document.getWorkspaceId());
				if (userWorkspace != null) {
					UserWorkspaceResponse userWorkspaceResponse = userWorkspace.getPayload();
					if (userWorkspaceResponse != null) {
						lstDocs.add(document);
					}
				}
			}
		}
		lstDocs.forEach(i -> i.setCreatedBy(null));
		return lstDocs;
	}


	@Override
	public DocumentElasticEntity getDocument(UUID documentId) {
		DocumentElasticEntity doc = documentElasticRepository.findById(documentId).orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found!"));
		ApiResponse<UserWorkspaceResponse> workspace = getUserWorkspace(getCurrentUser(), doc.getWorkspaceId());
		if (workspace == null) {
			if (Boolean.TRUE.equals(doc.getIsPrivate())) {
				throw new ForbiddenException("You don't have permission to access this document!");
			}
		}
		doc.setCreatedBy(null);
		return doc;
	}

	@Override
	public Void deleteDocument(List<UUID> documentId) {
		documentId.forEach(id -> {
			DocumentElasticEntity elasticDocument = documentElasticRepository.findById(id).orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found!"));
			if (!elasticDocument.getCreatedBy().equals(getCurrentUser())) {
				throw new ForbiddenException("You don't have permission to delete this document!");
			}
			if (Boolean.TRUE.equals(elasticDocument.getIsDeleted())) {
				documentRepository.deleteById(id);
				documentElasticRepository.deleteById(id);
			}
		});
		return null;
	}

	@Override
	public Optional<DocumentElasticEntity> updateDocument(UUID documentId, DocumentUpdateRequest documentUpdateRequest) {
		Optional<DocumentElasticEntity> elasticDoc = documentElasticRepository.findById(documentId);
		elasticDoc.orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found!"));
		if (!elasticDoc.get().getCreatedBy().equals(getCurrentUser())) {
			throw new ForbiddenException("You don't have permission to update this document!");
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
		elasticDoc.get().setCreatedBy(null);
		return elasticDoc;
	}


	@Override
	public Void updateStatusDocument(UUID documentId) {
		DocumentElasticEntity existElasticDoc = documentElasticRepository.findById(documentId).orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found!"));
		if (!existElasticDoc.getCreatedBy().equals(getCurrentUser())) {
			throw new ForbiddenException("You don't have permission to update this document!");
		}
		DocumentEntity existDoc = documentRepository.findById(documentId).orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found!"));
		if (Boolean.TRUE.equals(existElasticDoc.getIsPrivate())) {
			existElasticDoc.setIsPrivate(false);
			existDoc.setIsPrivate(false);
		} else {
			existElasticDoc.setIsPrivate(true);
			existDoc.setIsPrivate(true);
		}
		existElasticDoc.setUpdatedAt(LocalDateTime.now());
		existDoc.setUpdatedAt(LocalDateTime.now());
		documentRepository.save(existDoc);
		documentElasticRepository.save(existElasticDoc);
		return null;
	}

	@Override
	public Void updateStatusDelete(List<UUID> documentId) {
		documentId.forEach(id -> {
			DocumentElasticEntity existElasticDoc = documentElasticRepository.findById(id).orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found"));
			if (!existElasticDoc.getCreatedBy().equals(getCurrentUser())) {
				throw new ForbiddenException("You don't have permission to update this document!");
			}
			DocumentEntity existDoc = documentRepository.findById(id).orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found"));
			if (Boolean.FALSE.equals(existElasticDoc.getIsDeleted())) {
				existElasticDoc.setIsDeleted(true);
				existDoc.setIsDeleted(true);
			} else {
				existElasticDoc.setIsDeleted(false);
				existDoc.setIsDeleted(false);
			}
			existElasticDoc.setUpdatedAt(LocalDateTime.now());
			existDoc.setUpdatedAt(LocalDateTime.now());
			documentElasticRepository.save(existElasticDoc);
			documentRepository.save(existDoc);
		});
		return null;
	}

	@Override
	public List<DocumentElasticEntity> getAllDocumentByWorkspaceId(UUID workspaceId) {
		List<DocumentElasticEntity> lstDocs = documentElasticRepository.findAllByWorkspaceId(workspaceId);
		List<DocumentElasticEntity> docs = new ArrayList<>();
		for (DocumentElasticEntity document : lstDocs) {
			ApiResponse<UserWorkspaceResponse> workspace = getUserWorkspace(getCurrentUser(), document.getWorkspaceId());
			if (workspace == null) {
				throw new ForbiddenException("You don't have permission to access this document!");
			}
			if (workspace.getPayload() != null) {
				docs.add(document);
			}
		}
		docs.forEach(i -> i.setCreatedBy(null));
		return docs;
	}

	@Override
	public Void deleteDocumentByWorkspaceId(UUID workspaceId) {
		List<DocumentElasticEntity> lstDoc = documentElasticRepository.findAllByWorkspaceId(workspaceId);
		if (lstDoc.isEmpty()) {
			throw new NotFoundException("Workspace id " + workspaceId + " not found!");
		}
		if (!lstDoc.getFirst().getCreatedBy().equals(getCurrentUser())) {
			throw new ForbiddenException("You don't have permission to delete this document!");
		}
		documentRepository.deleteByWorkspaceId(workspaceId);
		documentElasticRepository.deleteByWorkspaceId(workspaceId);
		return null;
	}

	@Override
	public List<DocumentElasticEntity> getAllTrashDocument(Integer pageNo, Integer pageSize, SortBy sortBy, SortDirection sortDirection) {
		Sort.Direction direction = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
		String sortField = sortBy.getFieldName();
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(direction, sortField));
		Page<DocumentElasticEntity> page = documentElasticRepository.findAll(pageable);
		List<DocumentElasticEntity> docs = new ArrayList<>();
		for (DocumentElasticEntity document : page.getContent()) {
			if (Boolean.TRUE.equals(document.getIsDeleted())) {
				if (document.getCreatedBy().equals(getCurrentUser())) {
					docs.add(document);
				}
			}
		}
		docs.forEach(i -> i.setCreatedBy(null));
		return docs;
	}
}
