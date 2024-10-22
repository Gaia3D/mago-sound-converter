package com.gaia3d.sound.utils;

import org.locationtech.proj4j.BasicCoordinateTransform;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.ProjCoordinate;

public class CoordinatesUtils {
    public static void transformToWGS84(CoordinateReferenceSystem source, ProjCoordinate coordinate, ProjCoordinate result) {
        CRSFactory factory = new CRSFactory();
        CoordinateReferenceSystem wgs84 = factory.createFromParameters("WGS84", "+proj=longlat +datum=WGS84 +no_defs");
        BasicCoordinateTransform transformer = new BasicCoordinateTransform(source, wgs84);
        transformer.transform(coordinate, result);
    }
}
