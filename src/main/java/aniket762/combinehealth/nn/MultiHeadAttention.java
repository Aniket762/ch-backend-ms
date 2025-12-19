package aniket762.combinehealth.nn;

import aniket762.combinehealth.core.Matrix;
import aniket762.combinehealth.core.MatrixOps;

public class MultiHeadAttention {
    private int heads;
    private int dModel;
    private Attention[] headsArr;
    private Matrix Wo;

    public MultiHeadAttention(int dModel, int heads){
        this.dModel = dModel;
        this.heads = heads;
        headsArr = new Attention[heads];

        for(int i=0;i<heads;i++){
            headsArr[i] = new Attention(dModel);
        }

        Wo = Matrix.random(dModel,dModel);
    }

    // sequential concatenation
    public Matrix forward(Matrix X){
        Matrix out = new Matrix(X.rows,X.cols*heads);
        for (int i=0;i<heads;i++){
            Matrix h = headsArr[i].forward(X);
            for(int j = 0; j<X.rows;j++) {
                System.arraycopy(h.data[j], 0, out.data[j], i * X.cols, X.cols);
            }
        }
        return MatrixOps.matmul(out,Wo);
    }
}