package com.gaia3d.sound.globe;

import lombok.extern.slf4j.Slf4j;
import org.joml.Matrix4d;
import org.joml.Vector3d;

import java.util.Vector;

@Slf4j
public class Globe {
    public static double equatorialRadiusMeters() {
        return 6378137.0; // meters.
    }

    public static double equatorialRadiusMetersSquared() {
        return 40680631590769.0; // meters x meters.
    }

    public static double polarRadiusMeters() {
        return 6356752.3142; // meters.
    }

    public static double polarRadiusMetersSquared() {
        return 40408299984087.05552164; // meters x meters.
    }

    public static Vector3d normalAtCartesianPointWgs84(double x, double y, double z) {
        Vector3d resultNormal = new Vector3d();

        double equatorialRadiusSquared = equatorialRadiusMetersSquared();
        double polarRadiusSquared = polarRadiusMetersSquared();

        resultNormal.set(x / equatorialRadiusSquared, y / equatorialRadiusSquared, z / polarRadiusSquared);
        resultNormal.normalize();

        return resultNormal;
    }

    public static void transformMatrixAtCartesianPointWgs84(double x, double y, double z, Matrix4d transformMatrix) {
        Vector3d xAxis = new Vector3d();
        Vector3d yAxis = new Vector3d();
        Vector3d zAxis = normalAtCartesianPointWgs84(x, y, z);

        // Note: Check if zAxis is vertical vector. PENDENT.***
        //-----------------------------------------------------

        // now, calculate the east direction.
        // project zAxis to plane XY and calculate the left perpendicular.***
        xAxis.set(-y, x, 0.0);
        xAxis.normalize();

        // finally calculate the north direction.***
        zAxis.cross(xAxis, yAxis);
        yAxis.normalize();

        transformMatrix.set(xAxis.x, xAxis.y, xAxis.z, 0.0, yAxis.x, yAxis.y, yAxis.z, 0.0, zAxis.x, zAxis.y, zAxis.z, 0.0, x, y, z, 1.0);
    }

    public static Vector<Double> geographicDegree2DArrayToCartesianWGS84Array(Vector<Double> vecGeoCoordsDeg2D) {
        Vector<Double> vecCartesianWgs84Array = new Vector<>();

        int pointsCount = vecGeoCoordsDeg2D.size() / 2;
        Double altitude = 0.0;
        for (int i = 0; i < pointsCount; i++) {
            Double lonRad = Math.toRadians(vecGeoCoordsDeg2D.get(i * 2));
            Double latRad = Math.toRadians(vecGeoCoordsDeg2D.get(i * 2 + 1));
            Vector3d cartesianWC = new Vector3d();
            geographicToCartesianWGS84(lonRad, latRad, altitude, cartesianWC);

            vecCartesianWgs84Array.add(cartesianWC.x);
            vecCartesianWgs84Array.add(cartesianWC.y);
            vecCartesianWgs84Array.add(cartesianWC.z);
        }

        return vecCartesianWgs84Array;
    }

    public static void geographicToCartesianWGS84(Double lonRad, Double latRad, Double altitude, Vector3d resultCartesian) {
        // defined in the LINZ standard LINZS25000 (Standard for New Zealand Geodetic Datum 2000)
        // https://www.linz.govt.nz/data/geodetic-system/coordinate-conversion/geodetic-datum-conversions/equations-used-datum
        // a = semi-major axis.
        // e2 = firstEccentricitySquared.
        // v = a / sqrt(1 - e2 * sin2(lat)).
        // x = (v+h)*cos(lat)*cos(lon).
        // y = (v+h)*cos(lat)*sin(lon).
        // z = [v*(1-e2)+h]*sin(lat).
        double degToRadFactor = 0.017453292519943296; // 3.141592653589793 / 180.0;
        double equatorialRadius = equatorialRadiusMeters(); // meters.
        double firstEccentricitySquared = 6.69437999014E-3;
        double cosLon = Math.cos(lonRad);
        double cosLat = Math.cos(latRad);
        double sinLon = Math.sin(lonRad);
        double sinLat = Math.sin(latRad);
        double a = equatorialRadius;
        double e2 = firstEccentricitySquared;
        double v = a / Math.sqrt(1.0 - e2 * sinLat * sinLat);
        double h = altitude;

        resultCartesian.x = (v + h) * cosLat * cosLon;
        resultCartesian.y = (v + h) * cosLat * sinLon;
        resultCartesian.z = (v * (1.0 - e2) + h) * sinLat;

    }
}
