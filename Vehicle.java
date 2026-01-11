package assignment.Vehicles;

import java.io.Serializable;

import assignment.Exceptions.InvalidOperationException;

public abstract class Vehicle implements Comparable<Vehicle>, Serializable{
    private static final long serialVersionUID = 1L;
    private String id;
    private String model;
    private double maxSpeed;
    private double currentMileage;
    public Vehicle(String id, String model, double maxSpeed) throws InvalidOperationException{
        
        this.id = id;
        this.model = model;
        this.maxSpeed = maxSpeed;
        this.currentMileage = 0;
    }
    public String getSummary() {
	    return getClass().getSimpleName() + ", ID - " + getId() + ", Model - " + getModel() + ", Max Speed - " 
	           + getMaxSpeed() + ", Current Mileage - " + getCurrentMileage();
	}
    public abstract void move(double distance) throws Exception;
    public abstract double calculateFuelEfficiency();
    public abstract double estimateJourneyTime(double distance) throws InvalidOperationException;
    public void displayInfo() {
        System.out.printf("Vehicle ID is %s | Model is %s | Max Speed is %.1f km/h | Mileage is %.1f km%n",
                id, model, maxSpeed, currentMileage);
    }

    public double getCurrentMileage() {
        return currentMileage;
    }
    public String getModel(){
        return model;
    }
    public String getId() {
        return id;
    }
    public double getMaxSpeed(){
        return maxSpeed;
    }
    public void updateMileage(double distance){
        this.currentMileage+=distance;
    }
    @Override
    public int compareTo(Vehicle a){
        return Double.compare(this.calculateFuelEfficiency(), a.calculateFuelEfficiency());
    }
}