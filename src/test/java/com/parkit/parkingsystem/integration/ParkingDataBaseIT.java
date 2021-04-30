package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ClockService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.Month;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;

    private final static LocalDateTime TEST_DATE = LocalDateTime.of(2021, Month.JANUARY, 1, 15, 30);

    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ClockService clockService;
    

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    public void testParkingACar(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, clockService);
        when(clockService.getCurrentDate()).thenReturn(TEST_DATE);
        parkingService.processIncomingVehicle();

        Ticket ticketTest = ticketDAO.getTicket("ABCDEF");
        assertNotNull(ticketTest);
        assertEquals("ABCDEF", ticketTest.getVehicleRegNumber());
        assertEquals(1, ticketTest.getParkingSpot().getId());
        assertEquals(ParkingType.CAR, ticketTest.getParkingSpot().getParkingType());
        //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability
    }

    @Test
    public void testParkingLotExit(){
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, clockService);
        when(clockService.getCurrentDate()).thenReturn(TEST_DATE.plusMinutes(60));
        parkingService.processExitingVehicle();
        Ticket ticketTest = ticketDAO.getTicket("ABCDEF");
        assertNotNull(ticketTest);
        assertEquals("ABCDEF", ticketTest.getVehicleRegNumber());
        assertNotNull(ticketTest.getOutTime());
        assertEquals(1.5, ticketTest.getPrice());    
        //TODO: check that the fare generated and out time are populated correctly in the database
    }
}
