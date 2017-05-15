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

package realdata;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Scanner;

import dataGenerator.RandVector;


/**
 * Uses real data from Zillow, NBA, etc.
 * Based on the implementation of LVDN14, APSkyline
 */
public class RandomFileDataGenerator {

    private int dims;
    private String filename;
    private RandVector[] elements;


    public RandomFileDataGenerator(String filename, int inputSize, int dims) {


        this.filename = getFilePath(filename);

        this.dims = dims;
        this.elements = new RandVector[inputSize];

        readRawData();
    }


    private String getFilePath(String filename) {
        String canonicalPath = null;
        String OS = null;
        String prefix = null;
        try {
            canonicalPath = new File(".").getCanonicalPath();

            OS = System.getProperty("os.name");
            if (OS.equals("Mac OS X")) {
                prefix = canonicalPath + "/DataGenerator/data/";
            } else {
                prefix = canonicalPath + "/data/";
//            	prefix = "";
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
            sc = new Scanner(new File(filename+".txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unable to read file: " + filename);
        }
        sc.useLocale(Locale.US);
        return sc;
    }



    private void readRawData() {
        Scanner sc = this.getScanner();

        // read raw data into ArrayList
        for (int i = 0; i < elements.length && sc.hasNext(); ++i) {
            double[] tuple = new double[dims];
            // remove ID column, i.e. the first column of the data set
        	sc.nextDouble();
            for (int j = 0; j < dims; ++j) {
                double val = sc.nextDouble();
                tuple[j] = val;
            }
            elements[i] = new RandVector(tuple);
            sc.nextLine();
        }
    }
    
    public RandVector[] getElements(){
    	return elements;
    }
}
