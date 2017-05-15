package test;
import java.util.Random;

/**
 * Created by Viechle on 09.12.2016.
 */
public class RandDataTest {

    int dim;
    int count;



    static double random_equal(double min, double max){

        Random randomNr = new Random();
        return randomNr.nextDouble()*(max-min) + min;
    }

    static double random_peak(double min, double max, int dim){

        double sum = 0.0;

        for(int d =0 ; d < dim ; d++){

            sum += random_equal(0, 1);
        }
        sum /= dim;
        return sum *(max-min) + min;
    }

    static double random_normal(double med, double var){

        return random_peak(med-var, med+var, 12);
    }

   /* static void generate_indep(int count, int dim){

        double[] values = new double[dim];

        while(count-- > 0){

            for(int d = 0 ; d < dim ; d++){

                values[d] = random_equal(0,1);
            }
            output_vector(values);
            stats_enter(dim, values);
        }
    }*/

   /* static void generate_corr(int count, int dim){

        double[] values = new double[dim];

        while(count-- > 0){

            do{

                double v = random_peak(0,1,dim);
                double l = v <= 0.5 ? v : 1.0 - v;

                for(int d = 0 ; d < dim ; d++){

                    values[d] = v;
                }

                for(int d = 0 ; d < dim ; d++){

                    double h = random_normal(0,1);
                    values[d] += h;
                    values[(d+1) % dim] -= h;
                }
            }while(!is_vector_ok(values));

            output_vector(values);
            stats_enter(dim, values);
        }
    }*/

    /*static void generate_anti(int count, int dim){

        double[] values = new double[dim];

        while(count-- > 0){

            do{

                double v = random_normal(0.5, 0.25);
                double l = v <= 0.5 ? v : 1.0 - v;

                for(int d = 0 ; d < dim ; d++){

                    values[d] = v;
                }

                for(int d = 0 ; d < dim ; d++){

                    double h = random_equal(-1, 1);
                    values[d] += h;
                    values[(d+1) % dim] -= h;
                }
            }while(!is_vector_ok(values));

            output_vector(values);
            stats_enter(dim, values);
        }
    }*/


    static void output_vector(double[] values){

        //opt_id?
        //opt_pad?
        int i;
        for(i = 0 ; i < values.length-1 ; i++){

            System.out.print(String.format("%5.10e", values[i]) + "| ");
        }
        System.out.print(String.format("%5.10e", values[i]) + "\n");

    }

    static void stats_enter(int dim, double[] values){


    }

    static boolean is_vector_ok(double[] values){

        for(int i = 0 ; i < values.length ; i++){

            if(values[i] < 0.0 || values[i] > 1.0){

                return false;
            }
        }
        return true;
    }

    public static void main(String[] args){

        /*for(int i = 0 ; i < 10 ; i++){

            System.out.println(random_peak(10,50,10));
        }
        System.out.println(String.format("%10.10e", 2.012312435655353535));*/
        //generate_corr(3, 3);

    }
}
