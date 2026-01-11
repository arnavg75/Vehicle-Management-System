package assignment.Vehicles;
import assignment.Exceptions.InvalidOperationException;

public abstract class WaterVehicle extends Vehicle{

    private boolean hasSail;
    public WaterVehicle(String id, String model, double maxSpeed, boolean hasSail) throws InvalidOperationException{
        super(id, model, maxSpeed);
        this.hasSail = hasSail;
    }
    @Override
    public double estimateJourneyTime(double distance){
        double base = distance/getMaxSpeed();
        return base*1.15;
    }
    public boolean hasSail(){
        return hasSail;
    }
    public String getSummary() {
	    return super.getSummary() + ", Has sailed - " + hasSail;
	}
}





                                                                                   