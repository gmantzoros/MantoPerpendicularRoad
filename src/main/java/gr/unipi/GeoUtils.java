package gr.unipi;

/**
 * The `GeoUtils` class provides utility methods for geographical calculations,
 * such as calculating bearing angles, determining points on a circle, and moving
 * points forward by a specified distance.
 *
 * <p>Features:
 * <ul>
 *     <li>Calculate the bearing angle between two geographical points.</li>
 *     <li>Calculate two bearing angles with an offset.</li>
 *     <li>Determine a point on a circle given a center, radius, and angle.</li>
 *     <li>Move a point forward by a specified distance in meters.</li>
 * </ul>
 *
 * @version 1.0
 */
public class GeoUtils {

    /**
     * Calculates two bearing angles between two geographical points with an offset.
     *
     * @param from The starting geographical point.
     * @param to The destination geographical point.
     * @param offsetAngle The offset angle in degrees.
     * @return An array containing two bearing angles: [bearing - offset, bearing + offset].
     */
    public static double[] calculateBearingAngles(LatLon from, LatLon to, double offsetAngle) {
        double bearing = calculateBearing(from, to);
        return new double[]{bearing - offsetAngle, bearing + offsetAngle};
    }

    /**
     * Calculates the bearing angle between two geographical points.
     *
     * @param from The starting geographical point.
     * @param to The destination geographical point.
     * @return The bearing angle in degrees, normalized to the range [0, 360).
     */
    public static double calculateBearing(LatLon from, LatLon to) {
        double lat1 = Math.toRadians(from.lat());
        double lon1 = Math.toRadians(from.lon());
        double lat2 = Math.toRadians(to.lat());
        double lon2 = Math.toRadians(to.lon());

        double y = Math.sin(lon2 - lon1) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2)
                - Math.sin(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1);

        double bearing = Math.toDegrees(Math.atan2(y, x));
        return (bearing + 360) % 360; // normalize to 0-360
    }

    /**
     * Calculates a point on a circle given a center point, radius, and angle.
     *
     * @param center The center point of the circle.
     * @param radius The radius of the circle in meters.
     * @param angleDeg The angle in degrees from the center point.
     * @return A `LatLon` object representing the calculated point on the circle.
     */
    public static LatLon calculatePointOnCircle(LatLon center, double radius, double angleDeg) {
        double centerLatRad = Math.toRadians(center.lat());
        double centerLonRad = Math.toRadians(center.lon());
        double radiusRad = radius / RoadDetectionApp.EARTH_RADIUS;
        double angleRad = Math.toRadians(angleDeg);

        double latRad = Math.asin(Math.sin(centerLatRad) * Math.cos(radiusRad) +
                Math.cos(centerLatRad) * Math.sin(radiusRad) * Math.cos(angleRad));

        double lonRad = centerLonRad + Math.atan2(
                Math.sin(angleRad) * Math.sin(radiusRad) * Math.cos(centerLatRad),
                Math.cos(radiusRad) - Math.sin(centerLatRad) * Math.sin(latRad));

        return new LatLon(Math.toDegrees(latRad), Math.toDegrees(lonRad));
    }

    /**
     * Moves a geographical point forward by a specified distance in meters along the bearing
     * from another point.
     *
     * @param point1 The reference point to calculate the bearing.
     * @param point2 The point to move forward.
     * @param distanceMeters The distance to move forward in meters.
     * @return A `LatLon` object representing the new point after moving forward.
     */
    public static LatLon movePointForward(LatLon point1, LatLon point2, double distanceMeters) {
        double bearing = calculateBearing(point1, point2);

        //Return the current point $distanceMeters forward
        return calculatePointOnCircle(point2, distanceMeters, bearing);
    }

}

