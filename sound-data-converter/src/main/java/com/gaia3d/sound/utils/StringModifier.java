package com.gaia3d.sound.utils;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class StringModifier {
    public static Optional<String> getExtensionByStringHandling(String filename) {
        // https://www.baeldung.com/java-file-extension
        return Optional.ofNullable(filename).filter(f -> f.contains(".")).map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    public static String getLastNameFromPath(String folderPath) {
        String folderName = "";
        int lastIndexOf = folderPath.lastIndexOf(File.separator);
        if (lastIndexOf >= 0) {
            folderName = folderPath.substring(lastIndexOf + 1);
        }
        return folderName;
    }

    public static boolean checkStringCoincidences(String word, List<String> vecStrings, boolean bIgnoreCase) {
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

    public static void getFileNamesInFolder(String folderPath, List<String> vecFileExtensions, List<String> vecFileNames) {
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

    public static String getRawFileName(String fileName) {
        String rawFileName = fileName.substring(0, fileName.lastIndexOf('.'));
        return rawFileName;
    }

    public static boolean createAllFoldersIfNoExist(String filePath) {
        File file = new File(filePath);
        return file.mkdirs();
    }
}
