package com.gaia3d.utils;

//import org.geotools.referencing.CRS;
import org.locationtech.proj4j.BasicCoordinateTransform;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.ProjCoordinate;
//import org.opengis.referencing.FactoryException;
//import org.opengis.referencing.operation.MathTransform;

public class CoordinatesUtils
{
    public static void transformToWGS84(CoordinateReferenceSystem source, ProjCoordinate coordinate, ProjCoordinate result) {
        CRSFactory factory = new CRSFactory();
        CoordinateReferenceSystem wgs84 = factory.createFromParameters("WGS84", "+proj=longlat +datum=WGS84 +no_defs");
        BasicCoordinateTransform transformer = new BasicCoordinateTransform(source, wgs84);
        transformer.transform(coordinate, result);

        //CoordinateReferenceSystem inputCrs = CRS.decode("+proj=tmerc +lat_0=38 +lon_0=127.5 +k=0.9996 +x_0=1000000 +y_0=2000000 +ellps=bessel +units=m +no_defs +towgs84=-115.80,474.99,674.11,1.16,-2.31,-1.63,6.43"); // korea 2000.***
        //CoordinateReferenceSystem inputCrs = CRS.decode("EPSG:5179"); // UTMK.***
        //CoordinateReferenceSystem crs4326 = (CoordinateReferenceSystem) CRS.decode("EPSG:4326");
        //MathTransform transform = CRS.findMathTransform((org.opengis.referencing.crs.CoordinateReferenceSystem) source, (org.opengis.referencing.crs.CoordinateReferenceSystem) crs4326);

        int hola = 0;
    }
}
