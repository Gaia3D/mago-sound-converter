package com.gaia3d.sound.dataStructure;

import lombok.extern.slf4j.Slf4j;
import org.locationtech.proj4j.CoordinateReferenceSystem;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class DataTypeFacade {
    public ArrayList<DataTypePlan> dataTypePlanList;
    public DataTypePlan totalDataTypePlan; // must join all dataTypePlanList to one dataTypePlan.***
    public ArrayList<Integer> buildingIndexList;
    public String fileName;
    int numBuilding;

    public DataTypeFacade() {
        numBuilding = 0;
        dataTypePlanList = new ArrayList<DataTypePlan>();
        buildingIndexList = new ArrayList<Integer>();
    }

    public DataTypePlan newDataTypePlan() {
        DataTypePlan dataTypePlan = new DataTypePlan();
        dataTypePlanList.add(dataTypePlan);
        return dataTypePlan;
    }

    public void convertData(CoordinateReferenceSystem inputCrs) {
        // 1rst, join all dataTypePlanList to one dataTypePlan.***
        this.totalDataTypePlan = new DataTypePlan();

        for (DataTypePlan dataTypePlan : dataTypePlanList) {
            totalDataTypePlan.joinDataTypePlan(dataTypePlan);
        }
        // now convert data.***
        totalDataTypePlan.convertData(inputCrs);
    }

    public void writeToGlbFile(String gltfFilePath) throws IOException {
        totalDataTypePlan.writeToGlbFile(gltfFilePath);
    }

    public void writeToKmlFile(String kmlPath, String glbPath) throws IOException {
        totalDataTypePlan.writeToKmlFile(kmlPath, glbPath);
    }

    public void writeToJsonFile(String jsonFilePath) throws IOException {
        totalDataTypePlan.writeToJsonFile(jsonFilePath);
    }
}
