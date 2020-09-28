package uk.ac.ed.inf.heatmap;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Utilities {

    public static String readFile(String filePath) {
        Path file = Path.of(filePath);
        String fileContent = new String();

        try {
            fileContent = Files.readString(file);
        } catch (Exception e) {
            System.out.println("Error occured during reading of file: " + filePath + "\n" + e);
            System.exit(1);
        }

        return fileContent;
    }

    public static int[][] parsePredictions(String filePath) {
        int[][] predictions = new int[10][10];
        Path predictionsFile = Path.of(filePath);
        try {
            BufferedReader predictionsFileReader = Files.newBufferedReader(predictionsFile);
            String readLine = predictionsFileReader.readLine();
            int i = 0;
            while(readLine != null && i < predictions.length) {
                String[] rowValues = readLine.split(",");
                for (int j = 0 ; j < 10; j++) {
                    int prediction = Integer.parseInt(rowValues[j].replaceAll("\\s+",""));
                    if ( prediction < 0 || prediction >= 256) {
                        System.out.println("prediction value must be in range [0,256)");
                        throw new Exception("");
                    }
                    predictions[i][j] = prediction;
                }
                i++;
                readLine = predictionsFileReader.readLine();
            }
        } catch (Exception e) {
            System.out.println("Error occurred during parsing of predictions file: " + filePath + "\n" + e);
            System.exit(1);
        }

        return predictions;
    }
}
