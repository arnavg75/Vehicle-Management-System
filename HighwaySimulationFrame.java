package assignment;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import assignment.Fleet.FleetManager;
import assignment.Interfaces.FuelConsumable;
import assignment.Exceptions.InvalidOperationException;
import assignment.Vehicles.Vehicle;

public class HighwaySimulationFrame extends JFrame {

    private DefaultTableModel tableModel;
    private JTable vehicleTable;

    private final FleetManager fleetManager;
    private final Map<Vehicle, SimInfo> simulators = new HashMap<>();

    private final JLabel highwayDistanceLabel = new JLabel("Highway Distance: 0");
    private final JLabel totalMileageLabel   = new JLabel("Total Mileage: 0");
    private final JLabel diffLabel           = new JLabel("Difference: 0");
    private final JLabel statusLabel         = new JLabel("Ready");
    private final JLabel syncLabel           = new JLabel("Sync: OFF");

    private javax.swing.Timer uiTimer;
    private int baselineTotalMileage = 0;

    public HighwaySimulationFrame(FleetManager fleet) {
        super("Fleet Highway Simulator");
        this.fleetManager = fleet;

        ensureInitialThreeVehicles();

        setupUI();
        loadFleetIntoSimulators();

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                stopSimulation();
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(this::stopSimulation));

