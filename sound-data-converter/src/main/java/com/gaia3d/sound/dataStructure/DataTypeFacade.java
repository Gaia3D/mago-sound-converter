package com.gaia3d.sound.dataStructure;

import org.locationtech.proj4j.CoordinateReferenceSystem;
//import org.opengis.referencing.FactoryException;

import java.io.IOException;
import java.util.ArrayList;

public class DataTypeFacade
{
    int numBuilding;
    public ArrayList<DataTypePlan> dataTypePlanList;
    public DataTypePlan totalDataTypePlan; // must join all dataTypePlanList to one dataTypePlan.***
    public ArrayList<Integer> buildingIndexList;

    public String fileName;

    public DataTypeFacade()
    {
        numBuilding = 0;
        dataTypePlanList = new ArrayList<DataTypePlan>();
        buildingIndexList = new ArrayList<Integer>();
    }

    public DataTypePlan newDataTypePlan()
    {
        DataTypePlan dataTypePlan = new DataTypePlan();
        dataTypePlanList.add(dataTypePlan);
        return dataTypePlan;
    }

    public void convertData(CoordinateReferenceSystem inputCrs) {
        // 1rst, join all dataTypePlanList to one dataTypePlan.***
        this.totalDataTypePlan = new DataTypePlan();

        int totalvertexCount = 0;

        int dataTypePlanCount = dataTypePlanList.size();
        for (int i = 0; i < dataTypePlanCount; i++)
        {
            DataTypePlan dataTypePlan = dataTypePlanList.get(i);
            totalvertexCount += dataTypePlan.vertexList.size();
            totalDataTypePlan.joinDataTypePlan(dataTypePlan);

        }

        // now convert data.***
        totalDataTypePlan.convertData(inputCrs);

        int hola = 0;

    }

    public void writeToGlbFile(String gltfFilePath) throws IOException
    {
        totalDataTypePlan.writeToGlbFile(gltfFilePath);
    }

    public void writeToJsonFile(String jsonFilePath) throws IOException
    {
        totalDataTypePlan.writeToJsonFile(jsonFilePath);
    }
}
