package com.borgescloud.markets.admin;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ScheduledTasks {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    
    @Scheduled(cron = "0 15 9-17 * * MON-FRI")
	public void reportCurrentTime() {
		log.info("The time is now {}", dateFormat.format(new Date()));
	}
    
}