package org.example.documentservice.repository;
import org.example.documentservice.model.entity.DocumentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface DocumentRepository extends MongoRepository<DocumentEntity, UUID> {
    Optional<List<DocumentEntity>> findAllByWorkspaceId(UUID workspaceId);
    Optional<List<DocumentEntity>> deleteByWorkspaceId(UUID workspaceId);
}
