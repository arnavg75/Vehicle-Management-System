package assignment.Vehicles;

import assignment.Exceptions.InsufficientFuelException;
import assignment.Exceptions.InvalidOperationException;
import assignment.Exceptions.OverloadException;
import assignment.Interfaces.CargoCarrier;
import assignment.Interfaces.FuelConsumable;
import assignment.Interfaces.Maintainable;

public class CargoShip extends WaterVehicle implements CargoCarrier, FuelConsumable, Maintainable{
    private final double cargoCapacity = 50000;
    private double currentCargo = 0;
    private boolean maintenanceNeeded = false;
    private double fuelLevel = 0;
    
    public CargoShip(String id, String model, double maxSpeed, boolean hasSail) throws InvalidOperationException{
        super(id, model, maxSpeed, hasSail);
    }
    @Override
    public void loadCargo(double weight) throws OverloadException{
        if(currentCargo+weight>cargoCapacity) throw new OverloadException("too much weight");
        currentCargo+=weight;
    }
    @Override
    public void unloadCargo(double weight) throws InvalidOperationException{
        if(weight>currentCargo) throw new InvalidOperationException("Not enough cargo for unloading");
        currentCargo-=weight;
    }
    @Override
    public double getCargoCapacity(){
        return cargoCapacity;
    }
    public double getCurrentCargo(){
        return currentCargo;
    }
    @Override
    public void scheduleMaintenance(){
        maintenanceNeeded = true;
    }
    @Override
    public boolean needsMaintenance(){
        if(getCurrentMileage()>10000)return true;else if(maintenanceNeeded)return true; else return false;
    }
    @Override
    public void performMaintenance(){
        maintenanceNeeded = false;
        System.out.println("Maintenance completed.");
    }

    @Override
    public double calculateFuelEfficiency(){
        if(hasSail()) return 0.0; else return 4.0;
    }
    @Override
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException{
        if(hasSail()==false){
            double fuelNeeded = distance/calculateFuelEfficiency(); 
            if(fuelNeeded>fuelLevel) throw new InsufficientFuelException("Fuel not enough");
            fuelLevel-=fuelNeeded;

        }
        updateMileage(distance);
        if (getCurrentMileage() > 10000 && !maintenanceNeeded) {
            maintenanceNeeded = true;
        }
        System.out.println("Sailing with cargo " + currentCargo + " kg for " + distance + " km.");
    }
    public void refuel(double amount) throws InvalidOperationException{
        if (hasSail()) throw new InvalidOperationException("This ship uses sail and cannot be refueled.");
        if(amount <= 0) throw new InvalidOperationException("Amount can't be negative");
        fuelLevel += amount;
    }
    public double getFuelLevel(){
        return hasSail() ? 0.0 : fuelLevel;
    }
    public double consumeFuel(double distance) throws InsufficientFuelException{
        if (hasSail()) return 0.0;
        double fuelNeeded = distance / calculateFuelEfficiency();
        if (fuelNeeded > fuelLevel) throw new InsufficientFuelException("Fuel not enough");
        fuelLevel -= fuelNeeded;
        return fuelNeeded;
    }
    @Override
	public String getSummary() {
	    return super.getSummary() + ", Cargo capacity - " + cargoCapacity + ", Current cargo - " + currentCargo + ", Maintenance needed - "
	    		+ maintenanceNeeded + ", Fuel level - " + fuelLevel;
	}
}
