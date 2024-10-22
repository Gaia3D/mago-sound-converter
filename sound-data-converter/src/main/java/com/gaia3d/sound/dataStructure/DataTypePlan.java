package com.gaia3d.sound.dataStructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gaia3d.basic.structure.*;
import com.gaia3d.sound.geometry.BoundingBox;
import com.gaia3d.sound.globe.Globe;
import com.gaia3d.sound.jgltf.GltfWriter;
import com.gaia3d.sound.utils.CoordinatesUtils;
import lombok.extern.slf4j.Slf4j;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.ProjCoordinate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DataTypePlan {
    public int objNLv_Type;
    public int num_Node;

    public String date;

    public String fileName;

    public int buildingIndex = -1; // this is for the facade.***
    public ArrayList<Vertex> vertexList;
    public ArrayList<Vector3d> positionsLC;
    public double[] minSoundValue;
    public double[] maxSoundValue;
    public int num_Rect;
    public ArrayList<RectangleFace> faceList;
    public Map<GaiaVertex, Double> vertexNoiseLevelMap = new HashMap<>();
    Vector3d centerGeoCoords; // (longitude, latitude, altitude).***

    public DataTypePlan() {
        objNLv_Type = 0;
        num_Node = 0;
        vertexList = new ArrayList<Vertex>();
        faceList = new ArrayList<RectangleFace>();
    }

    public void joinDataTypePlan(DataTypePlan dataTypePlan) {
        int currVertexCount = this.vertexList.size();

        // 1rst, join the vertexList.***
        int vertexCount = dataTypePlan.vertexList.size();
        for (int i = 0; i < vertexCount; i++) {
            Vertex vertex = dataTypePlan.vertexList.get(i);
            vertexList.add(vertex);
        }

        // 2nd, join the faceList.***
        int faceCount = dataTypePlan.faceList.size();
        for (int i = 0; i < faceCount; i++) {
            RectangleFace face = dataTypePlan.faceList.get(i);
            // must offset the indices.***
            face.index1 += currVertexCount;
            face.index2 += currVertexCount;
            face.index3 += currVertexCount;
            face.index4 += currVertexCount;
            faceList.add(face);
        }
    }

    public void convertData(CoordinateReferenceSystem inputCrs) {
        // 1. convert vertex coords data to wgs84.***
        ProjCoordinate projCoordinate = new ProjCoordinate(0.0, 0.0, 0.0);
        ProjCoordinate resultWGS84 = new ProjCoordinate(0.0, 0.0, 0.0);
        BoundingBox boundingBox = new BoundingBox();
        int vertexCount = vertexList.size();
        for (int i = 0; i < vertexCount; i++) {
            Vertex vertex = vertexList.get(i);
            projCoordinate.setValue(vertex.x, vertex.y, vertex.z);
            CoordinatesUtils.transformToWGS84(inputCrs, projCoordinate, resultWGS84);

            vertex.x = resultWGS84.x;
            vertex.y = resultWGS84.y;
            if (!Double.isNaN(resultWGS84.z)) vertex.z = resultWGS84.z;


            // calculate the boundingBox & minMaxSoundValues.***
            int objNLvLength = vertex.objNLv.length;
            double[] objNLv = vertex.objNLv; // object Noise Level.***
            if (i == 0) {
                boundingBox.initBox(vertex.x, vertex.y, vertex.z);
                minSoundValue = new double[objNLvLength];
                maxSoundValue = new double[objNLvLength];
                for (int j = 0; j < objNLvLength; j++) {
                    minSoundValue[j] = objNLv[j];
                    maxSoundValue[j] = objNLv[j];
                }
            } else {
                boundingBox.addPoint(vertex.x, vertex.y, vertex.z);
                for (int j = 0; j < objNLvLength; j++) {
                    if (objNLv[j] < minSoundValue[j]) minSoundValue[j] = objNLv[j];
                    if (objNLv[j] > maxSoundValue[j]) maxSoundValue[j] = objNLv[j];
                }
            }
        }

        // Now, calculate the centerGeographicCoords = (centerLongitude, centerLatitude, centerAltitude).***
        this.centerGeoCoords = new Vector3d();
        boundingBox.getCenterPosition(centerGeoCoords);

        // calculate tMat at centerGeoCoords.***
        Vector3d centerPosWC = new Vector3d();
        Globe.geographicToCartesianWGS84(Math.toRadians(centerGeoCoords.x), Math.toRadians(centerGeoCoords.y), centerGeoCoords.z, centerPosWC);
        Matrix4d tMat = new Matrix4d();
        Globe.transformMatrixAtCartesianPointWgs84(centerPosWC.x, centerPosWC.y, centerPosWC.z, tMat);
        Matrix4d tMatInv = new Matrix4d();
        tMat.invert(tMatInv);

        // 2. convert vertex coords data to cartesianWC 1rst & cartesianLC after.***
        if (positionsLC == null) positionsLC = new ArrayList<Vector3d>();

        Vector3d cartesianWC = new Vector3d(0.0, 0.0, 0.0);

        for (int i = 0; i < vertexCount; i++) {
            Vertex vertex = vertexList.get(i);
            Globe.geographicToCartesianWGS84(Math.toRadians(vertex.x), Math.toRadians(vertex.y), vertex.z, cartesianWC);
            Vector3d cartesianLC = new Vector3d(0.0, 0.0, 0.0);
            tMatInv.transformPosition(cartesianWC, cartesianLC);

            positionsLC.add(cartesianLC);
        }
    }

    public Vector4d getColorByNoiseLevel(double noiseLevel) {
        Vector4d color = new Vector4d();

        if (noiseLevel >= 150) {
            color.set(227.0 / 255.0, 39.0 / 255.0, 27.0 / 255.0, 1.0);
        } else if (noiseLevel >= 130) {
            color.set(231.0 / 255.0, 77.0 / 255.0, 24.0 / 255.0, 1.0);
        } else if (noiseLevel >= 120) {
            color.set(237.0 / 255.0, 115.0 / 255.0, 14.0 / 255.0, 1.0);
        } else if (noiseLevel >= 110) {
            color.set(242.0 / 255.0, 141.0 / 255.0, 11.0 / 255.0, 1.0);
        } else if (noiseLevel >= 100) {
            color.set(224.0 / 255.0, 157.0 / 255.0, 27.0 / 255.0, 1.0);
        } else if (noiseLevel >= 85) {
            color.set(191.0 / 255.0, 165.0 / 255.0, 55.0 / 255.0, 1.0);
        } else if (noiseLevel >= 80) {
            color.set(151.0 / 255.0, 167.0 / 255.0, 78.0 / 255.0, 1.0);
        } else if (noiseLevel >= 70) {
            color.set(70.0 / 255.0, 165.0 / 255.0, 97.0 / 255.0, 1.0);
        } else if (noiseLevel >= 60) {
            color.set(53.0 / 255.0, 163.0 / 255.0, 132.0 / 255.0, 1.0);
        } else if (noiseLevel >= 30) {
            color.set(38.0 / 255.0, 162.0 / 255.0, 170.0 / 255.0, 1.0);
        } else {
            color.set(23.0 / 255.0, 159.0 / 255.0, 201.0 / 255.0, 1.0);
        }

        return color;
    }

    public GaiaScene getGaiaScene() {
        GaiaScene gaiaScene = new GaiaScene();
        GaiaNode rootNode = new GaiaNode();
        gaiaScene.getNodes().add(rootNode);

        GaiaNode node = new GaiaNode();
        rootNode.getChildren().add(node);

        GaiaMesh mesh = new GaiaMesh();
        node.getMeshes().add(mesh);

        GaiaPrimitive primitive = new GaiaPrimitive();
        mesh.getPrimitives().add(primitive);

        GaiaSurface surface = new GaiaSurface();
        primitive.getSurfaces().add(surface);

        // vertices.***
        int pointsLCCount = positionsLC.size();
        int vertexCount = vertexList.size();
        for (int i = 0; i < vertexList.size(); i++) {
            Vertex vertex = vertexList.get(i);
            Vector3d positionLC = positionsLC.get(i);
            GaiaVertex gaiaVertex = new GaiaVertex();
            gaiaVertex.setPosition(new Vector3d(positionLC));
            double objNLv = vertex.objNLv[0];
            vertexNoiseLevelMap.put(gaiaVertex, objNLv);

            Vector4d color = getColorByNoiseLevel(objNLv);
            byte[] byteColor = new byte[4];
            byteColor[0] = (byte) (color.x * 255);
            byteColor[1] = (byte) (color.y * 255);
            byteColor[2] = (byte) (color.z * 255);
            byteColor[3] = (byte) (color.w * 255);
            gaiaVertex.setColor(byteColor);

            primitive.getVertices().add(gaiaVertex);
        }

        // faces.***
        int[] trianglesIndices = new int[2 * 3]; // 2 triangles X 3 Vertices.***
        for (RectangleFace face : faceList) {
            face.getTrianglesIndices(trianglesIndices);
            GaiaFace gaiaFace = new GaiaFace();
            int[] indicesA = new int[3];
            indicesA[0] = trianglesIndices[0];
            indicesA[1] = trianglesIndices[1];
            indicesA[2] = trianglesIndices[2];
            gaiaFace.setIndices(indicesA);

            GaiaFace gaiaFace2 = new GaiaFace();
            int[] indicesB = new int[3];
            indicesB[0] = trianglesIndices[3];
            indicesB[1] = trianglesIndices[4];
            indicesB[2] = trianglesIndices[5];
            gaiaFace2.setIndices(indicesB);

            surface.getFaces().add(gaiaFace);
            surface.getFaces().add(gaiaFace2);
        }


        return gaiaScene;
    }

    public void writeToGlbFile(String glbFilePath) throws IOException {
        GaiaScene gaiaScene = getGaiaScene();
        GltfWriter gltfWriter = new GltfWriter();
        gltfWriter.writeGlb(gaiaScene, glbFilePath);
    }

    public void writeToJsonFile(String jsonFilePath) throws IOException {
        // json sample:
//        {
//            "centerGeographicCoord": {
//            "altitude": 76.221,
//                    "latitude": 37.61701569054009,
//                    "longitude": 127.1827785160741
//        },
//            "date": "20220926",
//                "fileName": "M.OUT",
//                "indices": [
//            0,
//                    1,
//                    12,
//                    1,
//                    13,
//                    12,
//                    1,
//                    2,
//                    13,
//                    2,
//                    14,
//                    13,
//                    2,
//                    3,
//                    14,
//                    3,
//                    15,
//		...
//	],
//            "maxSoundValue": 76.30000305175781,
//                "minSoundValue": 30.86300086975098,
//                "positions": [
//            174.2522735595703,
//                    -2.902303457260132,
//                    -52.27137756347656,
//                    173.4910278320313,
//                    -6.108829021453857,
//                    -52.2713623046875,
//                    172.7297821044922,
//                    -9.316353797912598,
//                    -52.27134323120117,
//                    171.9685363769531,
//                    -12.5228796005249,
//                    -52.27132797241211,
//                    175.7008666992188,
//                    -13.3841495513916,
//                    -52.27143096923828,
//                    179.4331970214844,
//                    -14.24541854858398,
//                    -52.27153778076172,
//                    183.1665191650391,
//                    -15.10669040679932,
//                    -52.27164459228516,
//                    183.927734375,
//                    -11.92116451263428,
//                    -52.27165985107422,
//                    184.6889343261719,
//		...
//	],
//   "soundLevelValues" : [
//      45.12799835205078,
//      55.89899826049805,
//      55.53900146484375,
//      51.24599838256836,
//      55.31700134277344,
//      54.86000061035156,
//      47.84600067138672,
//      56.28400039672852,
//      55.69699859619141,
//      50.16899871826172,
//      55.65700149536133,
//      56.49700164794922,
//      44.49700164794922,
//      54.33200073242188,
//      53.78599929809570,
//      54.61100006103516,
//      54.34899902343750,
//      55.06600189208984,
//	  ...
//	  ]
//        }

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNodeRoot = objectMapper.createObjectNode();

        ObjectNode objectNodeCenterGeographicCoord = objectMapper.createObjectNode();
        objectNodeCenterGeographicCoord.put("altitude", this.centerGeoCoords.z);
        objectNodeCenterGeographicCoord.put("latitude", this.centerGeoCoords.y);
        objectNodeCenterGeographicCoord.put("longitude", this.centerGeoCoords.x);
        objectNodeRoot.set("centerGeographicCoord", objectNodeCenterGeographicCoord);

        objectNodeRoot.put("date", this.date);
        objectNodeRoot.put("fileName", this.fileName);

        int[] indices = new int[faceList.size() * 6];
        int[] trianglesIndices = new int[2 * 3]; // 2 triangles X 3 Vertices.***
        for (int i = 0; i < faceList.size(); i++) {
            RectangleFace face = faceList.get(i);
            face.getTrianglesIndices(trianglesIndices);
            indices[i * 6] = trianglesIndices[0];
            indices[i * 6 + 1] = trianglesIndices[1];
            indices[i * 6 + 2] = trianglesIndices[2];
            indices[i * 6 + 3] = trianglesIndices[3];
            indices[i * 6 + 4] = trianglesIndices[4];
            indices[i * 6 + 5] = trianglesIndices[5];

        }

        objectNodeRoot.putPOJO("indices", indices);
        objectNodeRoot.put("maxSoundValue", this.maxSoundValue[0]);
        objectNodeRoot.put("minSoundValue", this.minSoundValue[0]);

        double[] positions = new double[positionsLC.size() * 3];
        for (int i = 0; i < positionsLC.size(); i++) {
            positions[i * 3] = positionsLC.get(i).x;
            positions[i * 3 + 1] = positionsLC.get(i).y;
            positions[i * 3 + 2] = positionsLC.get(i).z;
        }

        objectNodeRoot.putPOJO("positions", positions);

        double[] soundLevelValues = new double[vertexList.size()];
        for (int i = 0; i < vertexList.size(); i++) {
            soundLevelValues[i] = vertexList.get(i).objNLv[0];
        }

        objectNodeRoot.putPOJO("soundLevelValues", soundLevelValues);


        JsonNode jsonNode = new ObjectMapper().readTree(objectNodeRoot.toString());
        objectMapper.writeValue(new File(jsonFilePath), jsonNode);

    }
}
