package cl.duoc.pedidos360.notifications_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private String recipientEmail;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String errorMessage;
    private LocalDateTime sentAt;

    public enum Status { SENT, FAILED }
}
