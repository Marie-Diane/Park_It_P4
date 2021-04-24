package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void calculateFareCar(){
        fareCalculatorService.calculateFare(commonTests(ParkingType.CAR, 60, false));
        assertEquals(Fare.CAR_RATE_PER_HOUR, ticket.getPrice());
    }

    @Test
    public void calculateFareBike(){
        fareCalculatorService.calculateFare(commonTests(ParkingType.BIKE, 60, false));
        assertEquals(Fare.BIKE_RATE_PER_HOUR, ticket.getPrice());
    }

    @Test
    public void calculateFareUnkownType(){
        assertThrows(
            NullPointerException.class, () 
            -> fareCalculatorService.calculateFare(commonTests(null, 60, false))
        );
    }

    @Test
    public void calculateFareBikeWithFutureInTime(){
        assertThrows(
            IllegalArgumentException.class, () 
            -> fareCalculatorService.calculateFare(commonTests(ParkingType.BIKE, -60, false))
        );
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
        fareCalculatorService.calculateFare(commonTests(ParkingType.BIKE, 45, false));
        double outPrice = (double) Math.round(0.75 * Fare.BIKE_RATE_PER_HOUR * 100) / 100;
        assertEquals(outPrice, ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){
        fareCalculatorService.calculateFare(commonTests(ParkingType.CAR, 45, false));
        double outPrice = (double) Math.round(0.75 * Fare.CAR_RATE_PER_HOUR * 100) / 100;
        assertEquals(outPrice, ticket.getPrice());
    }
 
    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
        fareCalculatorService.calculateFare(commonTests(ParkingType.CAR, (24*60), false));
        assertEquals(24 * Fare.CAR_RATE_PER_HOUR, ticket.getPrice());
    }

    @Test
    public void calculateFareWithLessThanThirtyMinutesParkingTime(){
        fareCalculatorService.calculateFare(commonTests(ParkingType.CAR, 30, false));
        assertEquals(0 * Fare.CAR_RATE_PER_HOUR, ticket.getPrice());
    }
    
    @Test
    public void calculateFareWith5PercentDiscountParkingTime(){
        fareCalculatorService.calculateFare(commonTests(ParkingType.CAR, 60, true));
        double outPrice = (double) Math.round(0.95 * Fare.CAR_RATE_PER_HOUR * 100) / 100;
        assertEquals(outPrice, ticket.getPrice());
    }

    @Test
    public void calculateFareWith5PercentDiscountAndLessThanOneHourParkingTime(){
        fareCalculatorService.calculateFare(commonTests(ParkingType.CAR, 45, true));
        double outPrice = (double) Math.round(0.95 * 0.75 * Fare.CAR_RATE_PER_HOUR * 100) / 100;
        assertEquals(outPrice, ticket.getPrice());
    }

    private Ticket commonTests(ParkingType parkingType, int minutes, Boolean isDiscounted) {
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (minutes * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, parkingType,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setDiscount(isDiscounted);
        return ticket;
    }
}