package org.example.documentservice.model.request;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.util.List;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Contents {
    private Integer contentId;
    private String type; // type like h1,h2,block code
    private Props props;
    private List<Content> content;
    private List<String> children;
}