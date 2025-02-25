package com.gaia3d.sound.converter;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * KmlReader is a class that reads kml files.
 * It reads kml files and returns the information of the kml file.
 */
@Slf4j
@NoArgsConstructor
public class JacksonKmlReader {

    /*@Override
    public KmlInfo read(File file) {
        KmlInfo kmlInfo = null;
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            KmlRoot root = xmlMapper.readValue(file, KmlRoot.class);
            Document document = root.getDocument();
            for (Placemark placemark : document.getPlacemark()) {
                String name = placemark.getName();
                String description = placemark.getDescription();
                List<Model> models = placemark.getModel();
                Model model = models.get(0);
                String altitudeMode = model.getAltitudeMode();
                String href = model.getLink().getHref();
                double longitude = model.getLocation().getLongitude();
                double latitude = model.getLocation().getLatitude();
                double altitude = model.getLocation().getAltitude();
                double heading = model.getOrientation().getHeading();
                double tilt = model.getOrientation().getTilt();
                double roll = model.getOrientation().getRoll();
                double x = model.getScale().getX();
                double y = model.getScale().getY();
                double z = model.getScale().getZ();
                kmlInfo = KmlInfo.builder().name(name).position(new Vector3d(longitude, latitude, altitude)).altitudeMode(altitudeMode).heading(heading).tilt(tilt).roll(roll).href(href).scaleX(x).scaleY(y).scaleZ(z).build();
            }
        } catch (IOException e) {
            log.error("Error : ", e);
            throw new RuntimeException(e);
        }
        return kmlInfo;
    }


    public List<KmlInfo> readAll(File file) {
        List<KmlInfo> kmlInfos = new ArrayList<>();
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            KmlRoot root = xmlMapper.readValue(file, KmlRoot.class);
            Document document = root.getDocument();
            for (Placemark placemark : document.getPlacemark()) {
                String name = placemark.getName();
                String description = placemark.getDescription();
                List<Model> models = placemark.getModel();
                for (Model model : models) {
                    String altitudeMode = model.getAltitudeMode();
                    String href = model.getLink().getHref();
                    double longitude = model.getLocation().getLongitude();
                    double latitude = model.getLocation().getLatitude();
                    double altitude = model.getLocation().getAltitude();
                    double heading = model.getOrientation().getHeading();
                    double tilt = model.getOrientation().getTilt();
                    double roll = model.getOrientation().getRoll();
                    double x = model.getScale().getX();
                    double y = model.getScale().getY();
                    double z = model.getScale().getZ();

                    HashMap<String, String> properties = new HashMap<>();
                    properties.put("name", name);
                    properties.put("description", description);
                    KmlInfo kmlInfo = KmlInfo.builder()
                            .name(name)
                            .position(new Vector3d(longitude, latitude, altitude))
                            .altitudeMode(altitudeMode)
                            .heading(heading)
                            .tilt(tilt)
                            .roll(roll)
                            .href(href)
                            .scaleX(x)
                            .scaleY(y)
                            .scaleZ(z)
                            .properties(properties)
                            .build();
                    kmlInfos.add(kmlInfo);
                }
            }
        } catch (IOException e) {
            log.error("Error : ", e);
            throw new RuntimeException(e);
        }
        return kmlInfos;
    }*/
}
