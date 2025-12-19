package aniket762.combinehealth.nn;

import aniket762.combinehealth.core.Matrix;

public class Embedding {
    public Matrix weights;

    public Embedding(int vocabSize, int dModel){
        this.weights = Matrix.random(vocabSize,dModel);
    }

    public Matrix forward(int[] tokens){
        Matrix out = new Matrix(tokens.length,weights.cols);
        for(int i=0;i<tokens.length;i++){
            out.data[i] = weights.data[tokens[i]].clone();
        }
        return out;
    }
}