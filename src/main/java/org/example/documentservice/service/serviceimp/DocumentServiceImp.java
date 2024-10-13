package org.example.documentservice.service.serviceimp;

import lombok.AllArgsConstructor;
import org.example.documentservice.exception.NotFoundException;
import org.example.documentservice.model.entity.DocumentEntity;
import org.example.documentservice.model.request.Contents;

import org.example.documentservice.model.request.ContentsRequest;
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
//        AtomicInteger contentId = new AtomicInteger(1);
        DocumentEntity documentEntity = modelMapper.map(documentRequest, DocumentEntity.class);
        documentEntity.setDocumentId(UUID.randomUUID());
        documentEntity.setCreatedAt(LocalDateTime.now());
        documentEntity.setUpdatedAt(LocalDateTime.now());
//        documentEntity.getContents().forEach(content -> content.setContentId(contentId.getAndIncrement()));
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
        for(ContentsRequest contentsRequest : documentRequest.getContentsRequests()){
            boolean contentExists = false;
            for(Contents existContent : documentEntity.getContents()){
                // update content
                if(existContent.getContentId().equals(contentsRequest.getContentId())){
                    existContent.setContentType(contentsRequest.getContentType());
                    existContent.setProps(contentsRequest.getProps());
                    existContent.setBlockContents(contentsRequest.getBlockContents());
                    existContent.setChildren(contentsRequest.getChildren());
                    contentExists = true;
                    break;
                }
            }
            // Add new content
            if(!contentExists){
                Contents newContent = new Contents();
                newContent.setContentId(contentsRequest.getContentId());
                newContent.setContentType(contentsRequest.getContentType());
                newContent.setProps(contentsRequest.getProps());
                newContent.setBlockContents(contentsRequest.getBlockContents());
                newContent.setChildren(contentsRequest.getChildren());
                documentEntity.getContents().add(newContent);
            }
        }
        return documentRepository.save(modelMapper.map(documentEntity, DocumentEntity.class));
    }

    @Override
    public DocumentEntity updateStatusDocument(UUID documentId, Boolean isPrivate) {
        DocumentEntity documentEntity = documentRepository.findById(documentId).orElseThrow(() -> new NotFoundException("Document id " + documentId + " not found"));
        documentEntity.setIsPrivate(isPrivate);
        documentEntity.setUpdatedAt(LocalDateTime.now());
        documentRepository.save(documentEntity);
        return documentEntity;
    }


}
