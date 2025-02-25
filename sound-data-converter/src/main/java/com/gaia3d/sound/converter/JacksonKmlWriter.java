package com.gaia3d.sound.converter;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.util.DefaultXmlPrettyPrinter;
import com.gaia3d.sound.converter.kml.*;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3d;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@NoArgsConstructor
public class JacksonKmlWriter {

    public void write(File output, File glbPath, Vector3d position) {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // arrange the data
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        xmlMapper.setDefaultPrettyPrinter(new DefaultXmlPrettyPrinter());

        try {
            KmlRoot kmlRoot = createKml(position, glbPath.getName());
            xmlMapper.writeValue(output, kmlRoot);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
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
     */

    private KmlRoot createKml(Vector3d position, String fileName) {
        KmlRoot kmlRoot = new KmlRoot();
        Document document = new Document();


        Placemark placemark = new Placemark();
        placemark.setName(fileName);
        placemark.setDescription("This is a description");

        Model model = new Model();
        model.setAltitudeMode("absolute");

        Location location = new Location();
        location.setLongitude(position.x);
        location.setLatitude(position.y);
        location.setAltitude(position.z);
        model.setLocation(location);

        Orientation orientation = new Orientation();
        orientation.setHeading(0.0);
        orientation.setTilt(0.0);
        orientation.setRoll(0.0);
        model.setOrientation(orientation);

        Scale scale = new Scale();
        scale.setX(1.0);
        scale.setY(1.0);
        scale.setZ(1.0);
        model.setScale(scale);

        Link link = new Link();
        link.setHref(fileName);
        model.setLink(link);

        placemark.setModel(List.of(model));
        document.setPlacemark(List.of(placemark));

        kmlRoot.setDocument(document);
        return kmlRoot;
    }
}
