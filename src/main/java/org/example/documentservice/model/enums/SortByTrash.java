package org.example.documentservice.model.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortByTrash {
	CREATED_AT("createdAt"),
	UPDATED_AT("updatedAt");
	private final String fieldName;
}
