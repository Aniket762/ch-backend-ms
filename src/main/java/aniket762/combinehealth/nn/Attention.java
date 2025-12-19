package aniket762.combinehealth.nn;

import aniket762.combinehealth.core.Matrix;
import aniket762.combinehealth.core.MatrixOps;

public class Attention {
    public Matrix Wq,Wk,Wv;

    public Attention(int dModel){
        Wq = Matrix.random(dModel,dModel);
        Wk = Matrix.random(dModel,dModel);
        Wv = Matrix.random(dModel,dModel);
    }

    public Matrix forward(Matrix X){
        int t = X.rows;
        int d = X.cols;

        Matrix Q = MatrixOps.matmul(X,Wq);
        Matrix K = MatrixOps.matmul(X,Wk);
        Matrix V = MatrixOps.matmul(X,Wv);

        Matrix scores = MatrixOps.matmul(Q,MatrixOps.transpose(K));
        float scale = (float) Math.sqrt(d);

        for(int i=0;i<scores.rows;i++){
            for (int j=0;j<scores.cols;j++){
                scores.data[i][j] /= scale;
            }
        }

        //mask
        for(int i=0;i<t;i++){
            for(int j=i+1; j<t;j++){
                scores.data[i][j] = -1e9f;
            }
        }

        MatrixOps.softMax(scores);

        return MatrixOps.matmul(scores,V);
    }
}