        setLocationRelativeTo(null);
        setVisible(true);
    }
    private void ensureInitialThreeVehicles() {
        var fleet = fleetManager.getFleet();
        if (fleet.isEmpty()) {
            try { fleetManager.addVehicle(new assignment.Vehicles.Car("Car1", "Sedan", 120, 4, 5)); } catch (InvalidOperationException ignored) {}
            try { fleetManager.addVehicle(new assignment.Vehicles.Bus("Bus1", "CityBus", 100, 6, 40)); } catch (InvalidOperationException ignored) {}
            try { fleetManager.addVehicle(new assignment.Vehicles.Truck("Truck1", "Hauler", 90, 6)); } catch (InvalidOperationException ignored) {}

            for (var v : fleetManager.getFleet()) {
                if (v instanceof FuelConsumable fc) {
                    try {
                        double current = fc.getFuelLevel();
                        fc.refuel(80 - current);
                    } catch (Exception ignored) {}
                }
            }
        }
    }
    private void setupUI() {
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(10, 10, 10, 10));

        
        JPanel topControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton btnStart         = new JButton("Start");
        JButton btnPause         = new JButton("Pause");
        JButton btnResume        = new JButton("Resume");
        JButton btnStop          = new JButton("Stop");
        JCheckBox chkSync        = new JCheckBox("Use Synchronization (Lock)");
        JButton btnRefuelAll     = new JButton("Refuel All");
        JButton btnResetHighway  = new JButton("Reset Highway Distance");
        

        topControls.add(btnStart);
        topControls.add(btnPause);
        topControls.add(btnResume);
        topControls.add(btnStop);
        topControls.add(chkSync);
        topControls.add(btnRefuelAll);
        topControls.add(btnResetHighway);
        topControls.add(statusLabel);

        
        tableModel = new DefaultTableModel(new Object[] {"Vehicle", "Fuel", "Mileage", "Status"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        vehicleTable = new JTable(tableModel);
        vehicleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScroll = new JScrollPane(vehicleTable);

        content.add(topControls, BorderLayout.NORTH);
        content.add(tableScroll, BorderLayout.CENTER);

        
        JPanel rightPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        rightPanel.setBorder(new EmptyBorder(0, 10, 0, 0));

        JLabel lblRefuel = new JLabel("Refuel Amount:");
        JTextField refuelAmountField = new JTextField("100");
        JButton btnRefuelSpecific = new JButton("Refuel Selected Vehicle");
        JButton btnStartSelected  = new JButton("Start Selected");
        JButton btnStopSelected   = new JButton("Stop Selected");

        rightPanel.add(lblRefuel);
        rightPanel.add(refuelAmountField);
        rightPanel.add(btnRefuelSpecific);
        rightPanel.add(btnStartSelected);
        rightPanel.add(btnStopSelected);

        content.add(rightPanel, BorderLayout.EAST);

        
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bottom.add(highwayDistanceLabel);
        bottom.add(totalMileageLabel);
        bottom.add(diffLabel);
        bottom.add(syncLabel);
        content.add(bottom, BorderLayout.SOUTH);

        setContentPane(content);

        
        btnStart.addActionListener(e -> startSimulation());
        btnPause.addActionListener(e -> pauseSimulation());
        btnResume.addActionListener(e -> resumeSimulation());
        btnStop.addActionListener(e -> stopSimulation());

        btnRefuelAll.addActionListener(e -> {
            refuelAllVehicles();
            updateUIFromTimer();
        });
        btnResetHighway.addActionListener(e -> {
            Highway.reset();
            statusLabel.setText("Highway distance reset to 0.");
            updateUIFromTimer();
        });

        btnRefuelSpecific.addActionListener(e -> {
            int row = vehicleTable.getSelectedRow();
            if (row < 0) {
                statusLabel.setText("Select a vehicle first.");
                return;
            }
            try {
                double amount = Double.parseDouble(refuelAmountField.getText());
                refuelSelectedVehicle(amount);
                updateUIFromTimer();
            } catch (NumberFormatException ex) {
                statusLabel.setText("Invalid refuel amount.");
            }
        });

        btnStartSelected.addActionListener(e -> startSelectedVehicle());
        btnStopSelected.addActionListener(e -> stopSelectedVehicle());

        chkSync.addActionListener(e -> {
            Highway.useLock = chkSync.isSelected();
            syncLabel.setText(Highway.useLock ? "Sync: ON" : "Sync: OFF");
            statusLabel.setText("Synchronization: " + (Highway.useLock ? "ON" : "OFF"));
            updateUIFromTimer();
        });

        statusLabel.setText("Ready: Press Start to run simulation.");

        
        uiTimer = new javax.swing.Timer(700, new ActionListener() {
            private int counter = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                updateUIFromTimer();
            }
        });
        uiTimer.start();
    }
    private static class SimInfo {
        Thread thread;
        volatile boolean running = false;
        volatile boolean paused  = false;
        volatile String  status  = "Stopped";
    }

    
    private void syncSimulatorsWithFleet() {
        
        for (Vehicle v : fleetManager.getFleet()) {
            simulators.computeIfAbsent(v, vv -> {
                SimInfo si = new SimInfo();
                si.status = "Stopped";
                return si;
            });
        }
        
        simulators.keySet().removeIf(v -> !fleetManager.getFleet().contains(v));
    }

    private void loadFleetIntoSimulators() {
        simulators.clear();
        for (Vehicle v : fleetManager.getFleet()) {
            SimInfo si = new SimInfo();
            si.status = "Stopped";
            simulators.put(v, si);
        }
        updateUIFromTimer();
    }

    private void refuelSelectedVehicle(double amount) {
        int idx = vehicleTable.getSelectedRow();
        if (idx < 0) {
            statusLabel.setText("Select a vehicle first");
            return;
        }
        Vehicle v = fleetManager.getFleet().get(idx);
        if (v instanceof FuelConsumable fc) {
            try {
                fc.refuel(amount);
                statusLabel.setText("Refueled " + v.getId() + " by " + amount);
                SimInfo si = simulators.get(v);
                if (si != null && "Out of Fuel".equals(si.status)) {
                        si.status  = "Running";
                        si.paused  = false;
                        if (si.thread == null || !si.thread.isAlive()) {
                            si.running = true;
                            si.thread = new Thread(() -> runVehicleLoop(v, si), "Sim-" + v.getId());
                            si.thread.setDaemon(true);
                            si.thread.start();
                        } else {
                            si.running = true;
                        }
                }
            } catch (Exception e) {
                statusLabel.setText("Refuel failed: " + e.getMessage());
            }
        } else {
            statusLabel.setText("Selected vehicle is not fuel consumable.");
        }
    }
    private void refuelAllVehicles() {
        fleetManager.refuelAll(100);
        statusLabel.setText("All vehicles refueled by 100.");
        for (var entry : simulators.entrySet()) {
            var v = entry.getKey();
            var si = entry.getValue();
            if (si != null && "Out of Fuel".equals(si.status)) {
                si.status = "Running";
                si.paused = false;
                if (si.thread == null || !si.thread.isAlive()) {
                    si.running = true;
                    si.thread = new Thread(() -> runVehicleLoop(v, si), "Sim-" + v.getId());
                    si.thread.setDaemon(true);
                    si.thread.start();
                } else {
                    si.running = true;
                }
            }
        }
    }

    private void startSimulation() {
        syncSimulatorsWithFleet();

        if (simulators.isEmpty()) {
            loadFleetIntoSimulators();
        }
        if (simulators.isEmpty()) {
            statusLabel.setText("No vehicles to simulate.");
            return;
        }

        List<Vehicle> vs = fleetManager.getFleet();
        baselineTotalMileage = vs.stream().mapToInt(v -> (int) v.getCurrentMileage()).sum();

        for (var entry : simulators.entrySet()) {
            Vehicle v = entry.getKey();
            SimInfo si = entry.getValue();
            if (si.running) continue;

            si.running = true;
            si.paused  = false;
            si.status  = "Running";

            si.thread = new Thread(() -> runVehicleLoop(v, si), "Sim-" + v.getId());
            si.thread.setDaemon(true);
            si.thread.start();
        }

        statusLabel.setText("Simulation started.");
    }
    private void pauseSimulation() {
        simulators.values().forEach(si -> {
            if (si.running) {
                si.paused = true;
                si.status = "Paused";
            }
        });
        statusLabel.setText("Simulation paused.");
    }
    private void resumeSimulation() {
        simulators.values().forEach(si -> {
            if (si.running) {
                si.paused = false;
                si.status = "Running";
            }
        });
        statusLabel.setText("Simulation resumed.");
    }
    private void stopSimulation() {
        simulators.values().forEach(si -> {
            si.running = false;
            si.paused  = false;
            si.status  = "Stopped";
            if (si.thread != null) si.thread.interrupt();
        });
        baselineTotalMileage = 0;
        statusLabel.setText("Simulation stopped.");
        updateUIFromTimer();
    }

    private void startSelectedVehicle() {
        int row = vehicleTable.getSelectedRow();
        if (row < 0) {
            statusLabel.setText("Select a vehicle.");
            return;
        }
        Vehicle v = fleetManager.getFleet().get(row);

        syncSimulatorsWithFleet();
        SimInfo si = simulators.get(v);
        if (si == null) {
            statusLabel.setText("No simulation info for selected vehicle.");
            return;
        }

        if (!si.running) {
            si.running = true;
            si.paused  = false;
            si.status  = "Running";

            si.thread = new Thread(() -> runVehicleLoop(v, si), "Sim-" + v.getId());
            si.thread.setDaemon(true);
            si.thread.start();
            statusLabel.setText("Started " + v.getId());
        } else {
            si.paused = false;
            si.status = "Running";
            statusLabel.setText("Resumed " + v.getId());
        }
    }

    private void stopSelectedVehicle() {
        int row = vehicleTable.getSelectedRow();
        if (row < 0) {
            statusLabel.setText("Select a vehicle.");
            return;
        }
        Vehicle v = fleetManager.getFleet().get(row);

        syncSimulatorsWithFleet();
        SimInfo si = simulators.get(v);
        if (si == null) {
            statusLabel.setText("No simulation info for selected vehicle.");
            return;
        }

        si.running = false;
        si.paused  = false;
        si.status  = "Stopped";
        if (si.thread != null) si.thread.interrupt();
        statusLabel.setText("Stopped " + v.getId());
    }

    private void runVehicleLoop(Vehicle v, SimInfo si) {
        final double STEP_DISTANCE = 1.0;
        final int SLEEP_MS = 1000;

        while (si.running) {
            try {
                if (si.paused) {
                    Thread.sleep(100);
                    continue;
                }

                if (v instanceof FuelConsumable fc) {
                    try {
                        fc.consumeFuel(STEP_DISTANCE);
                    } catch (Exception ex) {
                        si.status = "Out of Fuel";
                        si.paused = true;
                        Thread.sleep(250);
                        continue;
                    }
                }

                v.updateMileage(STEP_DISTANCE);
                Highway.increment((int) STEP_DISTANCE);

                Thread.sleep(SLEEP_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        if (!si.running && !"Out of Fuel".equals(si.status)) {
            si.status = "Stopped";
        }
    }
    private void updateUIFromTimer() {
        syncSimulatorsWithFleet();

        List<Vehicle> vehicles = fleetManager.getFleet();
        int selectedRow = vehicleTable.getSelectedRow();

        tableModel.setRowCount(0);

        for (Vehicle v : vehicles) {
            SimInfo si = simulators.get(v);
            String status;

            if (si == null) {
                status = "Stopped";
            } else if ("Out of Fuel".equals(si.status)) {
                status = "Out of Fuel";
            } else if (si.running && !si.paused) {
                status = "Running";
            } else if (si.running && si.paused) {
                status = "Paused";
            } else {
                status = si.status != null ? si.status : "Stopped";
            }

            String fuelInfo;
            if (v instanceof FuelConsumable fc) {
                try {
                    fuelInfo = String.valueOf(fc.getFuelLevel());
                } catch (Exception ignored) {
                    fuelInfo = "N/A";
                }
            } else {
                fuelInfo = "N/A";
            }

            tableModel.addRow(new Object[] {
                    v.getId(),
                    fuelInfo,
                    (int) v.getCurrentMileage(),
                    status
            });
        }
        if (selectedRow >= 0 && selectedRow < tableModel.getRowCount()) {
            vehicleTable.setRowSelectionInterval(selectedRow, selectedRow);
        }
        int totalAll = vehicles.stream().mapToInt(v -> (int) v.getCurrentMileage()).sum();
        highwayDistanceLabel.setText("Highway Distance: " + Highway.getDistance());
        totalMileageLabel.setText("Total Mileage: " + totalAll);
        diffLabel.setText("Difference: " + (Highway.getDistance() - totalAll));
    }
}
