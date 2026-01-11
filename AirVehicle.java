package assignment.Vehicles;
import assignment.Exceptions.InvalidOperationException;

public abstract class AirVehicle extends Vehicle{
    private double maxAltitude;
    public AirVehicle(String id, String model, double maxSpeed, int numWheels) throws InvalidOperationException{
        super(id, model, maxSpeed);
        this.maxAltitude = 10000.0;
    }
    @Override
	public String getSummary() {
	    return super.getSummary() + ", Max Altitude - " + maxAltitude;
	}
    @Override
    public double estimateJourneyTime(double distance){
        double base = distance/getMaxSpeed();
        return base*0.95;
    }
}