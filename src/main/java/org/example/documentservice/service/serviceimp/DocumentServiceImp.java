package org.example.documentservice.service.serviceimp;

import feign.FeignException;
import lombok.AllArgsConstructor;
import org.example.documentservice.client.WorkspaceClient;
import org.example.documentservice.exception.ForbiddenException;
import org.example.documentservice.exception.InternalServerException;
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

	public String getCurrentUser() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
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
		ApiResponse<UserWorkspaceResponse> workspace = getUserWorkspace(UUID.fromString(getCurrentUser()), documentRequest.getWorkspaceId());
		if (workspace == null) {
			throw new ForbiddenException("You don't have permission to access this workspace!");
		}
		if (!workspace.getPayload().getIsAdmin()) {
			throw new ForbiddenException("You are collaborator allowed to create this document!");
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
		String sortField = sortBy.getFieldName();
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize,direction,sortField);
		Page<DocumentElasticEntity> page = documentElasticRepository.findAll(pageable);
		List<DocumentElasticEntity> lstDocs = new ArrayList<>();
		for (DocumentElasticEntity document : page.getContent()) {
			if (Boolean.FALSE.equals(document.getIsDeleted())) {
				ApiResponse<UserWorkspaceResponse> userWorkspace = getUserWorkspace(currentUserId, document.getWorkspaceId());
				if (userWorkspace != null) {
					UserWorkspaceResponse userWorkspaceResponse = userWorkspace.getPayload();
					if (userWorkspaceResponse != null) {
						lstDocs.add(document);
					}
				}

			}
		}
		return lstDocs;
	}


	@Override
	public DocumentElasticEntity getDocument(UUID documentId) {
		DocumentElasticEntity doc = documentElasticRepository.findById(documentId).orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found!"));
		ApiResponse<UserWorkspaceResponse> workspace = getUserWorkspace(UUID.fromString(getCurrentUser()), doc.getWorkspaceId());
		if (workspace == null) {
			throw new ForbiddenException("You don't have permission to access this document!");
		}
		return doc;
	}

	@Override
	public Void deleteDocument(UUID documentId) {
		DocumentElasticEntity elasticDocument = documentElasticRepository.findById(documentId).orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found!"));
		ApiResponse<UserWorkspaceResponse> workspace = getUserWorkspace(UUID.fromString(getCurrentUser()), elasticDocument.getWorkspaceId());
		if (workspace == null) {
			throw new ForbiddenException("You don't have permission to access this document!");
		}
		if (!workspace.getPayload().getIsAdmin()) {
			throw new ForbiddenException("You are collaborator allowed to create this document!");
		}
		documentRepository.deleteById(documentId);
		documentElasticRepository.deleteById(documentId);
		return null;
	}

	@Override
	public Optional<DocumentElasticEntity> updateDocument(UUID documentId, DocumentUpdateRequest documentUpdateRequest) {
		Optional<DocumentElasticEntity> elasticDoc = documentElasticRepository.findById(documentId);
		elasticDoc.orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found!"));
		ApiResponse<UserWorkspaceResponse> workspace = getUserWorkspace(UUID.fromString(getCurrentUser()), elasticDoc.get().getWorkspaceId());
		if (workspace == null) {
			throw new ForbiddenException("You don't have permission to access this document!");
		}
		if (!workspace.getPayload().getIsAdmin()) {
			throw new ForbiddenException("You are collaborator allowed to update this document!");
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
		ApiResponse<UserWorkspaceResponse> workspace = getUserWorkspace(UUID.fromString(getCurrentUser()), existElasticDoc.getWorkspaceId());
		if (workspace == null) {
			throw new ForbiddenException("You don't have permission to access this document!");
		}
		if (!workspace.getPayload().getIsAdmin()) {
			throw new ForbiddenException("You are collaborator allowed to update this document!");
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
		ApiResponse<UserWorkspaceResponse> workspace = getUserWorkspace(UUID.fromString(getCurrentUser()), existElasticDoc.getWorkspaceId());
		if (workspace == null) {
			throw new ForbiddenException("You don't have permission to access this document!");
		}
		if (!workspace.getPayload().getIsAdmin()) {
			throw new ForbiddenException("You are collaborator allowed to update this document!");
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
			ApiResponse<UserWorkspaceResponse> workspace = getUserWorkspace(UUID.fromString(getCurrentUser()), document.getWorkspaceId());
			if (workspace == null) {
				throw new ForbiddenException("You don't have permission to access this document!");
			}
			if (workspace.getPayload() != null) {
				docs.add(document);
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
		ApiResponse<UserWorkspaceResponse> workspace = getUserWorkspace(UUID.fromString(getCurrentUser()), lstDoc.getFirst().getWorkspaceId());
		if (workspace == null) {
			throw new ForbiddenException("You don't have permission to access this document!");
		}
		if (!workspace.getPayload().getIsAdmin()) {
			throw new ForbiddenException("You are collaborator don't allowed to delete this document!");
		}
		documentRepository.deleteByWorkspaceId(workspaceId);
		documentElasticRepository.deleteByWorkspaceId(workspaceId);
		return null;
	}

	@Override
	public DocumentElasticEntity getPublishDocument(UUID documentId) {
		DocumentElasticEntity publishDoc = documentElasticRepository.findById(documentId).orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found!"));
		if (Boolean.TRUE.equals(publishDoc.getIsPrivate())) {
			throw new ForbiddenException("You don't have permission to access this document is private!");
		}
		return publishDoc;
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
				UserWorkspaceResponse userWorkspaceResponse;
				ApiResponse<UserWorkspaceResponse> userWorkspace = getUserWorkspace(UUID.fromString(getCurrentUser()), document.getWorkspaceId());
				if (userWorkspace != null) {
					userWorkspaceResponse = userWorkspace.getPayload();
					if (userWorkspaceResponse != null && userWorkspaceResponse.getIsAdmin().equals(true)) {
						docs.add(document);
					}
				}
			}
		}
		return docs;
	}
}
