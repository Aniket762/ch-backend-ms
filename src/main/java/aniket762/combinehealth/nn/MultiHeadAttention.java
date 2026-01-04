package aniket762.combinehealth.nn;

import aniket762.combinehealth.core.Matrix;
import aniket762.combinehealth.core.MatrixOps;

public class MultiHeadAttention {
    private int heads;
    private int dModel;
    private Attention[] headsArr;
    private Matrix Wo;
    private Matrix Wproj; // project input into dModel then slice per-head

    public MultiHeadAttention(int dModel, int heads){
        if (dModel % heads != 0) {
            throw new IllegalArgumentException("dModel must be divisible by heads");
        }

        this.dModel = dModel;
        this.heads = heads;
        headsArr = new Attention[heads];

        int headDim = dModel / heads;
        for(int i=0;i<heads;i++){
            headsArr[i] = new Attention(headDim, headDim);
        }

        Wo = Matrix.random(dModel, dModel);
        Wproj = Matrix.random(dModel, dModel);
    }

    // sequential concatenation
    public Matrix forward(Matrix X){
        int headWidth = dModel / heads;

        // project input once into dModel and then slice per-head
        Matrix projected = MatrixOps.matmul(X, Wproj); // X.rows x dModel

        Matrix out = new Matrix(X.rows, dModel);

        for (int i=0;i<heads;i++){
            int start = i * headWidth;
            Matrix headIn = new Matrix(X.rows, headWidth);
            for(int r = 0; r < X.rows; r++){
                System.arraycopy(projected.data[r], start, headIn.data[r], 0, headWidth);
            }

            Matrix h = headsArr[i].forward(headIn);
            if (h.cols != headWidth) {
                throw new IllegalStateException("Unexpected head output width: expected " + headWidth + " got " + h.cols);
            }
            for(int r = 0; r < X.rows; r++) {
                System.arraycopy(h.data[r], 0, out.data[r], start, headWidth);
            }
        }
        return MatrixOps.matmul(out, Wo);
    }
}
