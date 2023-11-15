package com.gaia3d;


import com.gaia3d.soundDataConverter.SoundDataConverter;
import com.gaia3d.utils.StringModifier;
import org.apache.commons.cli.*;
import org.locationtech.proj4j.CRSFactory;

import java.io.IOException;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws ParseException, IOException {
        System.out.println("Start the program.");
        Options options = new Options();
        options.addOption("type", true, "conversion type");
        options.addOption("input", true, "input folder path");
        options.addOption("output", true, "output folder path");
        options.addOption("inputProj", true, "input proj4 string");

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(options, args);

        String type = commandLine.getOptionValue("type");

        if(type == "SOUND_SIMULATION")
        {
            // Sound simulation data.************************************
            String inputFolderPath = commandLine.getOptionValue("input");
            String outputFolderPath = commandLine.getOptionValue("output");
            // check if exist the inputProj.***
            String inputProj = "+proj=tmerc +lat_0=38 +lon_0=127 +k=1 +x_0=200000 +y_0=600000 +ellps=GRS80 +units=m +no_defs"; // 5186.***
            if(commandLine.hasOption("inputProj"))
            {
                inputProj = commandLine.getOptionValue("inputProj");
            }

            SoundDataConverter soundDataConverter = new SoundDataConverter();

            // set the coords of the inputData.***
            CRSFactory factory = new CRSFactory();
            // provisionally set the proj4 as 5186.***
            String proj = inputProj;
            if (proj != null && !proj.isEmpty()) {
                soundDataConverter.inputCrs = factory.createFromParameters("CUSTOM", proj);
            }

            StringModifier.createAllFoldersIfNoExist(outputFolderPath);

            soundDataConverter.convertDataInFolder(inputFolderPath, outputFolderPath);
            soundDataConverter.writeJsonIndexFile(outputFolderPath);
        }

    }
}