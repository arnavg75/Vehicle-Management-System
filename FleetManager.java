package assignment.Fleet;

import java.util.*;
import java.io.*;
import assignment.Vehicles.*;
import assignment.Exceptions.*;
import assignment.Interfaces.FuelConsumable;
import assignment.Interfaces.Maintainable;

public class FleetManager {
    private List<Vehicle> fleet = new ArrayList<>();
    private Set<String> unique = new HashSet<>();
    private TreeSet<Vehicle> vehiclebySpeed = new TreeSet<>
    ((v1, v2) -> Double.compare(v2.getMaxSpeed(), v1.getMaxSpeed()));
    
    public void addVehicle(Vehicle v) throws InvalidOperationException {
        for(Vehicle vehicle : fleet){
            if(vehicle.getId().equals(v.getId())) throw new InvalidOperationException("Vehicle with ID: " + v.getId() + " is not unique.");
        }
        fleet.add(v);                                                   
        unique.add(v.getModel());
        vehiclebySpeed.add(v);
        System.out.println("Vehicle with ID: " + v.getId() + " is added successfully.");
    }
    public void removeVehicle(String id) throws InvalidOperationException {
        Vehicle remove = null;
        for(int i = 0; i < fleet.size(); i++) {
            if(fleet.get(i).getId().equals(id)) {
                remove = fleet.get(i);
                break;
            }
        }
        if(remove == null) throw new InvalidOperationException("Vehicle with ID: " + id + " not found");
        fleet.remove(remove);
        vehiclebySpeed.remove(remove);
        boolean itstillexists = false;
        for(int i = 0; i < fleet.size(); i++) {
            if(fleet.get(i).getModel().equals(remove.getModel())) {
                itstillexists = true;
                break;
            }
        }
        if(!itstillexists) {
            unique.remove(remove.getModel());
        }
        System.out.println("Vehicle with ID: " + id + " is removed successfully");
    }    
    public void startAllJourneys(double distance){
        for(int i=0;i<fleet.size();i++){
            try{fleet.get(i).move(distance);} catch (Exception issue){                     
                System.out.println("The issue is " + issue.getMessage());
            }
        }
    }
    
    public double getTotalFuelConsumption(double distance){
        double consumeFuel = 0;
        for(int i=0;i<fleet.size();i++){
            try{consumeFuel+=((FuelConsumable) fleet.get(i)).consumeFuel(distance);}
            catch(Exception issue){
                System.out.println("vehicle with ID " + fleet.get(i).getId() + " having issue " + issue.getMessage());
            }}return consumeFuel;  
    }
    public void refuelAll(double amount) {
        for (int i=0;i<fleet.size();i++) {
            if (fleet.get(i) instanceof FuelConsumable f) {
                try {
                    f.refuel(amount);
                } catch (Exception e) {
                    System.out.println("Refuel failed for " + fleet.get(i).getId() + ", Issue : " + e.getMessage());
                }
            }
        }
    }
    public void maintainAll() {
        int maintainedCount = 0;
        for(int i = 0; i < fleet.size(); i++) {
        Vehicle v = fleet.get(i);
        if(v instanceof Maintainable) {
            Maintainable m = (Maintainable)v;
            
            if(m.needsMaintenance()) {
                m.performMaintenance();
                maintainedCount++;
                System.out.println("Maintenance done for vehicle with ID : "+v.getId());
                }
            }
        }
        System.out.println("Maintained: " + maintainedCount + " vehicles");
        
    }
    public List<Vehicle> searchByType(String typeName){
        List<Vehicle> result = new ArrayList<>();
        for(int i=0;i<fleet.size();i++){
            if(fleet.get(i).getClass().getSimpleName().equals(typeName))
            result.add(fleet.get(i));
        }
        return result;
    }
    public void sortByFuelEfficiency(){
        Collections.sort(fleet, (v1, v2) -> Double.compare(v1.calculateFuelEfficiency(), v2.calculateFuelEfficiency()));
        System.out.println("Fleet is now sorted by fuel efficiency.");
    }
    public void sortByMaxSpeed() {
        Collections.sort(fleet, (v1, v2) -> Double.compare(v2.getMaxSpeed(), v1.getMaxSpeed()));
        System.out.println("Fleet is now sorted by max speed");
    }
    public void sortByModelName() {
        Collections.sort(fleet, (v1, v2) -> v1.getModel().compareToIgnoreCase(v2.getModel()));
        System.out.println("Fleet is now sorted by model name");
    }
    public Vehicle getFastestVehicle() {
        return Collections.max(fleet, (v1, v2) -> Double.compare(v1.getMaxSpeed(), v2.getMaxSpeed()));
    }
    public Vehicle getSlowestVehicle() {
        return Collections.min(fleet, (v1, v2) -> Double.compare(v1.getMaxSpeed(), v2.getMaxSpeed()));
    }
    
