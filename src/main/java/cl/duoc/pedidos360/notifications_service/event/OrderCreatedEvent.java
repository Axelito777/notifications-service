package cl.duoc.pedidos360.notifications_service.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderCreatedEvent {
    private Long orderId;
    private String usuarioId;
    private String email;
    private Double total;
}