package org.example.documentservice.model.entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Document(indexName = "documents")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DocumentElasticEntity {
    @Id
    private UUID documentId;
    @Field(type = FieldType.Keyword)
    private UUID workspaceId;
    @Field(type = FieldType.Text)
    private String title;
    @Field(type = FieldType.Object)
    private List<Object> contents;
    @Field(type = FieldType.Boolean)
    private Boolean isPrivate;
    @Field(type = FieldType.Boolean)
    private Boolean isDeleted;
    @Field(type = FieldType.Date)
    private String createdAt;
    @Field(type = FieldType.Date)
    private String updatedAt;
}

