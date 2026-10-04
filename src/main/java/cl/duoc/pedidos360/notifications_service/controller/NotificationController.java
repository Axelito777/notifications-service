package cl.duoc.pedidos360.notifications_service.controller;

import cl.duoc.pedidos360.notifications_service.dto.NotifyRequest;
import cl.duoc.pedidos360.notifications_service.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notify")
@RequiredArgsConstructor
public class NotificationController {

    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<String> notify(@Valid @RequestBody NotifyRequest request) {
        emailService.send(request.getEmail(), request.getSubject(), request.getMessage());
        return ResponseEntity.ok("Correo enviado a " + request.getEmail());
    }
}
