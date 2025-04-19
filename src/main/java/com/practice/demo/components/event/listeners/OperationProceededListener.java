package com.practice.demo.components.event.listeners;

import com.practice.demo.events.OperationProceededEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OperationProceededListener {

    @EventListener(OperationProceededEvent.class)
    public void log(OperationProceededEvent event) {
        System.out.println("[log]" + LocalDateTime.now() + " :: Reached listener with operation id = "
                + event.getOperation().getId());
    }

    @EventListener(OperationProceededEvent.class)
    public void sendEmail(OperationProceededEvent event) {

        System.out.println("[log]" + LocalDateTime.now() + " :: Sending email to: " + event.getEmail());
        //refer to email API
    }
}
