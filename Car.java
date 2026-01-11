package assignment.Vehicles;

import assignment.Exceptions.InsufficientFuelException;
import assignment.Exceptions.InvalidOperationException;
import assignment.Exceptions.OverloadException;
import assignment.Interfaces.FuelConsumable;
import assignment.Interfaces.Maintainable;
import assignment.Interfaces.PassengerCarrier;

public class Car extends LandVehicle implements FuelConsumable, PassengerCarrier, Maintainable {
    private double fuelLevel = 0;
    private int passengerCapacity= 5;
    private int currentPassengers = 1;
    private boolean maintenanceNeeded = false;

    public Car(String id, String model, double maxSpeed, int numWheels, int passengerCapacity) throws InvalidOperationException {
        super(id, model, maxSpeed, numWheels);
        this.passengerCapacity = 5;
    }

    @Override
    public double calculateFuelEfficiency() {
        return 15.0;
    }

    @Override
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException {
        if (distance < 0) throw new InvalidOperationException("Distance cannot be negative.");
        double fuelNeeded = distance / calculateFuelEfficiency();
        if (fuelNeeded > fuelLevel) throw new InsufficientFuelException("Not enough fuel.");
        fuelLevel -= fuelNeeded;
        if (currentPassengers < passengerCapacity) {
            currentPassengers = Math.min(passengerCapacity, currentPassengers + 1);
        }
        updateMileage(distance);
        if (getCurrentMileage() > 10000 && !maintenanceNeeded) {
            maintenanceNeeded = true;
        }
        System.out.println("Driving on road... " + distance + " km with " + currentPassengers + " passengers.");
    }
    @Override
	public String getSummary() {
		return super.getSummary() + ", Fuel level - " + fuelLevel + ", Passenger capacity - " + passengerCapacity + ", Current passengers - "
				+ currentPassengers + ", Maintenance needed - " + maintenanceNeeded;
	}
    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (amount <= 0) throw new InvalidOperationException("Refuel amount must be greater");
        fuelLevel += amount;
    }

    @Override
    public double getFuelLevel() {
        return fuelLevel;
    }

    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException {
        double fuelNeeded = distance / calculateFuelEfficiency();
        if (fuelNeeded > fuelLevel) throw new InsufficientFuelException("Not enough fuel.");
        fuelLevel -= fuelNeeded;
        return fuelNeeded;
    }

    @Override
    public void boardPassengers(int count) throws OverloadException {
        if (currentPassengers + count > passengerCapacity) throw new OverloadException("Car overload.");
        currentPassengers += count;
    }

    @Override
    public void disembarkPassengers(int count) throws InvalidOperationException {
        if (count > currentPassengers) throw new InvalidOperationException("Not enough passengers to disembark.");
        currentPassengers -= count;
    }

    @Override
    public int getPassengerCapacity() {
        return passengerCapacity;
    }

    @Override
    public int getCurrentPassengers() {
        return currentPassengers;
    }

    @Override
    public void scheduleMaintenance() {
        maintenanceNeeded = true;
    }

    @Override
    public boolean needsMaintenance() {
        return getCurrentMileage() > 10000 || maintenanceNeeded;
    }

    @Override
    public void performMaintenance() {
        maintenanceNeeded = false;
        System.out.println("Car maintenance completed.");
    }
}
