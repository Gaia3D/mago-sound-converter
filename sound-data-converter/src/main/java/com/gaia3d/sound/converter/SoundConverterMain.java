package com.gaia3d.sound.converter;


import com.gaia3d.sound.Configurator;
import com.gaia3d.sound.utils.StringModifier;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;

import java.io.File;
import java.io.IOException;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
@Slf4j
public class SoundConverterMain {
    public static void main(String[] args) throws ParseException, IOException {
        Configurator.initConsoleLogger();

        printStartMessage();
        Options options = new Options();
        options.addOption("input", true, "input folder path");
        options.addOption("output", true, "output folder path");
        options.addOption("type", true, "conversion type (SOUND_SIMULATION, RADIO_WAVE, INTERFERENCE)");
        options.addOption("inputProj", true, "input proj4 string (default: EPSG:5186)");
        options.addOption("help", false, "show help");

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(options, args);

        String type = commandLine.getOptionValue("type");

        if (commandLine.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.setWidth(200);
            formatter.printHelp("mago-sound-converter", options);
            return;
        }

        String inputFolderPath = commandLine.getOptionValue("input");
        String outputFolderPath = commandLine.getOptionValue("output");
        if (inputFolderPath == null) {
            log.error("Input folder path is not valid.");
            return;
        }
        if (outputFolderPath == null) {
            log.error("Output folder path is not valid.");
            return;
        }

        File inputPath = new File(inputFolderPath);
        File outputPath = new File(outputFolderPath);

        if (inputFolderPath.isEmpty() || !inputPath.isDirectory()) {
            log.error("Input folder path is not valid.");
            return;
        } else if (!inputPath.exists()) {
            log.error("Input folder does not exist.");
            return;
        }

        if (!outputPath.exists() && !outputPath.mkdirs()) {
            log.error("Failed to create output folder.");
            return;
        }

        if (type.equals("SOUND_SIMULATION")) {
            log.info("==============================================");
            log.info("Start Sound Simulation Converter");

            // Sound simulation data.************************************
            // check if exist the inputProj.***
            String inputProj = "+proj=tmerc +lat_0=38 +lon_0=127 +k=1 +x_0=200000 +y_0=600000 +ellps=GRS80 +units=m +no_defs"; // 5186.***
            if (commandLine.hasOption("inputProj")) {
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
        } else if (type.equals("RADIO_WAVE") || type.equals("INTERFERENCE")) {
            log.info("==============================================");
            log.info("Start Radio Wave Converter");

            // Sound simulation data.************************************
            String inputProj;
            if (commandLine.hasOption("inputProj")) {
                inputProj = commandLine.getOptionValue("inputProj");
            } else {
                inputProj = "+proj=tmerc +lat_0=38 +lon_0=127 +k=1 +x_0=200000 +y_0=600000 +ellps=GRS80 +units=m +no_defs"; // 5186.***
            }

            // set the coords of the inputData.***
            CRSFactory factory = new CRSFactory();
            // provisionally set the proj4 as 5186.***
            String proj = inputProj;
            CoordinateReferenceSystem coordinateReferenceSystem = null;
            if (proj != null && !proj.isEmpty()) {
                coordinateReferenceSystem = factory.createFromParameters("CUSTOM", proj);
            } else {
                throw new IllegalArgumentException("Input proj4 is not valid.");
            }

            InterferenceConverter interferenceConverter = new InterferenceConverter(inputPath, outputPath, coordinateReferenceSystem);
            interferenceConverter.convert();
        } else {
            log.error("Conversion type is not supported.");
        }
        printEndMessage();
    }

    public static void printStartMessage() {
        log.info("=========================START==========================");
        log.info("Sound Data Converter : Gaia3D, Inc.");
        log.info("========================================================");
    }

    public static void printEndMessage() {
        log.info("==========================END===========================");
    }
}