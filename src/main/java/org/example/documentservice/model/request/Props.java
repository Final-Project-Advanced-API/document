package org.example.documentservice.model.request;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Props {  // properties
    private String textColor; // text color
    private String backgroundColor; // background color
    private String textAlignment;  // alignment
    private Integer level;  // for heading h1 h2 h3
    private String blockCode; // for block code
}

