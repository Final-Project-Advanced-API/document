package org.example.documentservice.repository;
import org.example.documentservice.model.entity.DocumentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;
@Repository
public interface DocumentRepository extends MongoRepository<DocumentEntity, UUID> {
    List<DocumentEntity> findByContentsContentId(Integer contentId);
}