    public int getUniqueModelCount(){
        return unique.size(); 
    }
    public Set<String> getUniqueModels(){
        return new TreeSet<>(unique); 
    }
    public void saveToCSV(String filename) {
        if (!filename.endsWith(".csv")) filename += ".csv";
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("Data Stored:\n");
            for (int i=0;i<fleet.size();i++) writer.write(fleet.get(i).getSummary() + "\n");
            System.out.println("Saved " + fleet.size() + " vehicles to " + filename);
        } catch (IOException e) { System.out.println("Save error: " + e.getMessage()); }
    }
    public void loadFromCSV(String filename) throws InvalidOperationException {
        if (!filename.endsWith(".csv")) filename += ".csv";
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                try { 
                    String[] parts = line.split(",");
                    String type = parts[0], id = parts[1], model = parts[2];
                    double maxSpeed = Double.parseDouble(parts[3]), mileage = Double.parseDouble(parts[4]);
                    
                    Vehicle vehicle =  switch (type) {
                        case "Car" -> new Car(id, model, maxSpeed, 4, 5);
                        case "Bus" -> new Bus(id, model, maxSpeed, 6, 50);
                        case "Truck" -> new Truck(id, model, maxSpeed, 6);
                        case "Airplane" -> new Airplane(id, model, maxSpeed, 3, 200);
                        case "CargoShip" -> new CargoShip(id, model, maxSpeed, false);
                        default -> throw new InvalidOperationException("Unknown type: " + type);
                    };
                    vehicle.updateMileage(mileage);
                    addVehicle(vehicle);
                } catch (Exception e) { 
                        System.out.println("Skipping invalid line: " + line); 
                    }
                }
            } catch (IOException e) { 
                throw new InvalidOperationException("Load error: " + e.getMessage()); 
            }
    }
    public String generateEnhancedReport() {
        StringBuilder report = new StringBuilder();
        report.append("--- ENHANCED FLEET REPORT ---\n");
        report.append("Total Vehicles: ").append(fleet.size()).append("\n");
        report.append("Unique Models: ").append(getUniqueModelCount()).append("\n");
        
        if (!fleet.isEmpty()) {
            Vehicle fastest = getFastestVehicle();
            Vehicle slowest = getSlowestVehicle();
            report.append("Fastest: ").append(fastest.getModel()).append(" (").append(fastest.getMaxSpeed()).append(" km/h)\n");
            report.append("Slowest: ").append(slowest.getModel()).append(" (").append(slowest.getMaxSpeed()).append(" km/h)\n");
            report.append("Unique Models: ").append(getUniqueModels()).append("\n");
        }
        return report.toString();
    }
    public List<Vehicle> getVehiclesNeedingMaintenance(){
        List<Vehicle> maintenance = new ArrayList<>();
        for (int i = 0; i < fleet.size(); i++) {
            Vehicle v = fleet.get(i);
            if (v instanceof Maintainable && ((Maintainable) v).needsMaintenance())
                maintenance.add(v);
        }
        return maintenance;
    }
    
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("---------------------------------------- FLEET REPORT ----------------------------------------\n");
        report.append("Total Vehicles: ").append(fleet.size()).append("\n\n");
        report.append(String.format("%-8s | %-8s | %-15s | %-10s | %-10s\n", 
            "Type", "ID", "Model", "Max Speed", "Mileage"));
        report.append("---------|----------|-----------------|------------|--------------\n");
        for (Vehicle vehicle : fleet) {
            report.append(String.format("%-8s | %-8s | %-15s | %-10s | %-10s\n",
                vehicle.getClass().getSimpleName(),
                vehicle.getId(),
                vehicle.getModel(),
                vehicle.getMaxSpeed(),
                vehicle.getCurrentMileage()));
        }
        
        report.append("----------------------------------------------------------------------------------------------\n");
        return report.toString();
    }
    public void saveToFile(String filename) { saveToCSV(filename); }
    public void loadFromFile(String filename) throws InvalidOperationException { loadFromCSV(filename); }

    public List<Vehicle> getFleet() { return fleet; }
}