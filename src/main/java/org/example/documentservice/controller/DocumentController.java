package org.example.documentservice.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.AllArgsConstructor;
import org.example.documentservice.model.request.Contents;
import org.example.documentservice.model.request.ContentsRequest;
import org.example.documentservice.model.request.DocumentRequest;
import org.example.documentservice.model.response.ApiResponse;
import org.example.documentservice.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@AllArgsConstructor
@SecurityRequirement(name = "stack-notes")
@CrossOrigin
public class DocumentController {
    private DocumentService documentService;

    @PostMapping
    @Operation(summary = "create document")
    public ResponseEntity<?> createDocument(@RequestBody DocumentRequest documentRequest) {
        ApiResponse<?> response = ApiResponse.builder()
                .message("Create document successfully")
                .payload(documentService.createDocument(documentRequest))
                .status(HttpStatus.CREATED)
                .statusCode(HttpStatus.CREATED.value())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @GetMapping
    @Operation(summary = "get all document")
    public ResponseEntity<?> getAllDocument() {
        ApiResponse<?> response = ApiResponse.builder()
                .message("Get all document successfully")
                .payload(documentService.getAllDocument())
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/{documentId}")
    @Operation(summary = "get document")
    public ResponseEntity<?> getDocument(@PathVariable UUID documentId) {
        ApiResponse<?> response = ApiResponse.builder()
                .message("Get document successfully")
                .payload(documentService.getDocument(documentId))
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{documentId}")
    @Operation(summary = "delete document")
    public ResponseEntity<?> deleteDocument(@PathVariable UUID documentId) {
        ApiResponse<?> response = ApiResponse.builder()
                .message("Delete document successfully")
                .payload(documentService.deleteDocument(documentId))
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{documentId}")
    @Operation(summary = "update document")
    public ResponseEntity<?> updateDocument(@PathVariable UUID documentId,@RequestBody DocumentRequest documentRequest) {
        ApiResponse<?> response = ApiResponse.builder()
                .message("Update document successfully")
                .payload(documentService.updateDocument(documentId,documentRequest))
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PutMapping("/{documentId}/status")
    @Operation(summary = "update status document")
    public ResponseEntity<?> updateStatusDocument(@PathVariable UUID documentId,@RequestParam Boolean isPrivate) {
        ApiResponse<?> response = ApiResponse.builder()
                .message("Update status document successfully")
                .payload(documentService.updateStatusDocument(documentId,isPrivate))
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
