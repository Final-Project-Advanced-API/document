package org.example.documentservice.model.enums;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortBy {
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt");
    private final String fieldName;
}