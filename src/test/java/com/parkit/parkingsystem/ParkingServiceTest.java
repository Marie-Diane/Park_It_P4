package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ClockService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;


    @BeforeEach
    private void setUpPerTest() {
        try {
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, new ClockService());
            
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    /**
     * This test check that updateParking() is called once
     * 
     * @throws Exception
     */
    @Test
    public void processExitingVehicleTestWithTicketExisting() throws Exception{
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        Ticket ticket = mockTicket();
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        parkingService.processExitingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }

    /**
     * This test check that updateParking() is not called if the ticket has not been updated
     * 
     * @throws Exception
     */
    @Test
    public void processExitingVehicleTestWithTicketNotUpdating() throws Exception{
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        Ticket ticket = mockTicket();
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
        parkingService.processExitingVehicle();
        verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
    }

    /**
     * This test check that updateParking() not called if ticket doesn't exist
     * 
     * @throws Exception
     */
    @Test
    public void processExitingVehicleTestIfTicketDoesntExist() throws Exception{
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(ticketDAO.getTicket(anyString())).thenReturn(null);
        parkingService.processExitingVehicle();
        verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
    }

    /**
     * This test check that return correct parking spot
     */
    @Test
    public void getNextParkingNumberIfAvailableTest(){
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(2);
        assertEquals(new ParkingSpot(2, ParkingType.CAR, true), parkingService.getNextParkingNumberIfAvailable());
    }

    /**
     * This test checks that parkingSpot is null if there is no space available.
     */
    @Test
    public void getNextParkingNumberIfAvailableTestIfNoSpaceAvailable(){
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(0);
        assertEquals(null, parkingService.getNextParkingNumberIfAvailable());
    }

     /**
     * This test checks that parkingSpot is null if parkingType is not correct
     */
    @Test
    public void getNextParkingNumberIfAvailableTestIfParkingTypeNotCorrect(){
        when(inputReaderUtil.readSelection()).thenReturn(0);
        assertEquals(null, parkingService.getNextParkingNumberIfAvailable());
    }

    /**
     * This test check that saveTicket is called once
     * 
     * @throws Exception
     */
    @Test
    public void processIncomingVehicleTest() throws Exception{
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        when(ticketDAO.getTicket(anyString())).thenReturn(mockTicket());
        when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
        parkingService.processIncomingVehicle();
        verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
    }

    /**
     * This test check that saveTicket is not called if parkingSpot is null
     */
    @Test
    public void processIncomingVehicleTestKO(){
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(0);
        parkingService.processIncomingVehicle();
        verify(ticketDAO, Mockito.times(0)).saveTicket(any(Ticket.class));
    }

    private Ticket mockTicket() {
        Ticket ticket = new Ticket();
        ticket.setInTime(LocalDateTime.now().plusMinutes(-60));
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setDiscount(true);

        return ticket;
    }
}
