package com.freecraft.freecraftbackend.manager;

import com.freecraft.freecraftbackend.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EventListenerBean {

    @Autowired
    ShopTransactionManager shopTransactionManager;

    @Autowired
    private LogService logService;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            shopTransactionManager.checkMembers(true);
            shopTransactionManager.performTransactions();
        } catch (Exception e) {
            logService.logError(e.getMessage());
        }
    }
}