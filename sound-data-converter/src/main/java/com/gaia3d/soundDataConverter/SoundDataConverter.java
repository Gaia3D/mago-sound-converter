package com.gaia3d.soundDataConverter;

import com.gaia3d.utils.StringModifier;
import com.gaia3d.utils.io.LittleEndianDataInputStream;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SoundDataConverter
{
    public SoundDataConverter()
    {
        System.out.println("SoundDataConverter constructor");
    }

    public void convertDataInFolder(String inputFolderPath, String outputFolderPath)
    {
        System.out.println("SoundDataConverter convert");
        // 1rst, find all files *.RBin in the input folder.***
        ArrayList<String> vecFileExtensions = new ArrayList<>();
        vecFileExtensions.add("RBin");
        ArrayList<String> vecFileNames = new ArrayList<>();
        StringModifier.getFileNamesInFolder(inputFolderPath, vecFileExtensions, vecFileNames);

        int filesCount = vecFileNames.size();
        for (int i = 0; i < filesCount; i++)
        {
            String fileName = vecFileNames.get(i);
            String rawFileName = StringModifier.getRawFileName(fileName);
            System.out.println("fileName = " + fileName);


            String inputFilePath = inputFolderPath + "/" + fileName;
            String outputFilePath = outputFolderPath + "/" + rawFileName + ".json";
            try
            {
                convertData(inputFilePath, outputFilePath);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void convertData(String inputFilePath, String outputFilePath) throws FileNotFoundException
    {
        System.out.println("SoundDataConverter convert");
        // the input file is binary.***
        // the output file is json.***
        Path inputPath = Paths.get(inputFilePath);
        File input = inputPath.toFile();

        try (LittleEndianDataInputStream stream = new LittleEndianDataInputStream(new BufferedInputStream(new FileInputStream(input))))
        {
            /*
            this.isBigEndian = stream.readByte();
            this.projectName = stream.readText();
            int materialCount = stream.readInt();
            List<GaiaMaterial> materials = new ArrayList<>();

            for (int i = 0; i < materialCount; i++) {
                GaiaMaterial material = new GaiaMaterial();
                material.read(stream, imagesPath);
                materials.add(material);
            }

            if (materials.isEmpty()) {
                log.error("material size is 0");
            }

            this.materials = materials;
            int bufferDataCount = stream.readInt();
            List<GaiaBufferDataSet> bufferDataSets = new ArrayList<>();
            for (int i = 0; i < bufferDataCount; i++) {
                GaiaBufferDataSet bufferDataSet = new GaiaBufferDataSet();
                bufferDataSet.read(stream);

                int materialId = bufferDataSet.getMaterialId();
                GaiaMaterial materialById = materials.stream()
                        .filter(material -> material.getId() == materialId)
                        .findFirst().orElseThrow();
                bufferDataSet.setMaterial(materialById);
                GaiaRectangle texcoordBoundingRectangle = calcTexcoordBoundingRectangle(bufferDataSet);
                bufferDataSet.setTexcoordBoundingRectangle(texcoordBoundingRectangle);
                bufferDataSets.add(bufferDataSet);

             */
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }

    }
}
