package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.time.temporal.*;


public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long diffMinutes = ChronoUnit.MINUTES.between(ticket.getInTime().toInstant(), ticket.getOutTime().toInstant());
        double price = 0;
        double coeff = 0;

        if (diffMinutes > 30) {
            if (diffMinutes >=60) {
                coeff = diffMinutes / 60;
            } else {
                coeff = 0.75;
            }
        }

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                price = coeff * Fare.CAR_RATE_PER_HOUR;
                break;
            }
            case BIKE: {
                price = coeff * Fare.BIKE_RATE_PER_HOUR;
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
        if (ticket.isDiscounted()) {
            price = price * 0.95;
        }

        ticket.setPrice((double) Math.round(price * 100) / 100);
    }
}