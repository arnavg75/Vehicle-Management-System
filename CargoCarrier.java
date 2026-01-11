package assignment.Interfaces;

import assignment.Exceptions.InvalidOperationException;
import assignment.Exceptions.OverloadException;

public interface CargoCarrier {
    void loadCargo(double weight) throws OverloadException;
    void unloadCargo(double weight) throws InvalidOperationException;
    double getCargoCapacity();
    double getCurrentCargo();
}
