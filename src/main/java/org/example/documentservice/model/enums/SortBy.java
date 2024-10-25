package org.example.documentservice.model.enums;
import lombok.Getter;

@Getter
public enum SortBy {
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt"),
    IS_PRIVATE("isPrivate");
    private final String fieldName;
    SortBy(String fieldName) {
        this.fieldName = fieldName;
    }
}
