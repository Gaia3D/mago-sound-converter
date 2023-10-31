package com.gaia3d;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainTest
{
    @Test
    void main() throws ParseException {
        String inputFolderPath = "D:\\data\\simulation-data\\SOUND\\newSpecData\\M";
        String outputFolderPath = "D:\\data\\simulation-data\\SOUND\\newSpecData\\output";


        String[] testArgs = new String[]{
                "-type", "SOUND_SIMULATION",
                "-input", inputFolderPath,
                "-output", outputFolderPath
        };

        Main.main(testArgs);
    }

    @Test
    void testMain()
    {

    }
}