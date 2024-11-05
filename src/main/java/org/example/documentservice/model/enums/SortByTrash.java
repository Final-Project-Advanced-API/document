package org.example.documentservice.model.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortByTrash {
	TITLE("title"),
	CREATED_AT("createdAt"),
	UPDATED_AT("updatedAt");
	private final String fieldName;
}
