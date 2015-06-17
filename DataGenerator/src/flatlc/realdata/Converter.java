package flatlc.realdata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;


/**
 * Uses real data from Zillow, NBA, etc.
 * Based on the implementation of LVDN14, APSkyline
 */
public class Converter {


    //    private String filename;
//    private String input;
//    private String output;
//    private int cols;
//    private int rows;

//    private ArrayList<int[]> data = new ArrayList<>();


//    public Converter(String input, String output, int cols, int rows) {
//        this.input = input;
//        this.output = output;
//        this.cols = cols;
//        this.rows = rows;
//                                 }

    /**
     * findmax values for the data set
     * This is equal to set the maxLevels
     */
    public static String getMaxValues(String filename, int cols, int rows) {

        ArrayList<int[]> rawData = Converter.readRawData(filename, cols, rows);

        int[] tuple;

        int[] max = new int[cols];

        for (int i = 0; i < rawData.size(); i++) {
            tuple = rawData.get(i);
            setMax(max, tuple);
        }


        String out = "";
        for (int i = 0; i < max.length; i++)
            out += max[i] + " ";


        return out;
    }

//    private void setMin(double[] min, double[] tuple) {
//        if (min.length != tuple.length)
//            throw new RuntimeException("Wrong size of arrays");
//        for (int i = 0; i < min.length; i++) {
//            min[i] = min[i] < tuple[i] ? min[i] : tuple[i];
//        }
//    }


    // note that the first component is the ID
    private static void setMax(int[] max, int[] tuple) {
        if (max.length != tuple.length)
            throw new RuntimeException("Wrong size of arrays");
        for (int i = 0; i < max.length; i++) {
            max[i] = max[i] > tuple[i] ? max[i] : tuple[i];
        }

    }


    private static String getString(int[] val) {

        StringBuffer sb = new StringBuffer();

        for (int i = 1; i < val.length; i++) {
            if (i != 4) {
                sb.append(val[i]);
                sb.append(" ");
            }
        }

        return sb.toString();
    }

    public static void convertZillow(ArrayList<int[]> data, String output) {
        try {
            FileWriter writer = new FileWriter(new File(output));
//            int bedroom = 0;
//            int bathroom = 0;

            for (int i = 0; i < data.size(); i++) {
                int[] val = data.get(i);

//                if(val[1] > 10)
//                    bedroom++;
//
//                if(val[2] > 10)
//                    bathroom++;

//                if(val[1] > 100)
//                    val[1] = val[1]/100;
//                if(val[1]>10)
//                    System.out.println("bedroom: " + val[1]);
//                val[1] = 5;
////
//                if(val[2] > 100)
//                    val[2] = val[2]/ 100;
//                if(val[2]>10)
//                    System.out.println("bathroom: " + val[2]);
//                val[2] = 5;
//
                // in km^2
                val[3] = (int) (val[3] * 0.09 / 1000);
//
////
//                val[4] = (int)(val[4] * 0.09 * 0.001 * 0.01);
//
//                if (val[4] >= 20)
//                    val[4] = val[4] / 10;
////
                // age
                val[5] = 2014 - val[5];
                if (val[5] == 2014 || val[5] > 45)
                    val[5] = 45;

                String s = getString(val);

                if (val[1] <= 10 && val[2] <= 10) {
                    writer.write(s);
                    writer.write("\n");
                }
            }


            writer.flush();
            writer.close();

//            System.out.println("Bedroom: " + bedroom);
//            System.out.println("Bathroom: " + bathroom);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Open file and initiate scanner /**
     *
     * @return sc
     */
    private static Scanner getScanner(String filename) {
        Scanner sc;
        try {
            sc = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unable to read file: " + filename);
        }
        sc.useLocale(Locale.US);
        return sc;
    }


    private static ArrayList<int[]> readRawData(String filename, int cols, int rows) {

        ArrayList<int[]> rawData = new ArrayList<>();
        Scanner sc = Converter.getScanner(filename);

        // read raw data into ArrayList
        for (int i = 0; i < rows; ++i) {
//            sc.nextInt();
            int[] tuple = new int[cols];
            for (int j = 0; j < cols; ++j) {
                double val = sc.nextDouble();

                tuple[j] = (int) val;
            }
            rawData.add(tuple);
            sc.nextLine();
        }

        sc.close();
        return rawData;

    }


    public static void main(String[] args) {
        String canonicalPath = null;
        try {
            canonicalPath = new File(".").getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String prefix = canonicalPath + "/FlatLCDataGenerator/data/";


        //    private String prefix;
        String NBA = "5d-nba-17265.txt";
        String HOU = "6d-hou-127931.txt";
        String ZILLOW = "ZillowData.txt";
        int ZILLOW_SIZE = 2245109;
//        int ZILLOW_SIZE = 2000000;
        String TEST = "test.txt";


        String inputFile = prefix + ZILLOW;
        String outputFile = prefix + "zillow_mod.txt";

//        ArrayList<int[]> input = Converter.readRawData(inputFile, 6, ZILLOW_SIZE);
//        Converter.convertZillow(input, outputFile);
        System.out.println("Max: " + Converter.getMaxValues(outputFile, 4, 2236252));

    }


}
