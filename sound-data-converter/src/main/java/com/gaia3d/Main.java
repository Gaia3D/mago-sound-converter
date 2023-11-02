package com.gaia3d;


import com.gaia3d.soundDataConverter.SoundDataConverter;
import org.apache.commons.cli.*;

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
            soundDataConverter.convertDataInFolder(inputFolderPath, outputFolderPath);

            //soundDataConverter.testFunction();
        }

    }
}