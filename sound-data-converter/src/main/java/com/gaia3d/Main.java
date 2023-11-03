package com.gaia3d;


import com.gaia3d.soundDataConverter.SoundDataConverter;
import org.apache.commons.cli.*;
import org.locationtech.proj4j.CRSFactory;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws ParseException {
        System.out.println("Start the program.");
        Options options = new Options();
        options.addOption("type", true, "conversion type");
        options.addOption("input", true, "input folder path");
        options.addOption("output", true, "output folder path");

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(options, args);

        String type = commandLine.getOptionValue("type");

        if(type == "SOUND_SIMULATION")
        {
            // Sound simulation data.************************************
            String inputFolderPath = commandLine.getOptionValue("input");
            String outputFolderPath = commandLine.getOptionValue("output");

            SoundDataConverter soundDataConverter = new SoundDataConverter();

            // set the coords of the inputData.***
            CRSFactory factory = new CRSFactory();
            // provisionally set the proj4 as 5186.***
            String proj = "+proj=tmerc +lat_0=38 +lon_0=127 +k=1 +x_0=200000 +y_0=600000 +ellps=GRS80 +units=m +no_defs"; // 5186.***
            if (proj != null && !proj.isEmpty()) {
                soundDataConverter.inputCrs = factory.createFromParameters("CUSTOM", proj);
            }

            soundDataConverter.convertDataInFolder(inputFolderPath, outputFolderPath);
        }

    }
}