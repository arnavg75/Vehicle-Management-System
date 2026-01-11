package assignment.Interfaces;

import assignment.Exceptions.InvalidOperationException;
import assignment.Exceptions.OverloadException;

public interface PassengerCarrier {
    void boardPassengers(int count) throws OverloadException;
    void disembarkPassengers(int count) throws InvalidOperationException;
    int getPassengerCapacity();
    int getCurrentPassengers();
}
