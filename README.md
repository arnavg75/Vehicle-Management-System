
Vehicle Management System

Overview
--------
This project extends the original Transportation Fleet Management System with a Swing-based graphical simulator that runs a highway simulation using multiple threads. Each vehicle in the fleet runs on its own thread to simulate movement and fuel consumption. A shared `Highway` counter represents the aggregate highway meters driven by all vehicles; the GUI demonstrates a race condition on this shared counter and includes a toggle to enable a synchronization fix (ReentrantLock).


Steps to compile, run and test the program
-----------------------------------------

To compile all the files:
 javac -d out (Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName })

Run the CLI:
java -cp out assignment.Main

Notes:
- The GUI reads the current fleet from `FleetManager`; if no vehicles exist, three sample vehicles are added at startup.
- CLI `Exit` option forces JVM exit (System.exit(0)) to stop the GUI too.

Overview of the design and GUI layout
------------------------------------
- Top Controls: "Start", "Pause", "Resume", "Stop" — control the entire simulation.
- Synchronization Toggle: CheckBox labeled "Use Synchronization (Lock)" toggles whether `Highway.increment()` uses a `ReentrantLock` or not.
- Center Table: Displays vehicles with columns [Vehicle, Fuel, Mileage, Status]. Selecting a row picks that vehicle for specific actions.
- Right-Side Panel: Per-vehicle controls — a numeric field to input refuel amount, and buttons: "Refuel Selected Vehicle", "Start Selected", "Stop Selected".
- Bottom Status Bar: Shows the Highway distance, Total Mileage across all vehicles, the difference (to demonstrate lost increments under a race condition), a small status message, and a label showing whether synchronization is enabled.

How the simulation threads are controlled via the GUI
---------------------------------------------------
- Each `Vehicle` is represented by a lightweight `SimInfo` structure. The GUI creates one thread per running vehicle.
- "Start" (top) launches threads for all vehicles that are not running; "Stop" terminates them (interrupt and flag to stop), and "Pause/Resume" toggles a `paused` flag.
- Starting a single vehicle ("Start Selected") creates one thread for that vehicle. "Stop Selected" stops the vehicle's thread.
- When a vehicle runs and consumes fuel to 0, the thread stops with the status "Out of Fuel". Refueling the vehicle by any amount using the refuel controls resumes status and allows you to start the thread again using "Start Selected".

Demonstrating the race condition and synchronization fix
-------------------------------------------------------
- Behavior: All vehicles call `Highway.increment(int delta)` as they move. Without synchronization (`useLock = false`), concurrent increments may cause lost updates causing the displayed Highway Distance to be lower than the sum of actual vehicle mileage due to race conditions.

Steps to reproduce the race condition (no lock):
1. Start the program and open the GUI (option 19 from the CLI, or run `assignment.HighwaySimulationFrame`).
2. Ensure the "Use Synchronization (Lock)" checkbox is UNCHECKED.
3. Click "Start" to launch the simulation.
4. Let the simulation run for a few seconds and observe the bottom status: the "Highway Distance" may not match the "Total Mileage"; the difference highlights lost increments (race condition).

Steps to verify the fix (with lock):
1. Pause or Stop the simulation and check the "Use Synchronization (Lock)" checkbox to enable the lock.
2. Click "Start" again to run the simulation in synchronized mode.
3. The highway counter will now accurately reflect the total mileage across vehicles, eliminating lost updates.


GUI thread-safety considerations (Event Dispatch Thread)
-------------------------------------------------------
- All Swing UI updates must run on the Event Dispatch Thread (EDT). Updating Swing components from non-EDT threads causes undefined behavior.
- This project uses a Swing `Timer` which executes on the EDT to periodically update the table and status labels.
- The per-vehicle threads perform model updates (mileage, fuel and calling `Highway.increment()`) off the EDT and then rely on the Timer to re-sync the UI with the model. This is the recommended approach: keep model work off the EDT and schedule UI updates on the EDT.
- When a worker thread needs to make a small direct UI update (rare in this project), use `SwingUtilities.invokeLater()` to do so safely.

Testing tips and recommended settings
-----------------------------------
- To make the race condition more apparent, increase the number of vehicles running concurrently and decrease sleep times in `HighwaySimulationFrame`'s vehicle loop. This accelerates total increments and the chance of lost updates.
- You can also increase the work done in the `runVehicleLoop` to make newsletter contention more likely. Use caution: keep the UI responsive.

Support and debugging
---------------------
- Common issues: If the GUI opinion doesn't show vehicles, ensure `FleetManager` contains vehicles. The GUI adds three sample vehicles automatically when the fleet is empty.
- If the CLI `Exit` option doesn't stop the GUI, use Task Manager to kill the JVM process (should not be necessary because `System.exit(0)` is called in exit option 18).
- For further debugging, add logging to `Highway.increment()` to print thread names or increment counts and compare rolling totals.

Project Files
-------------
- `src/assignment/Highway.java` - Shared highway counter with optional lock.
- `src/assignment/HighwaySimulationFrame.java` - Swing UI for simulation and thread management.
- `src/assignment/Fleet/FleetManager.java` - Fleet management logic.
- `src/assignment/Interfaces/*` - Interface definitions.
- `src/assignment/Vehicles/*` - Vehicle classes.
- `src/assignment/Main.java` - CLI entry point, menu, and option 19 to open the GUI.

Acknowledgements
----------------
- The Swing Timer and the EDT approach follow standard Java Swing best practices. See Oracle's Swing tutorial for more details on thread-safety.
