# Vehicle Management System

## Overview

A Java-based Transportation Fleet Management System with a Swing GUI that simulates a real-time highway environment using multithreading. Each vehicle runs on its own thread, consuming fuel and updating its mileage independently. The project also demonstrates race conditions and their resolution using `ReentrantLock`.

## Features

- 🚗 Fleet management with multiple vehicle types
- 🧵 Multithreaded vehicle simulation (one thread per vehicle)
- ⛽ Real-time fuel consumption and mileage tracking
- 🖥️ Java Swing graphical interface
- ▶️ Start, Pause, Resume, and Stop simulation controls
- 🔒 Demonstrates race conditions and synchronization using `ReentrantLock`
- 📊 Live highway distance, total mileage, and vehicle status updates

## Technologies Used

- Java
- Java Swing
- Multithreading
- ReentrantLock
- Object-Oriented Programming (OOP)

## How It Works

- Each vehicle runs on a separate thread.
- Vehicles consume fuel and update mileage while moving.
- A shared highway counter tracks total distance travelled.
- Running without synchronization demonstrates race conditions.
- Enabling synchronization prevents lost updates using `ReentrantLock`.

## Learning Outcomes

- Java Multithreading
- Thread Synchronization
- Java Swing GUI Development
- Event Dispatch Thread (EDT)
- Object-Oriented Design
