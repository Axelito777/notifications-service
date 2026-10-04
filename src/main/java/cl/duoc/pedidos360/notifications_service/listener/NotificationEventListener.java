package cl.duoc.pedidos360.notifications_service.listener;

import cl.duoc.pedidos360.notifications_service.config.RabbitConfig;
import cl.duoc.pedidos360.notifications_service.event.OrderCreatedEvent;
import cl.duoc.pedidos360.notifications_service.model.Notification;
import cl.duoc.pedidos360.notifications_service.repository.NotificationRepository;
import cl.duoc.pedidos360.notifications_service.service.EmailService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private static final int MAX_REINTENTOS = 3;

    private final EmailService emailService;
    private final NotificationRepository notificationRepository;

    @RabbitListener(queues = RabbitConfig.NOTIF_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void enviarNotificacion(OrderCreatedEvent event, Channel channel,
                                    @Header(AmqpHeaders.DELIVERY_TAG) long tag) {

        boolean enviado = false;
        String ultimoError = null;

        if (event.getEmail() == null) {
            log.warn("Orden {} sin email en el evento, no se puede notificar", event.getOrderId());
        } else {
            for (int intento = 1; intento <= MAX_REINTENTOS && !enviado; intento++) {
                try {
                    emailService.send(event.getEmail(),
                            "Pedido #" + event.getOrderId() + " confirmado",
                            "Tu pedido #" + event.getOrderId() + " por $" + event.getTotal() + " fue confirmado.");
                    enviado = true;
                    log.info("Correo enviado a {} (orden {})", event.getEmail(), event.getOrderId());
                } catch (Exception e) {
                    ultimoError = e.getMessage();
                    log.warn("Fallo intento {}/{} enviando correo orden {}: {}",
                            intento, MAX_REINTENTOS, event.getOrderId(), ultimoError);
                }
            }
        }

        notificationRepository.save(Notification.builder()
                .orderId(event.getOrderId())
                .recipientEmail(event.getEmail())
                .status(enviado ? Notification.Status.SENT : Notification.Status.FAILED)
                .errorMessage(enviado ? null : ultimoError)
                .sentAt(LocalDateTime.now())
                .build());

        try {
            if (enviado) {
                channel.basicAck(tag, false);
            } else {
                log.error("Orden {} sin notificar tras {} intentos, va a DLQ", event.getOrderId(), MAX_REINTENTOS);
                channel.basicNack(tag, false, false);
            }
        } catch (IOException io) {
            log.error("Error confirmando ack/nack: {}", io.getMessage());
        }
    }
}
