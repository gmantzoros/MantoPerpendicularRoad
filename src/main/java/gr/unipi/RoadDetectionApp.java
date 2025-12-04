package gr.unipi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * The `RoadDetectionApp` class is a Java application that detects the nearest roads
 * to a given geographical location using the Google Maps Roads API and Geocoding API.
 * It calculates bearing angles, determines points on a circle, and identifies the
 * nearest roads to those points.
 *
 * <p>Features:
 * <ul>
 *     <li>Calculate bearing angles between two geographical points.</li>
 *     <li>Determine points on a circle around a given center point.</li>
 *     <li>Retrieve the nearest roads to specified points using the Google Maps APIs.</li>
 * </ul>
 *
 * <p>Note: This application requires a valid Google Maps API key to function.
 *
 * <p>Constants:
 * <ul>
 *     <li>{@link #EARTH_RADIUS}: The Earth's radius in meters.</li>
 *     <li>{@link #SEARCH_RADIUS}: The radius in meters for searching nearby roads.</li>
 * </ul>
 *
 * @author gmantzoros
 * @version 1.0
 */
public class RoadDetectionApp {

    public static final double EARTH_RADIUS = 6371000; // meters
    private static final double SEARCH_RADIUS = 25.0;   // meters
    private static final String API_KEY = ""; //TODO: Insert your Google Maps API Key here

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static void main(String[] args) {

        LatLon current = LatLon.fromString("38.247250, 21.738974"); //Current Position //Test Case
        LatLon previous = LatLon.fromString("38.247170, 21.739063"); //Previous Position //Test Case

        //Acceptable Cases
        LatLon newCurrent = GeoUtils.movePointForward(previous, current, 25);
        System.out.println("Moved Current: " + newCurrent);

        //Calculate the two bearing angles
        double[] angles = GeoUtils.calculateBearingAngles(previous, newCurrent, 30);
        LatLon point1 = GeoUtils.calculatePointOnCircle(newCurrent, SEARCH_RADIUS, angles[0]);
        LatLon point2 = GeoUtils.calculatePointOnCircle(newCurrent, SEARCH_RADIUS, angles[1]);

        //print out the points
        System.out.println("Point 1: " + point1);
        System.out.println("Point 2: " + point2);

        //Get nearest roads
        String[] roads = getNearestRoads(point1, point2);
        String currentRoad = "";
        String targetRoad;

        //Determine current road
        if (!roads[0].equals(currentRoad) && !roads[1].equals(currentRoad)) {
            if (roads[0].equals(roads[1])) {
                targetRoad = roads[0];
                System.out.println("The target road is: " + targetRoad);
            } else {
                System.out.printf("We have 2 roads: %s and %s%n", roads[0], roads[1]);
            }
        } else if (roads[0].equals(currentRoad)) {
            System.out.println("The target road is: " + roads[1]);
        } else {
            System.out.println("The target road is: " + roads[0]);
        }
    }

    /**
     * Retrieves the nearest roads to two geographical points using the Google Maps Roads API.
     *
     * @param p1 The first geographical point.
     * @param p2 The second geographical point.
     * @return An array of road names corresponding to the nearest roads to the given points.
     */
    private static String[] getNearestRoads(LatLon p1, LatLon p2) {
        String pointsParam = URLEncoder.encode(p1 + "|" + p2, StandardCharsets.UTF_8);
        String url = String.format(
                "https://roads.googleapis.com/v1/nearestRoads?points=%s&key=%s",
                pointsParam, API_KEY
        );

        try {
            String response = sendGetRequest(url);
            JSONObject json = new JSONObject(response);
            JSONArray snappedPoints = json.getJSONArray("snappedPoints");

            String[] placeIds = new String[2];
            for (int i = 0; i < snappedPoints.length(); i++) {
                JSONObject point = snappedPoints.getJSONObject(i);
                int index = point.getInt("originalIndex");
                String placeId = point.getString("placeId");
                if (index == 0) placeIds[0] = placeId;
                if (index == 1) placeIds[1] = placeId;
            }

            return new String[]{
                    resolvePlaceIdToRoad(placeIds[0]),
                    resolvePlaceIdToRoad(placeIds[1])
            };
        } catch (Exception e) {
            e.printStackTrace();
            return new String[]{"", ""};
        }
    }

    /**
     * Resolves a Google Maps Place ID to the name of the road it represents using the
     * Google Maps Geocoding API.
     *
     * @param placeId The Place ID to resolve.
     * @return The name of the road corresponding to the given Place ID, or an empty string if not found.
     */
    private static String resolvePlaceIdToRoad(String placeId) {
        if (placeId == null) return "";

        String url = String.format(
                "https://maps.googleapis.com/maps/api/geocode/json?place_id=%s&key=%s",
                placeId, API_KEY
        );

        try {
            String response = sendGetRequest(url);
            JSONObject json = new JSONObject(response);
            JSONArray components = json.getJSONArray("results")
                    .getJSONObject(0)
                    .getJSONArray("address_components");

            for (int i = 0; i < components.length(); i++) {
                JSONObject comp = components.getJSONObject(i);
                JSONArray types = comp.getJSONArray("types");
                for (int j = 0; j < types.length(); j++) {
                    if ("route".equals(types.getString(j))) {
                        return comp.getString("long_name");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Sends an HTTP GET request to the specified URL and returns the response body as a string.
     *
     * @param url The URL to send the GET request to.
     * @return The response body as a string.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If the operation is interrupted.
     */
    private static String sendGetRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}

