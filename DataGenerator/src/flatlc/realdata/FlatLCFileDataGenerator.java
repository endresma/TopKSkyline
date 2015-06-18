/*
 * Copyright (c) 2015. markus endres, timotheus preisinger
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package flatlc.realdata;

import flatlc.inputrelations.FlatLCResultSetA;
import flatlc.inputrelations.RandomResultSetMetaData;
import flatlc.levels.FlatLevelCombination;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;


/**
 * Uses real data from Zillow, NBA, etc.
 * Based on the implementation of LVDN14, APSkyline
 */
public class FlatLCFileDataGenerator extends FlatLCResultSetA {

    //    private String prefix;
//    private final String NBA = "5d-nba-17265.txt";
//    private final String HOU = "6d-hou-127931.txt";
//    private String ZILLOW = "zillow_mod_Scalagon.txt";
//    private final String TEST = "test.txt";


    /**
     * meta data object
     */
    ResultSetMetaData meta;

    private String filename;
    private int col;
    // rows is in the super class
//    private int rows;
    int currentRowInMem = 0;


    private ArrayList<Object> elements;

    private ArrayList<int[]> rawData = new ArrayList<>();
    // min and max value for each column
//    private double[] minLevels;
    private int[] maxValues;

    // multIDs
    private int[] multIDs;


//    public FlatLCFileDataGenerator(String filename, int inputSize, int[] maxValues) {
//        this(filename, maxValues.length, inputSize);
//
//    }


//    public FlatLCFileDataGenerator(String filename, int col, int rows) {
//        this.dataset = filename;
//        this.filename = getFilePath(filename);
//
//        // remove ID column, i.e. the first column of the data set
////        this.col = maxLevels.length;
////        this.rows = inputSize;
//        this.maxValues = maxValues;
//        this.col = col - 1;
//        this.rows = rows;
//
////        this.minLevels = new double[col];
//
//        // create multIDs
//        // compute the multiplicators
//        // note that the first component is the ID of the tuple
////        int len = this.col;
////        this.maxValues = new int[len];
//
//        readRawData();
//
////        setMinMax();
////        this.multIDs = new int[col];
////        this.multIDs[col - 1] = 1;
////        for (int i = col; --i > 0; ) {
////            this.multIDs[i - 1] = this.multIDs[i] * (maxValues[i] + 1);
////        }
//        convert();
//    }

    public FlatLCFileDataGenerator(String filename, int inputSize, int[] maxValues) {


        this.filename = getFilePath(filename);

        // remove ID column, i.e. the first column of the data set
//        this.col = maxLevels.length;
//        this.rows = inputSize;
        this.maxValues = maxValues;
        this.col = maxValues.length;
        this.rows = inputSize;

//        this.minLevels = new double[col];

        // create multIDs
        // compute the multiplicators
        // note that the first component is the ID of the tuple
//        int len = this.col;
//        this.maxValues = new int[len];

        this.multIDs = new int[col];
        this.multIDs[col - 1] = 1;
        for (int i = col; --i > 0; ) {
            this.multIDs[i - 1] = this.multIDs[i] * (maxValues[i] + 1);
        }

        readRawData();

        convert();
    }


    private String getFilePath(String filename) {
        String canonicalPath = null;
        String OS = null;
        String prefix = null;
        try {
            canonicalPath = new File(".").getCanonicalPath();

            OS = System.getProperty("os.name");
            if(OS.equals("Mac OS X")) {
                prefix = canonicalPath + "/FlatLCDataGenerator/data/";
            } else {
                prefix = canonicalPath + "/.." + "/FlatLCDataGenerator/data/";
            }


        } catch (Exception e) {
            e.printStackTrace();
        }



        return (prefix + filename);

    }


    /**
     * Open file and initiate scanner /**
     *
     * @return sc
     */
    private Scanner getScanner() {
        Scanner sc;
        try {
            sc = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unable to read file: " + filename);
        }
        sc.useLocale(Locale.US);
        return sc;
    }


    /**
     * create FlatLevelCombination from the raw data
     */
    private void convert() {
        elements = new ArrayList<>(rawData.size());

        for (int i = 0; i < rawData.size(); i++) {

            int[] levels = rawData.get(i);
            Object[] object = new Object[levels.length];
            for (int j = 0; j < levels.length; j++)
                object[j] = levels[j];

            FlatLevelCombination flc = new FlatLevelCombination(levels, object, maxValues, multIDs);

            elements.add(flc);

        }

    }


//    public String printMaxValues() {
//        StringBuffer sb = new StringBuffer();
//        sb.append("[");
//        for (int i : maxValues) {
//            sb.append(i);
//            sb.append(",");
//        }
//        sb.append("]");
//        return sb.toString();
//    }

