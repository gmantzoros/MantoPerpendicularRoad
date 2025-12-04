# Road Detection App

A Java application that detects the nearest roads to a given geographical location using the Google Maps Roads API and the Google Maps Geocoding API.  
It performs geospatial calculations such as bearing computation, forward movement, and determining points on a circle around a given coordinate.

## Features

- Detects nearest roads using Google Roads API
- Calculates bearing angles between two coordinates
- Computes two offset bearings for directional evaluation
- Generates circular projection points around a location
- Simulates moving a point forward based on distance and bearing
- Resolves Google Place IDs to road names using Geocoding API

## How It Works

### 1. Input Coordinates
Two points are used:
- Current Position
- Previous Position

### 2. Movement Simulation
`GeoUtils.movePointForward()` calculates a new point based on the distance traveled.

### 3. Bearing Offsets
Two angles (bearing ± offset) are generated for directional road detection.

### 4. Circle Projection
Using a search radius (25m), two points around the current position are computed.

### 5. Nearest Road Lookup
Google Maps Roads API identifies the nearest road for each point.

### 6. Road Name Resolution
The Place ID returned by the Roads API is resolved into a readable road name using the Geocoding API.

## Requirements

- Java 11 or newer
- A valid Google Maps API Key with:
    - Roads API enabled
    - Geocoding API enabled

Insert your key in:

```
private static final String API_KEY = "YOUR_API_KEY_HERE";
```

## Build & Run

### Compile:
```
javac -d out src/gr/unipi/*.java
```

### Run:
```
java -cp out gr.unipi.RoadDetectionApp
```

## Example Output

```
Moved Current: LatLon{lat=38.247300, lon=21.739020}
Point 1: LatLon{...}
Point 2: LatLon{...}
We have 2 roads: Main Street and Karaiskaki
```
