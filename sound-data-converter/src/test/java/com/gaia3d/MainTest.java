package com.gaia3d;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class MainTest
{
    @Test
    void main() throws ParseException, IOException {
        String inputFolderPath = "D:\\data\\simulation-data\\SOUND\\newSpecData\\M";
        String outputFolderPath = "D:\\data\\simulation-data\\SOUND\\newSpecData\\output";
        String inputProj = "+proj=tmerc +lat_0=38 +lon_0=127 +k=1 +x_0=200000 +y_0=600000 +ellps=GRS80 +units=m +no_defs"; // 5186.***

        String[] testArgs = new String[]{
                "-type", "SOUND_SIMULATION",
                "-input", inputFolderPath,
                "-output", outputFolderPath,
                "-inputProj", inputProj
        };

        Main.main(testArgs);
    }

    @Test
    void testMain()
    {

    }
}