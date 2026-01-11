package assignment.Vehicles;
import assignment.Interfaces.FuelConsumable;
import assignment.Interfaces.PassengerCarrier;
import assignment.Interfaces.Maintainable;
import assignment.Exceptions.InsufficientFuelException;
import assignment.Exceptions.InvalidOperationException;
import assignment.Exceptions.OverloadException;
import assignment.Interfaces.CargoCarrier;

public class Bus extends LandVehicle implements FuelConsumable, PassengerCarrier, CargoCarrier, Maintainable {
    private double fuelLevel = 0;
    private int passengerCapacity = 50;
    private int currentPassengers = 1;
    private final double cargoCapacity = 500;
    private double currentCargo = 0;
    private boolean maintenanceNeeded = false;
    
    public Bus(String id, String model, double maxSpeed, int numWheels,int passengerCapacity) throws InvalidOperationException{
        super(id,model,maxSpeed,numWheels);
        this.passengerCapacity = 50;
    }
    @Override
    public double calculateFuelEfficiency(){
        return 10;
    }
    @Override
    public void loadCargo(double weight) throws OverloadException{
        if(currentCargo + weight > cargoCapacity) throw new OverloadException("weight exceeded");
        currentCargo+=weight;    }

    @Override
    public void unloadCargo(double weight) throws InvalidOperationException{
        if(weight > currentCargo) throw new InvalidOperationException("blah blah");
        currentCargo -= weight;
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
    public void scheduleMaintenance(){
        maintenanceNeeded = true;
    }
    @Override
    public boolean needsMaintenance(){
        return getCurrentMileage() > 10000 || maintenanceNeeded;
    };
    @Override
    public void performMaintenance(){
        maintenanceNeeded = false;
        System.out.println("Maintainance done");
    }
    @Override
    public void boardPassengers(int count) throws OverloadException{
        if(passengerCapacity<currentPassengers + count) throw new OverloadException("Passengers exceeded");
        currentPassengers+=count;
    }
    @Override
    public void disembarkPassengers(int count) throws InvalidOperationException{
        if(count>currentPassengers) throw new InvalidOperationException("Will not disembark");
        currentPassengers-=count;
    }
    @Override
    public int getPassengerCapacity(){
        return passengerCapacity;
    }
    @Override
    public int getCurrentPassengers(){
        return currentPassengers;

    }
    @Override
    public void refuel (double amount) throws InvalidOperationException{
        if(amount<=0) throw new InvalidOperationException("Amount can't be negative");
        fuelLevel+=amount;
    }
    @Override
    public double getFuelLevel (){
        return fuelLevel;
    }
    @Override
	public String getSummary() {
		return super.getSummary() + ", Fuel level - " + fuelLevel + ", Passenger Capacity - " + passengerCapacity + ", Current passengers - "
				+ currentPassengers + ", Cargo Capacity - " + cargoCapacity + ", Current Cargo - " 
				+ currentCargo + ", Maintenance Needed - " + maintenanceNeeded;
	}
    @Override
    public double consumeFuel (double distance) throws InsufficientFuelException{
        double fuelNeeded = distance/calculateFuelEfficiency();
        if(fuelNeeded>fuelLevel) throw new InsufficientFuelException("Fuel not enough");
        fuelLevel -=fuelNeeded;
        return fuelNeeded;
    }
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException{
        if (distance < 0) throw new InvalidOperationException("Distance cannot be negative.");
        double fuelNeeded = distance / calculateFuelEfficiency();
        if (fuelLevel < fuelNeeded) throw new InsufficientFuelException("Not enough fuel.");
        fuelLevel -= fuelNeeded;
        if (currentPassengers < passengerCapacity) {
            currentPassengers = Math.min(passengerCapacity, currentPassengers + 1);
        }
        updateMileage(distance);
        if (getCurrentMileage() > 10000 && !maintenanceNeeded) {
            maintenanceNeeded = true;
        }
        System.out.println("Bus is moving... " + distance + " km with " + currentPassengers + " passengers and " + currentCargo + " kg cargo.");
    }
}
