package test;

import dataGenerator.GenerateAnti;

/**
 * Created by Viechle on 18.12.2016.
 */
public class Test {

    public static void main(String[] args){

        /*To use the RandDataGenerator just use a constructor
			
			new [TypeOfRandData](int count, int dimension, String TableName, Integer padding, Integer seed)
			
			!! some of the Parameters use wrapper classes like INTEGER to avoid to many constructors
		*/
		
		//Example
        GenerateAnti test = new GenerateAnti(10, 3, null, new Integer(30), 1);
    }
}
