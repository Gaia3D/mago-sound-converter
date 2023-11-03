package com.gaia3d.dataStructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gaia3d.geometry.BoundingBox;
import com.gaia3d.globe.Globe;
import com.gaia3d.utils.CoordinatesUtils;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.ProjCoordinate;

import java.util.ArrayList;

public class DataType_Plan
{
    public int objNLv_Type;
    public int num_Node;

    public ArrayList<Vertex> vertexList;
    public ArrayList<Vector3d> positionsLC;

    public int num_Rect;
    public ArrayList<RectangleFace> faceList;

    public DataType_Plan()
    {
        objNLv_Type = 0;
        num_Node = 0;
        vertexList = new ArrayList<Vertex>();
        faceList = new ArrayList<RectangleFace>();
    }

    public void convertData(CoordinateReferenceSystem inputCrs)
    {
        // 1. convert vertex coords data to wgs84.***
        ProjCoordinate projCoordinate = new ProjCoordinate(0.0, 0.0, 0.0);
        ProjCoordinate resultWGS84 = new ProjCoordinate(0.0, 0.0, 0.0);
        BoundingBox boundingBox = new BoundingBox();
        int vertexCount = vertexList.size();
        for (int i = 0; i < vertexCount; i++)
        {
            Vertex vertex = vertexList.get(i);

            projCoordinate.setValue(vertex.x, vertex.y, vertex.z);
            CoordinatesUtils.transformToWGS84(inputCrs, projCoordinate, resultWGS84);

            vertex.x = resultWGS84.x;
            vertex.y = resultWGS84.y;
            if(Double.isNaN(resultWGS84.z))
                vertex.z = 0.0;
            else
            vertex.z = resultWGS84.z;

            // calculate the boundingBox.***
            if(i == 0)
            {
                boundingBox.initBox(vertex.x, vertex.y, vertex.z);
            }
            else
            {
                boundingBox.addPoint(vertex.x, vertex.y, vertex.z);
            }

            int hola = 0;
        }

        // Now, calculate the centerGeographicCoords = (centerLongitude, centerLatitude, centerAltitude).***
        Vector3d centerGeoCoord = new Vector3d();
        boundingBox.getCenterPosition(centerGeoCoord);

        // calculate tMat at centerGeoCoords.***
        Vector3d centerPosWC = new Vector3d();
        Globe.geographicToCartesianWGS84(Math.toRadians(centerGeoCoord.x), Math.toRadians(centerGeoCoord.y), centerGeoCoord.z, centerPosWC);
        Matrix4d tMat = new Matrix4d();
        Globe.transformMatrixAtCartesianPointWgs84(centerPosWC.x, centerPosWC.y, centerPosWC.z, tMat);
        Matrix4d tMatInv = new Matrix4d();
        tMat.invert(tMatInv);

        // 2. convert vertex coords data to cartesianWC 1rst & cartesianLC after.***
        if(positionsLC == null)
            positionsLC = new ArrayList<Vector3d>();

        Vector3d cartesianWC = new Vector3d(0.0, 0.0, 0.0);

        for (int i = 0; i < vertexCount; i++)
        {
            Vertex vertex = vertexList.get(i);
            Globe.geographicToCartesianWGS84(Math.toRadians(vertex.x), Math.toRadians(vertex.y), vertex.z, cartesianWC);
            Vector3d cartesianLC = new Vector3d(0.0, 0.0, 0.0);
            tMatInv.transformPosition(cartesianWC, cartesianLC);
            positionsLC.add(cartesianLC);
        }

        int hola = 0;
    }

    public void saveJson(String jsonFilePath)
    {
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
//	]
//        }

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNodeRoot = objectMapper.createObjectNode();

        ObjectNode objectNodeCenterGeographicCoord = objectMapper.createObjectNode();
        objectNodeCenterGeographicCoord.put("altitude", 0.0);
        objectNodeCenterGeographicCoord.put("latitude", 0.0);
        objectNodeCenterGeographicCoord.put("longitude", 0.0);
        objectNodeRoot.set("centerGeographicCoord", objectNodeCenterGeographicCoord);

        objectNodeRoot.put("date", "20220926");
        objectNodeRoot.put("fileName", "M.OUT");

        int[] indices = new int[faceList.size() * 6];
        for (int i = 0; i < faceList.size(); i++)
        {
//            indices[i * 6 + 0] = faceList.get(i).vertexIndices[0];
//            indices[i * 6 + 1] = faceList.get(i).vertexIndices[1];
//            indices[i * 6 + 2] = faceList.get(i).vertexIndices[2];
//            indices[i * 6 + 3] = faceList.get(i).vertexIndices[0];
//            indices[i * 6 + 4] = faceList.get(i).vertexIndices[2];
//            indices[i * 6 + 5] = faceList.get(i).vertexIndices[3];
        }

        objectNodeRoot.putPOJO("indices", indices);

        objectNodeRoot.put("maxSoundValue", 0.0);
        objectNodeRoot.put("minSoundValue", 0.0);

        double[] positions = new double[vertexList.size() * 3];
        for (int i = 0; i < vertexList.size(); i++)
        {
            positions[i * 3 + 0] = vertexList.get(i).x;
            positions[i * 3 + 1] = vertexList.get(i).y;
            positions[i * 3 + 2] = vertexList.get(i).z;
        }

        objectNodeRoot.putPOJO("positions", positions);



    }
}
