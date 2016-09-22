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

package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GnuplotExporter {

    private static String MAC_VIEWER = "/Applications/Preview.app/Contents/MacOS/Preview";
    private static String MAC_GNUPLOT = "/opt/local/bin/gnuplot";

    private static String LINUX_VIEWER = "/usr/bin/pqiv";
    private static String LINUX_GNUPLOT = "/usr/bin/gnuplot";

    private static String VIEWER;
    private static String GNUPLOT;

    static {

            String OS = System.getProperty("os.name");
            if (OS.equals("Mac OS X")) {
                VIEWER = MAC_VIEWER;
                GNUPLOT = MAC_GNUPLOT;
            } else {
            	VIEWER = LINUX_VIEWER;
                GNUPLOT = LINUX_GNUPLOT;
            	//VIEWER = "";
                //GNUPLOT = "C:/Tests/gnuplot/bin/wgnuplot.exe";
            }
    }

    /**
     * file that contains data for the diagram
     */
    private File datFile;
    /**
     * file that contains config
     */
    private File pltFile;

    /**
     * labels for the y-coordinates, e.g. algorithm name, etc.
     */
    private String[] labels;
    /**
     * title for the diagram,
     */
    private String[] title;
    /**
     * path to save all output files
     */
    private String path;
    /**
     * x-lavel
     */
    private String xlabel = "x";
    /**
     * y-label
     */
    private String ylabel = "y";
    /**
     * title of the diagram
     */
    private String diaTitle = "";
    // /** file name of result diagram */
    // private String resultFileNmae = "";

    // BufferedWriter
    private BufferedWriter datOut, pltOut;

    /**
     * @param path
     * @param datFileName
     * @param pltFileName
     * @param labels
     * @param xylabels
     * @param title
     */
    public GnuplotExporter(String path, String datFileName, String pltFileName, String[] labels, String[] xylabels, String[] title, String type) {

        for (int i = 0; i < title.length; i++) {
            this.diaTitle += title[i];
//            if (i < title.length - 1)
//                this.diaTitle += "-";
        }

        this.path = path;
        this.datFile = new File(datFileName + diaTitle);
        this.pltFile = new File(pltFileName + diaTitle);

        
        this.labels = labels;
        this.title = title;
        if (xylabels != null) {
            this.xlabel = xylabels[0];
            this.ylabel = xylabels[1];
        }
        this.createFile();
        switch(type){
	        case "rowstacked":	
	        	writeRowstacked();
	        	break;
	        case "plt":
	        	writePltFile();
	        	break;
	        case "box":
	        	System.out.println("Box!");
	        	writeBoxFile();
	        	break;
	        case "partly rowstacked":
	        	writePartlyRowstacked();
	        	break;
	        case "errorbar":
	        	writeErrorbar();
	        	break;
	        default:
	        	break;	
        }
    }

    /**
     * create files
     */
    private void createFile() {
        try {
        	//System.out.println("New datFile: " + path + datFile);
            datOut = new BufferedWriter(new FileWriter(path + datFile));
            //System.out.println("New pltFile: " + path + pltFile);
            pltOut = new BufferedWriter(new FileWriter(path + pltFile));
        } catch (IOException e) {
            printException(e);
        }
    }

    /**
     * delete the file
     */
    public void delete() {
        this.close();
        datFile.delete();
        pltFile.delete();
    }

    /**
     * clear the content of the file
     */
    public void clearContent() {
        this.delete();
        this.createFile();
    }

    /**
     * write a String s to the file
     *
     * @param s
     */
    public void write(String s) {
        try {
            datOut.write(s);
            datOut.newLine();
        } catch (IOException e) {
            printException(e);
        }
    }

    private void writePltFile() {
        try {
            setStuff();
            
            // plot command
            pltOut.write("plot");

            // Aussehen der Line, gestrichelt, durchgehende, gepunktet, etc.
            int k = 1;
            for (int i = 0; i < labels.length; i++) {
                pltOut.write(" \"" + path + datFile + "\"");
                pltOut.write(" using 1:" + (i + 2));
                pltOut.write(" with linespoints title");
                pltOut.write(" \"" + labels[i] + "\"");
                pltOut.write(" ls " + k++);

                if (i < labels.length - 1)
                    pltOut.write(", ");
            }

        } catch (IOException e) {
            printException(e);
        }
    }
     
    
    private void setStuff(){
    	try{
    		// pltOut.write("set output \"" + epsFile + "\"");
            pltOut.write("set output \"" + path + datFile + ".jpg" + "\"");
            pltOut.newLine();
            //
            pltOut.write("set terminal jpeg enhanced font 'Helvetica, " +
                    "15' linewidth 2");
            pltOut.newLine();

            pltOut.write("set title \"" + datFile + "\"");
            pltOut.newLine();

            pltOut.write("set xlabel \"" + xlabel + "\"");
            pltOut.newLine();

            pltOut.write("set ylabel \"" + ylabel + "\"");
            pltOut.newLine();

            //pltOut.write("set key left");
            //pltOut.newLine();
            
            pltOut.write("set xtics rotate by -45 ;");
    		pltOut.newLine();

            pltOut.write("set pointsize 2");
            pltOut.newLine();

            pltOut.write("set datafile missing '?'");
            pltOut.newLine();
    	}
    	catch(IOException e){
    		printException(e);
    	}
    	
    }
    
    
    private void writePartlyRowstacked(){
    	try{   		  		
    		setStuff();
    		pltOut.newLine();
    		
    		pltOut.write("set boxwidth 0.75 absolute");
    		pltOut.newLine();
    		pltOut.write("set style fill solid 0.5");
    		pltOut.newLine();
    		pltOut.write("set style histogram rowstacked");
    		pltOut.newLine();
    		pltOut.write("set style data histograms");
    		pltOut.newLine();
    		pltOut.write("set offset 0,2,0,0");
    		pltOut.newLine(); 		
    		pltOut.newLine(); 	
    		
    		pltOut.write("plot ");
    		
    		File tempFile = new File(datFile.getName().replace("Phases", "Alg"));
    		
    		for (int i = 0; i < labels.length; i++) {
    			
    			if(i == 0){
    				pltOut.write("newhistogram \"" + labels[i] + "\" lt 1, \"" + path + tempFile + "\" index " + i + " u 2:xtic(1) title \"Complete\" lc rgb \"gray\",");
    			}
    			else{
    				pltOut.write("newhistogram \"" + labels[i] + "\" lt 1, \"" + path + tempFile  + "\" index " + i + " u 2:xtic(1) notitle lc rgb \"gray\",");
    			}
    			
    			if(i == 0){
    				pltOut.write("newhistogram \"\" lt 1, \"" + path + datFile + "\""
        					+ " index " + i + " u 2:xtic(1) title \"Init\", '' index " + i + " u 3 title \"phase1\", '' index " + i + " u 4 title \"phase2\", '' index " + i + " u 5 title \"phase3\", '' index " + i + " u 6 title \"Rest\"");
    			}
    			else{
    				pltOut.write("newhistogram \"\" lt 1, \"" + path + datFile + "\""
    					+ " index " + i + " u 2:xtic(1) notitle, '' index " + i + " u 3 notitle, '' index "+ i + " u 4 notitle, '' index " + i + " u 5 notitle, '' index " + i + " u 6 notitle");
    			}  			
    			
    			if(i < labels.length){
    				pltOut.write(", ");
    			}
    		}
    	}
    	catch(IOException e){
    		printException(e);
    	}
    }
    
    
    private void writeErrorbar(){
    	try{
    		setStuff();

    		pltOut.write("set multiplot");
    		pltOut.newLine();
    		
    		pltOut.write("set key out top horiz");
    		pltOut.newLine();
    		
    		pltOut.write("set logscale y 10");
    		pltOut.newLine();
    		
    		pltOut.write("set logscale x 10");
    		pltOut.newLine();
    		
    		pltOut.write("set pointsize 1");
    		pltOut.newLine();
    		
    		pltOut.write("plot");
    		
    		//labels: algorithms (ebnl, esfs, tkls, sequential, parallel)
    		
    		String color[] = new String[]{"brown", "red", "purple", "blue", "green", "grey"};
    		
    		for(int i = 0; i < labels.length; i++){
    			   			
    			//only calling this function when we reached the last label -> datFileName = datFile + labels[labels.length-1]
    			File tempFile = new File(datFile.getName().replace(labels[labels.length - 1], labels[i]));
    			
    			pltOut.write("\"" + path + tempFile + "\" with errorbars title \"" + labels[i] + "\" ls 16 pt 4 lc rgb \"" + color[i] + "\", "
    					+ "'' with lines title \"\" lc rgb \"" + color[i] + "\"");
    			
    			if(i < labels.length - 1){
    				pltOut.write(", ");
    			}
    		}
    		
    	}
    	catch(IOException e){
    		printException(e);
    	}
    }
    
    
    private void writeRowstacked(){
    	try{

    		setStuff(); 		
    		
    		pltOut.write("set boxwidth 0.75 absolute");
    		pltOut.newLine();
    		pltOut.write("set style fill solid 0.5");
    		pltOut.newLine();
    		pltOut.write("set style histogram rowstacked");
    		pltOut.newLine();
    		pltOut.write("set style data histograms");
    		pltOut.newLine();
    		//pltOut.write("set offset 0,2,0,0");
    		//pltOut.newLine(); 		
    		
    		pltOut.write("plot ");

    		for (int i = 0; i < labels.length; i++) {
    			
    			if(i == 0){
    				pltOut.write("\"" + path + datFile + "\" using 2:xtic(1) title \"Init\", '' using 3 title \"Phase1\", '' using 4 title \"Phase2\", '' using 5 title \"Phase3\", '' using 6 title \"Rest\"");
    			}
    			else{
    				pltOut.write("\"" + path + datFile + "\" using 2:xtic(1) notitle, '' using 3 notitle, '' using 4 notitle, '' using 5 notitle, '' using 6 notitle");
    			}
    			if(i < labels.length){
    				pltOut.write(", ");
    			}
    		}

    	}
    	catch(IOException e){
    		printException(e);
    	}
    }
    
    
    private void writeBoxFile() {
        try {
            setStuff();
            
            pltOut.write("set style data histogram");
            pltOut.newLine();
            
            //pltOut.write("set style histogram cluster gap 1");
            //pltOut.newLine();
            
            pltOut.write("set boxwidth 0.85 relative");
            pltOut.newLine();
            
            pltOut.write("set style fill solid 0.75");
            pltOut.newLine();
          
            pltOut.write("plot");

            pltOut.write(" \"" + path + datFile + "\"");
            
            //pltOut.write("using 2:xtic(1) title col, '' using 3:xtic(1) title col, '' using 4:xtic(1) title col, '' using 5:xtic(1) title col, '' using 6:xtic(1) title col,");
            pltOut.write("u 2:xtic(1) notitle");

        } catch (IOException e) {
            printException(e);
        }
    }
    
    
    /**
     * write some meta data, additional information, to a separate file
     *
     * @param filename
     * @param s
     */
    public static void writeMetaData(String filename, String s) {
        File file = new File(filename);
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.append(s);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * close the file
     */
    public void close() {
        try {
            datOut.close();
            pltOut.close();
        } catch (IOException e) {
            printException(e);
        }

    }

    /**
     * print exception
     *
     * @param e
     */
    private void printException(IOException e) {
        System.out.println("GnuplotExporter IOException");
        System.out.println(e.getMessage());
        e.printStackTrace();
    }

    public void plot() {
        try {

            String gnu = GNUPLOT + " " + path + pltFile;
            
            Runtime.getRuntime().exec(gnu);

        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * calls gnuplot and viewer to view the results
     */
    public void showRuntimeDiagram() {

        System.out.println("Show runtimeDiagramm");

        /*try {

             String gnu = GNUPLOT + " " + path + pltFile;

             //Runtime.getRuntime().exec(gnu);

            String view = VIEWER + " " + path + datFile + ".jpg";
            // System.out.println(view);
            //Runtime.getRuntime().exec("open -a " + view);
            Runtime.getRuntime().exec("cmd /c "+ path + datFile + ".jpg");
           
        } catch (IOException e) {
            String msg = e.getMessage();
            System.err.println(e.getMessage());
            e.printStackTrace();
        }*/

    }

}
