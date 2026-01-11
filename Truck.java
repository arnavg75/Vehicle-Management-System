package assignment.Vehicles;

import assignment.Exceptions.InsufficientFuelException;
import assignment.Exceptions.InvalidOperationException;
import assignment.Exceptions.OverloadException;
import assignment.Interfaces.CargoCarrier;
import assignment.Interfaces.FuelConsumable;
import assignment.Interfaces.Maintainable;

public class Truck extends LandVehicle implements FuelConsumable, CargoCarrier, Maintainable{
    private double fuelLevel = 0;
    private final double cargoCapacity = 5000;
    private double currentCargo = 0;
    private boolean maintenanceNeeded = false;

    public Truck (String id, String model, double maxSpeed, int numWheels) throws InvalidOperationException{
        super(id, model, maxSpeed, numWheels);
    }
    @Override
    public double calculateFuelEfficiency(){
        double efficiency = 8.0;
        if (currentCargo>cargoCapacity/2) efficiency*=0.9;
        return efficiency;
    }
    @Override
	public String getSummary() {
		return super.getSummary() + ", Cargo capacity - " + cargoCapacity + ", Current cargo - " + currentCargo + ", Maintenance needed - "
	    		+ maintenanceNeeded + ", Fuel level - " + fuelLevel;
	}
    @Override
    public double getFuelLevel(){
        return fuelLevel;
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
        System.out.println("Truck maintenance completed.");
    }
    @Override
    public void loadCargo(double weight) throws OverloadException{
        if(currentCargo + weight > cargoCapacity) throw new OverloadException("Exceeding the limit");
        currentCargo+=weight;
    }
    @Override
    public void unloadCargo(double weight) throws InvalidOperationException{
        if (weight>currentCargo) throw new InvalidOperationException("Not enought wait");
        currentCargo-=weight;
    }
    @Override
    public double getCargoCapacity(){
        return cargoCapacity;
    }
    @Override
    public double getCurrentCargo(){
        return currentCargo;
    }
    @Override
    public void refuel(double amount) throws InvalidOperationException{
        if (amount<=0) throw new InvalidOperationException("It should be positive");
        fuelLevel+=amount;
    }
    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException{
        double fuelNeeded = distance/calculateFuelEfficiency();
        if(fuelLevel<fuelNeeded) throw new InsufficientFuelException("Not enough fuel");
        fuelLevel-=fuelNeeded;
        return fuelNeeded;
    }
    @Override
    public void move(double distance) throws InsufficientFuelException{
        double fuelNeeded = distance/calculateFuelEfficiency();
        if (fuelLevel < fuelNeeded) throw new InsufficientFuelException("Not enough fuel.");
        fuelLevel -= fuelNeeded;
        updateMileage(distance);
        System.out.println("Driving..." + distance + " km");
    }
} 