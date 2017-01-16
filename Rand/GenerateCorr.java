/**
 * Created by Viechle on 18.12.2016.
 */
public class GenerateCorr extends RandomDataGenerator{

    String dist = "c";

    public GenerateCorr(int count, int dim, String tableName, Integer padding, Integer seed){

        super(count, dim, tableName, padding, seed);
        super.generateSqlTable(dist);
        super.generateSqlCopy();
        generateMatrix();
        super.output_vector();
    }

    public void generateMatrix(){

        for(int i=0 ; i<count ; i++){

            RandVector temp = new RandVector(dim);
            temp.generate_corr(dim);
            if(paddingLen != null){

                temp.generate_padding(paddingInit, Integer.valueOf(paddingLen));
            }
            data[i] = temp;
        }
    }
}