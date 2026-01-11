package assignment.Interfaces;
import assignment.Exceptions.InvalidOperationException;
import assignment.Exceptions.InsufficientFuelException;

public interface FuelConsumable {
    void refuel (double amount) throws InvalidOperationException;
    double getFuelLevel ();
    double consumeFuel (double distance) throws InsufficientFuelException;
    
}
