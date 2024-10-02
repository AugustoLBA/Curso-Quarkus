package io.github.augustolba.quarkussocial.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePostRequest {

    @NotBlank(message = "O texto do post é obrigatorio")
    private String text;
}
