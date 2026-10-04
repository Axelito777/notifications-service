package cl.duoc.pedidos360.notifications_service.repository;


import cl.duoc.pedidos360.notifications_service.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {}
