package io.github.augustolba.quarkussocial.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank(message = "O nome é obrigatorio")
    private String name;

    @NotNull(message = "A idade é obrigatoria !")
    private Integer age;

}
