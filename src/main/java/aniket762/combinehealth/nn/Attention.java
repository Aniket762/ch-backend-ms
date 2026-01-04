package aniket762.combinehealth.nn;

import aniket762.combinehealth.core.Matrix;
import aniket762.combinehealth.core.MatrixOps;

public class Attention {
    public Matrix Wq, Wk, Wv;
    private final int inDim;
    private final int outDim;

    public Attention(int inDim, int outDim){
        this.inDim = inDim;
        this.outDim = outDim;
        Wq = Matrix.random(inDim, outDim);
        Wk = Matrix.random(inDim, outDim);
        Wv = Matrix.random(inDim, outDim);
    }

    public Matrix forward(Matrix X){
        if (X.cols != inDim) {
            throw new IllegalArgumentException("Attention: expected input cols " + inDim + " but got " + X.cols);
        }

        int t = X.rows;

        Matrix Q = MatrixOps.matmul(X, Wq); // t x outDim
        Matrix K = MatrixOps.matmul(X, Wk); // t x outDim
        Matrix V = MatrixOps.matmul(X, Wv); // t x outDim

        Matrix scores = MatrixOps.matmul(Q, MatrixOps.transpose(K)); // t x t
        float scale = (float) Math.sqrt(outDim);

        for(int i=0;i<scores.rows;i++){
            for (int j=0;j<scores.cols;j++){
                scores.data[i][j] /= scale;
            }
        }

        // causal mask
        for(int i=0;i<t;i++){
            for(int j=i+1; j<t;j++){
                scores.data[i][j] = -1e9f;
            }
        }

        MatrixOps.softMax(scores);

        return MatrixOps.matmul(scores, V); // (t x t) * (t x outDim) -> t x outDim
    }
}
