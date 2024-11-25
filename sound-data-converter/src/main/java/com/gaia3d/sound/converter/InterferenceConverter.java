package com.gaia3d.sound.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaia3d.sound.dataStructure.radiowave.*;
import com.gaia3d.sound.utils.io.LittleEndianDataInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.locationtech.proj4j.BasicCoordinateTransform;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.ProjCoordinate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class InterferenceConverter {

    private static final String INPUT_FILE_EXTENSION = ".OUT";
    private static final String RADIO_FILE_NAME = "M_RI";
    private static final String TV_FILE_NAME = "M_TVI";
    private static final String MAGNETIC_FILE_NAME = "M_MF";
    private static final String MAGNETIC_CONTOUR_FILE_NAME = "M_MF_C";

    private final File inputDirectory;
    private final File outputDirectory;
    private final CoordinateReferenceSystem coordinateReferenceSystem;

    public void convert() {
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
            int gridX = 253;
            int gridY = 292;
            List<ModelMagneticFieldContour> magneticFieldContourList = readMagneticFieldContourFile(file);
            log.info("Read {} magnetic field contour data", magneticFieldContourList.size());
            BufferedImage contourImage = drawMagneticFieldContour(magneticFieldContourList, gridX, gridY);
            writeBufferedImage(contourImage, "M_MF_C.png");
            //String magneticFieldContour = toJsonMagneticFieldContour(magneticFieldContourList);
            //writeJsonFile("M_MF_C.json", magneticFieldContour);
        }
    }

    private List<ModelRadio> readRadioWaveFile(File file) {
        // Read the radio wave file
        List<ModelRadio> radioList = new ArrayList<>();
        //int columnCount = InterferenceType.RADIO.getColumnCount();
        try (LittleEndianDataInputStream dis = new LittleEndianDataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            /*
                N (int, 4byte)	개수
                index (int, 4byte)	index
                x (float, 4byte)	x좌표
                y (float, 4byte)	y좌표
                RH (float, 4byte)	라디오 안테나 높이
                value (float, 4byte)	청명시 라디오 수신장해 예측결과
                SNR (float, 4byte)	청명시 라디오 전계장도 대장 해비 예측결과
                rank (int, 4byte)	청명시 라디오 수신 품질등급 평가결과
                value_r (float, 4byte)	강우시 라디오 수신장해 예측결과
                SNR_r (float, 4byte)	강우시 라디오 전계강도 대장 해비 예측결과
                rank_r (int, 4byte)	강우시 라디오 수신 품질등급 평가결과
                dist_min (float, 4byte)	최근접 송진선로와의 이격거리
                in (int, 4byte)	지형모델링 영역 내 존재여부 (여 : 1, 부 : 0)
             */

            int count = dis.readInt();
            for (int i = 0; i < count; i++) {
                int index = dis.readInt();
                float x = dis.readFloat();
                float y = dis.readFloat();
                float relativeHeight = dis.readFloat();
                float value = dis.readFloat();
                float snr = dis.readFloat();
                int rank = dis.readInt();
                float valueR = dis.readFloat();
                float snrR = dis.readFloat();
                int rankR = dis.readInt();
                float distMin = dis.readFloat();
                int in = dis.readInt();

                ModelRadio radio = ModelRadio.builder()
                        .index(index)
                        .x(x)
                        .y(y)
                        .relativeHeight(relativeHeight)
                        .value(value)
                        .snr(snr)
                        .rank(rank)
                        .valueInRain(valueR)
                        .snrInRain(snrR)
                        .rankInRain(rankR)
                        .nearestDistance(distMin)
                        .in(in).build();
                //ModelRadio radio = ModelRadio.of(dis);
                radioList.add(radio);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("Failed to read radio wave file: {}", file, e);
            throw new RuntimeException(e);
        }


        /*try (BufferedReader br = new BufferedReader(new FileReader(file))) {
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
        }*/
        return radioList;
    }

    private List<ModelTV> readTVInterferenceFile(File file) {
        // Read the TV interference file
        List<ModelTV> tvList = new ArrayList<>();
        try (LittleEndianDataInputStream dis = new LittleEndianDataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            /*
                N (int, 4byte)	개수
                index (int, 4byte)	index
                x (float, 4byte)	x좌표
                y (float, 4byte)	y좌표
                RH (float, 4byte)	TV 안테나 높이
                value (float, 4byte)	TV 수신장해 예측결과
                SNR (float, 4byte)	TV 전계강도대장해비 예측결과
                rank (int, 4byte)	TV 수신 품질등급 평가결과
                dist_min (float, 4byte)	최근접 송진선로와의 이격거리
                in (int, 4byte)	지형모델링 영역 내 존재여부 (여 : 1, 부 : 0)
             */
            int count = dis.readInt();
            for (int i = 0; i < count; i++) {
                int index = dis.readInt();
                float x = dis.readFloat();
                float y = dis.readFloat();
                float relativeHeight = dis.readFloat();
                float value = dis.readFloat();
                float snr = dis.readFloat();
                int rank = dis.readInt();
                float distMin = dis.readFloat();
                int in = dis.readInt();
                ModelTV tv = ModelTV.builder()
                        .index(index)
                        .x(x)
                        .y(y)
                        .relativeHeight(relativeHeight)
                        .value(value)
                        .snr(snr)
                        .rank(rank)
                        .nearestDistance(distMin)
                        .in(in).build();
                tvList.add(tv);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("Failed to read radio wave file: {}", file, e);
            throw new RuntimeException(e);
        }


        /*int columnCount = InterferenceType.TV.getColumnCount();
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
        }*/
        return tvList;
    }

    private List<ModelMagneticField> readMagneticFieldFile(File file) {
        // Read the magnetic field file
        List<ModelMagneticField> magneticFieldList = new ArrayList<>();
        try (LittleEndianDataInputStream dis = new LittleEndianDataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            /*
                N (int, 4byte)	개수
                index (int, 4byte)	index
                x (float, 4byte)	x좌표
                y (float, 4byte)	y좌표
                RH (float, 4byte)	지상고
                dist2d (float, 4byte)	주거지와의 수평거리
                H_src (float, 4byte)	전선의 지상고
                Value[uT] (float, 4byte)	자계 예측 결과
                ok (int, 4byte)	판정결과(기준 만족 : 1, 기준 불만족 : 0)
                ref (float, 4byte)	자계 평가 기준
                in (int, 4byte)	지형모델링 영역 내 존재여부 (여 : 1, 부 : 0)
            */
            int count = dis.readInt();
            for (int i = 0; i < count; i++) {
                int index = dis.readInt();
                int x = dis.readInt();
                int y = dis.readInt();
                float relativeHeight = dis.readFloat();
                float distanceFromDwelling = dis.readFloat();
                float heightOfWire = dis.readFloat();
                float value = dis.readFloat();
                int result = dis.readInt();
                float ref = dis.readFloat();
                int in = dis.readInt();
                ModelMagneticField magneticField = ModelMagneticField.builder()
                        .index(index)
                        .x(x)
                        .y(y)
                        .relativeHeight(relativeHeight)
                        .distanceFromDwelling(distanceFromDwelling)
                        .heightOfWire(heightOfWire)
                        .value(value)
                        .result(result)
                        .ref(ref)
                        .in(in).build();
                magneticFieldList.add(magneticField);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /*int columnCount = InterferenceType.MAGNETIC_FIELD.getColumnCount();
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
        }*/
        return magneticFieldList;
    }

    private void writeBufferedImage(BufferedImage image, String fileName) {
        File outputFile = new File(this.outputDirectory, fileName);
        try {
            ImageIO.write(image, "png", outputFile);
            log.info("Write image file: {}", outputFile);
        } catch (IOException e) {
            log.error("Failed to write image file: {}", outputFile, e);
        }
    }

    private BufferedImage drawMagneticFieldContour(List<ModelMagneticFieldContour> magneticFieldContourList, int gridX, int gridY) {
        // Draw the magnetic field contour
        BufferedImage image = new BufferedImage(gridX, gridY, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, gridX, gridY);

        float minValue = magneticFieldContourList.stream().map(ModelMagneticFieldContour::getValue).min(Float::compareTo).orElse(0.0f);
        float maxValue = magneticFieldContourList.stream().map(ModelMagneticFieldContour::getValue).max(Float::compareTo).orElse(0.0f);

        float minX = magneticFieldContourList.stream().map(ModelMagneticFieldContour::getX).min(Float::compareTo).orElse(0.0f);
        float maxX = magneticFieldContourList.stream().map(ModelMagneticFieldContour::getX).max(Float::compareTo).orElse(0.0f);
        float minY = magneticFieldContourList.stream().map(ModelMagneticFieldContour::getY).min(Float::compareTo).orElse(0.0f);
        float maxY = magneticFieldContourList.stream().map(ModelMagneticFieldContour::getY).max(Float::compareTo).orElse(0.0f);

        for (ModelMagneticFieldContour magneticFieldContour : magneticFieldContourList) {
            int index = magneticFieldContour.getIndex();
            int[] gridPosition = getGridPositionFromIndex(index, gridX, gridY);
            int x = gridPosition[0];
            int y = gridPosition[1];

            //int x = (int) magneticFieldContour.getX();
            //int y = (int) magneticFieldContour.getY();
            float value = magneticFieldContour.getValue();
            float quantizedValue = value / (maxValue - minValue);

            int color = getColorByValue(quantizedValue);

            //int color = value > 0 ? 0xFF0000 : 0x0000FF;
            //int color = getColorIntFromRGB((int) (quantizedValue * 255), (int) (quantizedValue * 255), (int) (quantizedValue * 255));

            // flip the y-axis
            y = gridY - y - 1;
            image.setRGB(x, y, color); //

        }

        log.info("minValue: {}, maxValue: {}, minX: {}, maxX: {}, minY: {}, maxY: {}", minValue, maxValue, minX, maxX, minY, maxY);

        CRSFactory crsFactory = new CRSFactory();
        CoordinateReferenceSystem source = coordinateReferenceSystem;
        CoordinateReferenceSystem target =  crsFactory.createFromName("EPSG:4326");
        ProjCoordinate minCoordinate = new ProjCoordinate(minX, minY);
        ProjCoordinate maxCoordinate = new ProjCoordinate(maxX, maxY);

        BasicCoordinateTransform transformer = new BasicCoordinateTransform(source, target);
        ProjCoordinate minResult = new ProjCoordinate();
        ProjCoordinate maxResult = new ProjCoordinate();
        transformer.transform(minCoordinate, minResult);
        transformer.transform(maxCoordinate, maxResult);

        log.info("minResult: {}, maxResult: {}", minResult, maxResult);


        return image;
    }

    /*
    * NOISE_LEGEND.DEFAULT_LEGENDS = [
            {min : 70, max : Number.MAX_SAFE_INTEGER, color : new Cesium.Color(1, 0, 0, 1)},
            {min : 65, max : 70, color : new Cesium.Color(1, 0.4, 0, 1)},
            {min : 60, max : 65, color : new Cesium.Color(1, 0.84, 0, 1)},
            {min : 55, max : 60, color : new Cesium.Color(0.04, 0.74, 0.04, 1)},
            {min : 50, max : 55, color : new Cesium.Color(0.05, 0.47, 0.89, 1)},
            {min : 0, max : 50, color : new Cesium.Color(0, 0.10, 0.89, 1)},
            {min : Number.MIN_SAFE_INTEGER, max : 0, color : new Cesium.Color(0, 0.10, 0.89, 1)},
        ];
    */
    private int getColorByValue(float value) {
        if (value >= 0.9) {
            return getColorIntFromRGB(255, 0, 0);
        } else if (value >= 0.8) {
            return getMidColor(getColorIntFromRGB(255, 0, 0), getColorIntFromRGB(255, 102, 0));
        } else if (value >= 0.7) {
            return getColorIntFromRGB(255, 102, 0);
        } else if (value >= 0.6) {
            return getMidColor(getColorIntFromRGB(255, 102, 0), getColorIntFromRGB(255, 215, 0));
        } else if (value >= 0.5) {
            return getColorIntFromRGB(255, 215, 0);
        } else if (value >= 0.4) {
            return getMidColor(getColorIntFromRGB(255, 215, 0), getColorIntFromRGB(4, 189, 4));
        } else if (value >= 0.3) {
            return getColorIntFromRGB(10, 189, 10);
        } else if (value >= 0.2) {
            return getMidColor(getColorIntFromRGB(10, 189, 10), getColorIntFromRGB(13, 120, 228));
        }else if (value >= 0.1) {
            return getColorIntFromRGB(13, 120, 228);
        } else if (value >= 0.001) {
            return getColorIntFromRGB(0, 26, 228);
        } else {
            return getColorIntFromRGB(255/4, 255/4, 255/4);
        }
    }

    private int getMidColor(int color1, int color2) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int r = (r1 + r2) / 2;
        int g = (g1 + g2) / 2;
        int b = (b1 + b2) / 2;
        return getColorIntFromRGB(r, g, b);
    }

    private int getColorIntFromRGB(int r, int g, int b) {
        return (r << 16) | (g << 8) | b;
    }

    private int[] getGridPositionFromIndex(int index, int gridX, int gridY) {
        int x = index % gridX;
        int y = index / gridX;
        return new int[]{x, y};
    }

    private List<ModelMagneticFieldContour> readMagneticFieldContourFile(File file) {
        // Read the magnetic field contour file
        List<ModelMagneticFieldContour> magneticFieldContourList = new ArrayList<>();
        try (LittleEndianDataInputStream dis = new LittleEndianDataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            /*
                Nx (int, 4byte)	x방향 개수
                Ny (int, 4byte)	y방향 개수
                index (int, 4byte)	index
                x (float, 4byte)	x좌표
                y (float, 4byte)	y좌표
                RH (float, 4byte)	지상고
                uT	자계 예측 결과
            */
            int nx = dis.readInt();
            int ny = dis.readInt();
            for (int i = 0; i < nx * ny; i++) {
                int index = dis.readInt();
                float x = dis.readFloat();
                float y = dis.readFloat();
                float relativeHeight = dis.readFloat();
                float value = dis.readFloat();
                ModelMagneticFieldContour magneticFieldContour = ModelMagneticFieldContour.builder()
                        .index(index)
                        .x(x)
                        .y(y)
                        .relativeHeight(relativeHeight)
                        .value(value).build();
                magneticFieldContourList.add(magneticFieldContour);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /*int columnCount = InterferenceType.MAGNETIC_FIELD_CONTOUR.getColumnCount();
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
        }*/
        return magneticFieldContourList;
    }

    private String toJsonRadioWave(List<ModelRadio> radioList) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Model model = Model.builder()
                    .name("M_RI")
                    .length(radioList.size())
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
                    .name("M_TVI")
                    .length(tvList.size())
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
                    .name("M_MF")
                    .length(magneticFieldList.size())
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
                    .name("M_MF_C")
                    .length(magneticFieldContourList.size())
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
