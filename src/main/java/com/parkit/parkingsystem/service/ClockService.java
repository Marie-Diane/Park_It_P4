package com.parkit.parkingsystem.service;

import java.time.LocalDateTime;


// Create this service for simplify integrations tests
public class ClockService {

    /**
     * Allow to get local date now
     *  
     * @return is the local date now
     */
    public LocalDateTime getCurrentDate() { 
        LocalDateTime now = LocalDateTime.now();
        return now;
    }
}
