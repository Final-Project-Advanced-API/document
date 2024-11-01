package org.example.documentservice.model.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DocumentUpdateRequest {
    @NotBlank(message = "Title cannot be blank. Please provide a title.")
    @NotNull(message = "Title cannot be null. A valid title is required.")
    @Size(min = 1, max = 255, message = "Title length must be between 1 and 255 characters.")
    @Pattern(
            regexp = "^(?!\\s)(?!.*\\s{2}).*[A-Za-z0-9 ]*(?<!\\s)$",
            message = "Title cannot start or end with a space, and it cannot contain consecutive spaces."
    )
    private String title;
    private List<Object> contents;
}
