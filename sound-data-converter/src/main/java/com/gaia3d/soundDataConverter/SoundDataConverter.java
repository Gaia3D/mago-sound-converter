package com.gaia3d.soundDataConverter;

import com.gaia3d.utils.StringModifier;

import java.util.ArrayList;

public class SoundDataConverter
{
    public SoundDataConverter()
    {
        System.out.println("SoundDataConverter constructor");
    }

    public void convertInFolder(String inputFolderPath, String outputFolderPath)
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
            System.out.println("fileName = " + fileName);


        }
    }
}
