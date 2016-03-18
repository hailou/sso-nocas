package com.itsinic.sso.listener;

import com.itsinic.sso.WebConstants;
import com.itsinic.sso.model.Ticket;
import com.itsinic.sso.utils.RecoverTicket;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by HAIOU on 2016/3/16.
 */
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(StartupListener.class);

    /**
     * 回收ticket线程池
     */
    private ScheduledExecutorService schedulePool;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        logger.info("======== SSO server start !=======");

        Map<String, Ticket> tickets = WebConstants.TICKETS;

        schedulePool = Executors.newScheduledThreadPool(1);
        schedulePool.scheduleAtFixedRate(new RecoverTicket(tickets), WebConstants.TICKETTIMEOUT * 60, 1, TimeUnit.MINUTES);
    }
}
