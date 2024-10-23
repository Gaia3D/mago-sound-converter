package com.gaia3d.sound.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaia3d.sound.dataStructure.radiowave.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class InterferenceConverter {

    private static final String INPUT_FILE_EXTENSION = ".CSV";
    private static final String RADIO_FILE_NAME = "M_RI";
    private static final String TV_FILE_NAME = "M_TVI";
    private static final String MAGNETIC_FILE_NAME = "M_MF";
    private static final String MAGNETIC_CONTOUR_FILE_NAME = "M_MF_C";

    private final File inputDirectory;
    private final File outputDirectory;

    public void convert(String input, String output) {
        if (!inputDirectory.exists()) {
            log.error("Input file does not exist: {}", inputDirectory);
            throw new IllegalArgumentException("Input file does not exist: " + inputDirectory);
        }
        if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
            log.error("Failed to create output directory: {}", outputDirectory);
            throw new IllegalArgumentException("Failed to create output directory: " + outputDirectory);
        }
        Map<InterferenceType, File> interferenceMap = searchInputFiles();
        interferenceMap.forEach(this::readInterferenceFile);
    }

    private Map<InterferenceType, File> searchInputFiles() {
        File[] files = inputDirectory.listFiles();
        if (files == null) {
            log.error("No files found in the input directory: {}", inputDirectory);
            throw new IllegalArgumentException("No files found in the input directory: " + inputDirectory);
        }

        Map<InterferenceType, File> interferenceMap = new HashMap<>();

        for (File file : files) {
            if (file.isDirectory()) {
                //searchInputFiles(file);
            } else {
                String fileName = file.getName();
                if (fileName.endsWith(INPUT_FILE_EXTENSION)) {
                    if (fileName.contains(RADIO_FILE_NAME)) {
                        log.info("Found radio wave file: {}", fileName);
                        InterferenceType interferenceType = InterferenceType.RADIO;
                        interferenceMap.put(interferenceType, file);
                    } else if (fileName.contains(TV_FILE_NAME)) {
                        log.info("Found TV interference file: {}", fileName);
                        InterferenceType interferenceType = InterferenceType.TV;
                        interferenceMap.put(interferenceType, file);
                    } else if (fileName.contains(MAGNETIC_CONTOUR_FILE_NAME)) {
                        log.info("Found magnetic field contour file: {}", fileName);
                        InterferenceType interferenceType = InterferenceType.MAGNETIC_FIELD_CONTOUR;
                        interferenceMap.put(interferenceType, file);
                    } else if (fileName.contains(MAGNETIC_FILE_NAME)) {
                        log.info("Found magnetic field file: {}", fileName);
                        InterferenceType interferenceType = InterferenceType.MAGNETIC_FIELD;
                        interferenceMap.put(interferenceType, file);
                    }
                }
            }
        }

        return interferenceMap;
    }

    private void readInterferenceFile(InterferenceType interferenceType, File file) {
        if (interferenceType == InterferenceType.RADIO) {
            List<ModelRadio> radioList = readRadioWaveFile(file);
            log.info("Read {} radio wave data", radioList.size());
            String radioWave = toJsonRadioWave(radioList);
            writeJsonFile("M_RI.json", radioWave);
        } else if (interferenceType == InterferenceType.TV) {
            List<ModelTV> tvList = readTVInterferenceFile(file);
            log.info("Read {} TV interference data", tvList.size());
            toJsonTVInterference(tvList);
            String tvInterference = toJsonTVInterference(tvList);
            writeJsonFile("M_TVI.json", tvInterference);
        } else if (interferenceType == InterferenceType.MAGNETIC_FIELD) {
            List<ModelMagneticField> magneticFieldList  = readMagneticFieldFile(file);
            log.info("Read {} magnetic field data", magneticFieldList.size());
            String magneticField = toJsonMagneticField(magneticFieldList);
            writeJsonFile("M_MF.json", magneticField);
        } else if (interferenceType == InterferenceType.MAGNETIC_FIELD_CONTOUR) {
            List<ModelMagneticFieldContour> magneticFieldContourList = readMagneticFieldContourFile(file);
            log.info("Read {} magnetic field contour data", magneticFieldContourList.size());
            String magneticFieldContour = toJsonMagneticFieldContour(magneticFieldContourList);
            writeJsonFile("M_MF_C.json", magneticFieldContour);
        }
    }

    private List<ModelRadio> readRadioWaveFile(File file) {
        // Read the radio wave file
        List<ModelRadio> radioList = new ArrayList<>();
        int columnCount = InterferenceType.RADIO.getColumnCount();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            int lineCount = 0;
            String line;
            while ((line = br.readLine()) != null) {
                if (lineCount < 3) {
                    lineCount++;
                    continue;
                }

                String data = line.trim();
                String[] split = data.split(",");

                if (split.length != columnCount) {
                    log.error("Invalid magnetic field data: {}, {}/{}", data, split.length, columnCount);
                    continue;
                }
                ModelRadio radio = ModelRadio.of(split);
                radioList.add(radio);
                lineCount++;
            }
        } catch (IOException e) {
            log.error("Failed to read radio wave file: {}", file, e);
        }
        return radioList;
    }

    private List<ModelTV> readTVInterferenceFile(File file) {
        // Read the TV interference file
        List<ModelTV> tvList = new ArrayList<>();
        int columnCount = InterferenceType.TV.getColumnCount();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            int lineCount = 0;
            String line;
            while ((line = br.readLine()) != null) {
                if (lineCount < 3) {
                    lineCount++;
                    continue;
                }

                String data = line.trim();
                String[] split = data.split(",");

                if (split.length != columnCount) {
                    log.error("Invalid magnetic field data: {}, {}/{}", data, split.length, columnCount);
                    continue;
                }
                ModelTV tv = ModelTV.of(split);
                tvList.add(tv);
                lineCount++;
            }
        } catch (IOException e) {
            log.error("Failed to read TV interference file: {}", file, e);
        }
        return tvList;
    }

    private List<ModelMagneticField> readMagneticFieldFile(File file) {
        // Read the magnetic field file
        List<ModelMagneticField> magneticFieldList = new ArrayList<>();
        int columnCount = InterferenceType.MAGNETIC_FIELD.getColumnCount();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            int lineCount = 0;
            String line;
            while ((line = br.readLine()) != null) {
                if (lineCount < 3) {
                    lineCount++;
                    continue;
                }

                String data = line.trim();
                String[] split = data.split(",");

                if (split.length != columnCount) {
                    log.error("Invalid magnetic field data: {}, {}/{}", data, split.length, columnCount);
                    continue;
                }
                ModelMagneticField magneticField = ModelMagneticField.of(split);
                magneticFieldList.add(magneticField);
                lineCount++;
            }
        } catch (IOException e) {
            log.error("Failed to read magnetic field file: {}", file, e);
        }
        return magneticFieldList;
    }

    private List<ModelMagneticFieldContour> readMagneticFieldContourFile(File file) {
        // Read the magnetic field contour file
        List<ModelMagneticFieldContour> magneticFieldContourList = new ArrayList<>();
        int columnCount = InterferenceType.MAGNETIC_FIELD_CONTOUR.getColumnCount();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            int lineCount = 0;
            String line;
            while ((line = br.readLine()) != null) {
                if (lineCount < 3) {
                    lineCount++;
                    continue;
                }

                String data = line.trim();
                String[] split = data.split(",");

                if (split.length != columnCount) {
                    log.error("Invalid magnetic field data: {}, {}/{}", data, split.length, columnCount);
                    continue;
                }
                ModelMagneticFieldContour magneticFieldContour = ModelMagneticFieldContour.of(split);
                magneticFieldContourList.add(magneticFieldContour);
                lineCount++;
            }
        } catch (IOException e) {
            log.error("Failed to read magnetic field contour file: {}", file, e);
        }
        return magneticFieldContourList;
    }

    private String toJsonRadioWave(List<ModelRadio> radioList) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Model model = Model.builder()
                    .values(radioList)
                    .build();
            return objectMapper.writeValueAsString(model);
        } catch (IOException e) {
            log.error("Failed to convert radio wave data to JSON", e);
            return null;
        }
    }

    private String toJsonTVInterference(List<ModelTV> tvList) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Model model = Model.builder()
                    .values(tvList)
                    .build();
            return objectMapper.writeValueAsString(model);
        } catch (IOException e) {
            log.error("Failed to convert TV interference data to JSON", e);
            return null;
        }
    }

    private String toJsonMagneticField(List<ModelMagneticField> magneticFieldList) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Model model = Model.builder()
                    .values(magneticFieldList)
                    .build();
            return objectMapper.writeValueAsString(model);
        } catch (IOException e) {
            log.error("Failed to convert magnetic field data to JSON", e);
            return null;
        }
    }

    private String toJsonMagneticFieldContour(List<ModelMagneticFieldContour> magneticFieldContourList) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Model model = Model.builder()
                    .values(magneticFieldContourList)
                    .build();
            return objectMapper.writeValueAsString(model);
        } catch (IOException e) {
            log.error("Failed to convert magnetic field contour data to JSON", e);
            return null;
        }
    }

    private void writeJsonFile(String fileName, String json) {
        // Write the JSON file
        File outputFile = new File(this.outputDirectory, fileName);
        try {

            FileUtils.writeStringToFile(outputFile, json, StandardCharsets.UTF_8);
            log.info("Write JSON file: {}", outputFile);
        } catch (IOException e) {
            log.error("Failed to write JSON file: {}", outputFile, e);
        }
    }
}
