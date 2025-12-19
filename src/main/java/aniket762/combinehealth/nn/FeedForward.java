package aniket762.combinehealth.nn;

import aniket762.combinehealth.core.Matrix;
import aniket762.combinehealth.core.MatrixOps;

public class FeedForward {
    private Matrix W1, W2;

    public FeedForward(int dModel, int dff){
        W1 = Matrix.random(dModel,dff);
        W2 = Matrix.random(dff,dModel);
    }

    public Matrix forward(Matrix x){
        Matrix h = MatrixOps.matmul(x,W1);
        MatrixOps.relu(h);
        return MatrixOps.matmul(h,W2);
    }
}