package org.example.documentservice.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
    @Min(value = 1, message = "content id must be greater than 0")
    private Integer contentId;
    @NotBlank
    private String contentType; // type like h1,h2,block code
    private Props props;
    private List<BlockContents> blockContents;
    private List<String> children;
}
