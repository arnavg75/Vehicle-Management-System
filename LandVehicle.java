package assignment.Vehicles;
import assignment.Exceptions.InvalidOperationException;

public abstract class LandVehicle extends Vehicle {
    private int numWheels;
    public LandVehicle(String id, String model, double maxSpeed, int numWheels) throws InvalidOperationException {
        super(id, model, maxSpeed);
        this.numWheels = numWheels;
    
}   @Override
    public double estimateJourneyTime (double distance){
        double base = distance/getMaxSpeed();
        return base*1.1;
    }
    @Override
	public String getSummary() {
	    return super.getSummary() + ", Number of wheels - " + numWheels;
	}
}
