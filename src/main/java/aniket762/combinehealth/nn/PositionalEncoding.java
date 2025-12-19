package aniket762.combinehealth.nn;

import aniket762.combinehealth.core.Matrix;

public class PositionalEncoding {
    public static void add(Matrix x){
        for(int pos = 0; pos<x.rows;pos++){
            for(int i=0;i<x.cols;i++){
                double angle = pos/Math.pow(10000,(double) i/x.cols);
                x.data[pos][i] += (float) Math.sin(angle);
                if(i+1<x.cols){
                    x.data[pos][i+1] += (float) Math.cos(angle);
                }
            }
        }
    }
}