package org.example.documentservice.model.request;
import lombok.*;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DocumentUpdateRequest {
    private String title;
    private List<Object> contents;
}
