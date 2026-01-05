package aniket762.combinehealth.nn;

import aniket762.combinehealth.core.Matrix;

public class Embedding {
    public Matrix weights;

    public Embedding(int vocabSize, int dModel){
        this.weights = Matrix.random(vocabSize,dModel);
    }

    public Matrix forward(int[] tokens){
        Matrix out = new Matrix(tokens.length, weights.numCols());
        int vocabSize = weights.numRows();
        int unkId = 1; // assume <UNK> is 1

        for(int i=0; i<tokens.length; i++){
            int t = tokens[i];
            if (t < 0 || t >= vocabSize) {
                t = unkId;
            }
            System.arraycopy(weights.data[t], 0, out.data[i], 0, weights.numCols());
        }
        return out;
    }

}