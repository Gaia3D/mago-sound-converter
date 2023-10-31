package com.gaia3d.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

public class StringModifier
{
    public static Optional<String> getExtensionByStringHandling(String filename) {
        // https://www.baeldung.com/java-file-extension
        return Optional.ofNullable(filename).filter(f -> f.contains(".")).map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    public static boolean checkStringCoincidences(String word, ArrayList<String> vecStrings, boolean bIgnoreCase) {
        int stringsCount = vecStrings.size();
        for (int i = 0; i < stringsCount; i++) {
            if (bIgnoreCase) {
                if (word.equalsIgnoreCase(vecStrings.get(i))) {
                    return true;
                }
            } else {
                if (word.equals(vecStrings.get(i))) {
                    return true;
                }
            }

        }
        return false;
    }
    public static void getFileNamesInFolder(String folderPath, ArrayList<String> vecFileExtensions, ArrayList<String> vecFileNames) {
        File folder = new File(folderPath);

        // Populates the array with names of files and directories
        File[] listOfFiles = folder.listFiles();
        int filesCount = listOfFiles.length;
        boolean bIgnoreCase = true; // ignore char upperCase & lowerCase.***
        for (int i = 0; i < filesCount; i++) {
            File file = listOfFiles[i];

            // check if is a file or folder.***
            if (file.isFile()) {
                /* is a file.*** */
                String fileName = file.getName();
                Optional<String> optExtension = getExtensionByStringHandling(fileName);
                if (optExtension.isPresent()) {
                    // now check if the extension is coincident with wanted extension.***
                    String extension = optExtension.get();
                    if (checkStringCoincidences(extension, vecFileExtensions, bIgnoreCase)) {
                        vecFileNames.add(fileName);
                    }
                }
            }
        }

    }
}
