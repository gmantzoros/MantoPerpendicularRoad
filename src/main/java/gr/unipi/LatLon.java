package gr.unipi;

/**
 * The `LatLon` class represents a geographical point with latitude and longitude.
 * It is implemented as a Java record, providing an immutable data structure.
 *
 * <p>Features:
 * <ul>
 *     <li>Stores latitude and longitude as immutable fields.</li>
 *     <li>Provides a utility method to parse a `LatLon` object from a string.</li>
 *     <li>Overrides the `toString` method to return a string representation of the point.</li>
 * </ul>
 *
 * @param lat The latitude of the geographical point.
 * @param lon The longitude of the geographical point.
 * @version 1.0
 */
public record LatLon(double lat, double lon) {

    public static LatLon fromString(String latLonStr) {
        String[] parts = latLonStr.replace(" ", "").split(",");
        return new LatLon(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
    }

    @Override
    public String toString() {
        return lat + "," + lon;
    }
}
