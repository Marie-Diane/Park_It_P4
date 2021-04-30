package com.parkit.parkingsystem.service;

import java.time.LocalDateTime;

public class ClockService {

    public LocalDateTime getCurrentDate() { 
        LocalDateTime now = LocalDateTime.now();
        return now;
    }
}
