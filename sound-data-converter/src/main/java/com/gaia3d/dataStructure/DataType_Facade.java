package com.gaia3d.dataStructure;

import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.opengis.referencing.FactoryException;

import java.io.IOException;
import java.util.ArrayList;

public class DataType_Facade
{
    int num_Building;
    public ArrayList<DataType_Plan> dataTypePlanList;
    public DataType_Plan totalDataTypePlan; // must join all dataTypePlanList to one dataTypePlan.***
    public ArrayList<Integer> buildingIndexList;

    public String fileName;

    public DataType_Facade()
    {
        num_Building = 0;
        dataTypePlanList = new ArrayList<DataType_Plan>();
        buildingIndexList = new ArrayList<Integer>();
    }

    public DataType_Plan newDataTypePlan()
    {
        DataType_Plan dataTypePlan = new DataType_Plan();
        dataTypePlanList.add(dataTypePlan);
        return dataTypePlan;
    }

    public void convertData(CoordinateReferenceSystem inputCrs) throws FactoryException {
        // 1rst, join all dataTypePlanList to one dataTypePlan.***
        this.totalDataTypePlan = new DataType_Plan();

        int totalvertexCount = 0;

        int dataTypePlanCount = dataTypePlanList.size();
        for (int i = 0; i < dataTypePlanCount; i++)
        {
            DataType_Plan dataTypePlan = dataTypePlanList.get(i);
            totalvertexCount += dataTypePlan.vertexList.size();
            totalDataTypePlan.joinDataTypePlan(dataTypePlan);

        }

        // now convert data.***
        totalDataTypePlan.convertData(inputCrs);

        int hola = 0;

    }

    public void writeToJsonFile(String jsonFilePath) throws IOException
    {
        totalDataTypePlan.writeToJsonFile(jsonFilePath);
    }
}
