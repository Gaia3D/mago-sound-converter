package com.gaia3d.sound;

import com.gaia3d.sound.converter.SoundConverterMain;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class MainTest {
    @Test
    void main() throws ParseException, IOException {
        String inputFolderPath = "G:\\datas\\noise";
        String outputFolderPath = "G:\\datas\\noise\\output";
        String inputProj = "+proj=tmerc +lat_0=38 +lon_0=127 +k=1 +x_0=200000 +y_0=600000 +ellps=GRS80 +units=m +no_defs"; // 5186.***

        String[] testArgs = new String[]{"-type", "SOUND_SIMULATION", "-input", inputFolderPath, "-output", outputFolderPath, "-inputProj", inputProj};

        SoundConverterMain.main(testArgs);
    }

    @Test
    void main2() throws ParseException, IOException {
        String inputFolderPath = "G:\\datas\\radio_wave";
        String outputFolderPath = "G:\\datas\\radio_wave\\output";
        String inputProj = "+proj=tmerc +lat_0=38 +lon_0=127 +k=1 +x_0=200000 +y_0=600000 +ellps=GRS80 +units=m +no_defs"; // 5186.***

        String[] testArgs = new String[]{"-type", "RADIO_WAVE", "-input", inputFolderPath, "-output", outputFolderPath, "-inputProj", inputProj};

        SoundConverterMain.main(testArgs);
    }
}