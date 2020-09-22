package uk.ac.ed.inf.heatmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Arrays;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        System.out.println( "First parameter is " + args[0] );
        String[] predictions = parsePredictions("src/main/resources/predictions.txt");
        System.out.println("The predictions are: " + Arrays.toString(predictions));
    }

    public static String[] parsePredictions(String fileName) {
        String[] predictions = new String[100];

        try {
            File predictionsFile = new File(fileName);
            Scanner fileReader = new Scanner(predictionsFile);
            int i = 0;
            while (fileReader.hasNextLine()) {
              // each row expected to have 10 sensor predictions
              String row = fileReader.nextLine();
              String[] rowValues = row.split(",");
              System.out.println(row);
              for (int j = 0 ; j < 10; j++) {
                  predictions[i] = rowValues[j];
                  i++;
              }
            }
            fileReader.close();


          } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
        return predictions;
    }
}
