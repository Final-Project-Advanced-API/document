package org.example.documentservice.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ContentsRequest {
    private Integer contentId;
    private String type; // type like h1,h2,block code
    private Props props;
    private List<Content> content;
    private List<String> children;
}
