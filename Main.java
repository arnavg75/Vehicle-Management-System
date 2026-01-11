package assignment;

import java.util.Scanner;

import assignment.Fleet.FleetManager;
import assignment.Vehicles.Bus;
import assignment.Vehicles.Car;
import assignment.Vehicles.Truck;
import assignment.Vehicles.Airplane;
import assignment.Vehicles.CargoShip;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        FleetManager fleet = new FleetManager();

        boolean running = true;

        while (running) {
            System.out.println("MENU");
            System.out.println("1) Add Vehicle");
            System.out.println("2) Remove Vehicle");
            System.out.println("3) Start Journey");
            System.out.println("4) Refuel All");
            System.out.println("5) Perform Maintenance");
            System.out.println("6) Generate Report");
            System.out.println("7) Save Fleet");
            System.out.println("8) Load Fleet");
            System.out.println("9) Search by Type");
            System.out.println("10) List Vehicles Needing Maintenance");
            System.out.println("11) Sort by Fuel Efficiency");
            System.out.println("12) Sort by Max Speed");
            System.out.println("13) Sort by Model Name");
            System.out.println("14) Show Enhanced Report");
            System.out.println("15) Show Fastest Vehicle");
            System.out.println("16) Show Slowest Vehicle");
            System.out.println("17) Show Unique Models");
            System.out.println("18) Exit");
            System.out.println("19) Open GUI");

            int b = in.nextInt();
            in.nextLine();

            try {
                if (b == 18) {
                    System.out.println("Thank you...");
                    in.close();
                    System.exit(0);
                }
                switch (b) {
                    case 1 -> {
                        System.out.println("Type: ");
                        String type = in.nextLine();
                        System.out.print("ID: "); String id = in.nextLine();
                        System.out.print("Model: "); String model = in.nextLine();
                        System.out.print("Speed: "); double speed = in.nextDouble();
                        if (speed <= 0) {
                            System.out.println("Error: Speed must be positive!");
                            break;
                        }
                        System.out.print("No. of Wheels: "); int numberWheels = in.nextInt();
                        if (numberWheels <= 0) {
                            System.out.println("Error: Wheels must be positive!");
                            break;
                        }
                        in.nextLine();

                        if (type.equals("Bus")) {
                            System.out.print("Passenger Capacity: ");
                            int capacity = in.nextInt();
                            if (capacity > 50 || capacity < 1) {
                                System.out.println("Error: Bus capacity must be between 1-50!");
                                break;
                            }
                            fleet.addVehicle(new Bus(id, model, speed, numberWheels, capacity));
                        } else if (type.equals("Car")) {
                            System.out.print("Passenger Capacity: ");
                            int capacity = in.nextInt();
                            if (capacity > 5 || capacity < 1) {
                                System.out.println("Error: Car capacity must be between 1-5!");
                                break;
                            }
                            fleet.addVehicle(new Car(id, model, speed, numberWheels, capacity));
                        } else if (type.equals("Truck")) {
                            fleet.addVehicle(new Truck(id, model, speed, numberWheels));
                        } else if (type.equals("Airplane")) {
                            System.out.print("Passenger Capacity: ");
                            int pcap = in.nextInt();
                            if (pcap > 200 || pcap < 1) {
                                System.out.println("Error: Airplane capacity must be between 1-200!");
                                break;
                            }
                            fleet.addVehicle(new Airplane(id, model, speed, numberWheels, pcap));
                        } else if (type.equals("CargoShip")) {
                            System.out.print("Has sail (true/false): ");
                            boolean hasSail = in.nextBoolean();
                            fleet.addVehicle(new CargoShip(id, model, speed, hasSail));
                        } else {
                            System.out.println("Unknown type.");
                        }
                    }
                    case 2 -> {
                        System.out.println("Remove ID: ");
                        String id = in.nextLine();
                        fleet.removeVehicle(id);
                    }
                    case 3 -> {
                        System.out.println("Distance: ");
                        double distance = in.nextDouble();
                        fleet.startAllJourneys(distance);
                    }
                    case 4 -> {
                        System.out.println("Fuel Amount to add: ");
                        double fueltoAdd = in.nextDouble();
                        fleet.refuelAll(fueltoAdd);
                    }
                    case 5 -> fleet.maintainAll();
                    case 6 -> System.out.println(fleet.generateReport());
                    case 7 -> {
                        System.out.println("File to save: ");
                        String file = in.nextLine();
                        fleet.saveToFile(file);                        
                    }
                    case 8 -> {
                        System.out.println("File to load: ");
                        String file = in.nextLine();
                        fleet.loadFromFile(file);
                    }
                    case 9 -> {
                        System.out.println("Search type: ");
                        String type = in.nextLine();
                        var results = fleet.searchByType(type);
                        for (int i = 0; i < results.size(); i++) {
                            results.get(i).displayInfo();
                        }
                    }
                    case 10 -> {
                        var needsMaintenance = fleet.getVehiclesNeedingMaintenance();
                        if (needsMaintenance.isEmpty()) {
                            System.out.println("No vehicle need maintenance");
                        } else {
                            System.out.println("Vehicles that need maintenance:");
                            for (int i = 0; i < needsMaintenance.size(); i++) {
                                needsMaintenance.get(i).displayInfo();
                            }
                        }
                    }
                    case 11 -> fleet.sortByFuelEfficiency();
                    case 12 -> fleet.sortByMaxSpeed();
                    case 13 -> fleet.sortByModelName();
                    case 14 -> System.out.println(fleet.generateEnhancedReport());
                    case 15 -> {
                        try {
                            var fastest = fleet.getFastestVehicle();
                            System.out.println("Fastest Vehicle:");
                            fastest.displayInfo();
                        } catch (Exception e) {
                            System.out.println("No vehicles in fleet");
                        }
                    }
                    case 16 -> {
                        try {
                            var slowest = fleet.getSlowestVehicle();
                            System.out.println("Slowest Vehicle:");
                            slowest.displayInfo();
                        } catch (Exception e) {
                            System.out.println("No vehicles in fleet");
                        }
                    }
                    case 17 -> {
                        System.out.println("Unique Models: " + fleet.getUniqueModels());
                        System.out.println("Total Unique Models: " + fleet.getUniqueModelCount());
                    }
                    case 19 -> {
                        javax.swing.SwingUtilities.invokeLater(() -> new HighwaySimulationFrame(fleet));
                    }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (Exception issue) {
                System.out.println("Issue: " + issue.getMessage());
                if (in.hasNextLine()) in.nextLine();
            }
        }

        
        in.close();
    }
}
