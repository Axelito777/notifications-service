package cl.duoc.pedidos360.notifications_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
public class NotifyRequest {
    @NotBlank @Email
    private String email;
    @NotBlank
    private String subject;
    @NotBlank
    private String message;
}