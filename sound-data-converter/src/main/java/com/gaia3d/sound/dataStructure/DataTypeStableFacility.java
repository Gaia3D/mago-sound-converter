package com.gaia3d.sound.dataStructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class DataTypeStableFacility {
    public int objNLvType;
    public int numBuilding;

    public ArrayList<SubDataTypeStableFacility> subDataTypeStableFacilityArray = new ArrayList<SubDataTypeStableFacility>();

    public void writeToJsonFile(String jsonFilePath) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNodeRoot = objectMapper.createObjectNode();

        objectNodeRoot.put("objNLvType", this.objNLvType);
        objectNodeRoot.put("numBuilding", this.numBuilding);

        ArrayNode arrayNodeSubDataTypeStableFacility = objectMapper.createArrayNode();
        for (SubDataTypeStableFacility subDataTypeStableFacility : subDataTypeStableFacilityArray) {
            ObjectNode objectNodeSubDataTypeStableFacility = objectMapper.createObjectNode();
            objectNodeSubDataTypeStableFacility.put("index", subDataTypeStableFacility.index);
            objectNodeSubDataTypeStableFacility.put("numFloor", subDataTypeStableFacility.numFloor);
            objectNodeSubDataTypeStableFacility.put("height", subDataTypeStableFacility.height);

            ArrayNode arrayNodeObjNLvList = objectMapper.createArrayNode();
            for (int j = 0; j < subDataTypeStableFacility.objNLvList.size(); j++) {
                arrayNodeObjNLvList.add(subDataTypeStableFacility.objNLvList.get(j));
            }
            objectNodeSubDataTypeStableFacility.set("objNLvList", arrayNodeObjNLvList);

            arrayNodeSubDataTypeStableFacility.add(objectNodeSubDataTypeStableFacility);
        }

        objectNodeRoot.set("subDataTypeStableFacilityArray", arrayNodeSubDataTypeStableFacility);

        JsonNode jsonNode = new ObjectMapper().readTree(objectNodeRoot.toString());
        objectMapper.writeValue(new File(jsonFilePath), jsonNode);

    }
}
