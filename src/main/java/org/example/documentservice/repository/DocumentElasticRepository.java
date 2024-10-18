package org.example.documentservice.repository;

import org.example.documentservice.model.entity.DocumentElasticEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentElasticRepository extends ElasticsearchRepository<DocumentElasticEntity, UUID> {
    void deleteByWorkspaceId(UUID workspaceId);
    List<DocumentElasticEntity> findAllByWorkspaceId(UUID workspaceId);
}