    private void readRawData() {
        Scanner sc = this.getScanner();

        // read raw data into ArrayList
        for (int i = 0; i < rows; ++i) {
//            System.out.println("row: " + i);
            sc.nextInt();
//            System.out.println(i);
            int[] tuple = new int[col];
            for (int j = 0; j < col; ++j) {
                double val = sc.nextDouble();

                tuple[j] = (int) val;
//                rawData.add(sc.nextDouble());
            }
            rawData.add(tuple);
//            if (rows > 2400000)
//                printObject(tuple);
            sc.nextLine();
        }

//        find min/max for each column
//        setMinMax();

    }

//
//    private void printObject(int[] tuple) {
//        for (int i = 0; i < tuple.length; i++) {
//            System.out.print(tuple[i] + " ");
//        }
//        System.out.println();
//    }


//    /**
//     * set min and max values for the data set
//     * This is equal to set the maxLevels
//     */
//    private void setMinMax() {
//
//        int[] tuple;
//
////        double[] min = new double[col];
//        // note that the first element is the ID
//        int[] max = new int[col];
//
//        for (int i = 0; i < rawData.size(); i++) {
//            tuple = rawData.get(i);
////            setMin(min, tuple);
//            setMax(max, tuple);
//        }
//
//        this.maxValues = max;
//    }
//
//    private void setMin(double[] min, double[] tuple) {
//        if (min.length != tuple.length)
//            throw new RuntimeException("Wrong size of arrays");
//        for (int i = 0; i < min.length; i++) {
//            min[i] = min[i] < tuple[i] ? min[i] : tuple[i];
//        }
//    }
//
//
//    // note that the first component is the ID
//    private void setMax(int[] max, int[] tuple) {
//        if (max.length != tuple.length)
//            throw new RuntimeException("Wrong size of arrays");
//        for (int i = 0; i < max.length; i++) {
//            max[i] = max[i] > tuple[i] ? max[i] : tuple[i];
//        }
//
//        System.out.println("Maximum Values: ");
//        printObject(max);
//
//    }

//    public float[] generate(int d, int n) {
//        Scanner sc = this.getScanner();
//        float[] data = new float[d*n];
//        for (int row = 0; row < n; ++row) {
//            sc.nextInt();
//            for (int col = 0; col < d; ++col) {
//                data[row*d + col] = sc.nextFloat();
//            }
//            sc.nextLine();
//        }
//        return data;
//    }


    @Override
    public double[] nextVal() {
        throw new RuntimeException("getMetaData not supported in " + getClass());
    }

    @Override
    public Object getMetaData() {
        if (meta == null)
            meta = new RandomResultSetMetaData(maxValues);
        return meta;
    }


    @Override
    public ArrayList<Object> getElements() {
        return (ArrayList<Object>) elements.clone();
    }

    @Override
    public Object next() {
        return elements.get(currentRowInMem++);
    }

    @Override
    public Object peek() throws IllegalStateException, NoSuchElementException, UnsupportedOperationException {
        return elements.get(currentRowInMem);
    }

    @Override
    public boolean supportsPeek() {
        return true;
    }

    @Override
    public void remove() throws IllegalStateException, UnsupportedOperationException {
        throw new UnsupportedOperationException("remove is not supported");
    }

    @Override
    public boolean supportsRemove() {
        return false;
    }

    @Override
    public void update(Object object) throws IllegalStateException, UnsupportedOperationException {
        throw new UnsupportedOperationException("update is not supported");
    }

    @Override
    public boolean supportsUpdate() {
        return false;
    }

    @Override
    public void open() {

    }

    @Override
    public void close() {

    }

    @Override
    public boolean hasNext() {
        return elements.size() > currentRowInMem;
    }

    @Override
    public void reset() {
        currentRowInMem = 0;
    }

    @Override
    public boolean supportsReset() {
        return true;
    }


//    public void printResult() {
//        int counter = 0;
//        while (hasNext()) {
//            next();
//            counter++;
//        }
//        System.out.print("File: " + dataset + " --- ");
//        System.out.print("counter: " + counter + " --- ");
//        System.out.print(printMaxValues());
//    }
//
//    public void printHighValues() {
//
//        int counter = 0;
//        for (int i = 0; i < rawData.size(); i++) {
//            int[] val = rawData.get(i);
//            if(val[2] > 50000)             {
//                System.out.println(val[2]);
//                counter++;
//            }
//        }
//
//        System.out.println("Anzahl: " + counter);
//
//    }


//    public static void main(String[] args) {
//
//        // Test
////        FlatLCFileDataGenerator flc = new FlatLCFileDataGenerator("test", 7, 5);
////        flc.printResult();
////
////        System.out.println("+++++++++++++++++++++++");
////
////        // NBA
////        flc = new FlatLCFileDataGenerator("nba", 6, 17265);
////        flc.printResult();
////
////        System.out.println("+++++++++++++++++++++++");
////
////        // HOU
////        flc = new FlatLCFileDataGenerator("hou", 7, 127931);
////        flc.printResult();
////
////        System.out.println("+++++++++++++++++++++++");
//
//
//        // ZILLOW
//        FlatLCFileDataGenerator flc = new FlatLCFileDataGenerator("zillow", 6, 2245086);
////        flc.printHighValues();
//        flc.printResult();
//
//        System.out.println("+++++++++++++++++++++++");
//
//
//    }


}
