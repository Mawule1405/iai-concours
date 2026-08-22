package com.taurustex.api.aspects;



import com.taurustex.api.annotations.NotifyClients;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class WebSocketNotificationAspect {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationAspect(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // S'exécute uniquement si la méthode se termine sans erreur
    @AfterReturning("@annotation(notifyClients),@@annotation(notifyClientsDashBoard)")
    public void sendNotification(NotifyClients notifyClients) {
        String topic = notifyClients.topic();

        // On envoie un signal simple "REFRESH"
        // Tu peux aussi envoyer l'objet retourné par la méthode si besoin
        this.messagingTemplate.convertAndSend(topic, "REFRESH");

        System.out.println("Notification WebSocket envoyée sur : " + topic);
    }
}