package aniket762.combinehealth.nn;

import aniket762.combinehealth.core.Matrix;
import aniket762.combinehealth.core.MatrixOps;

public class DecoderBlock {
    private MultiHeadAttention mha;
    private FeedForward ffn;

    public DecoderBlock(int dModel, int heads,int dff){
        mha = new MultiHeadAttention(dModel,heads);
        ffn = new FeedForward(dModel,dff);
    }

    public Matrix forward(Matrix x){
        Matrix attn = mha.forward(x);
        x = MatrixOps.add(x,attn);
        MatrixOps.layerNorm(x);

        Matrix ff = ffn.forward(x);
        x = MatrixOps.add(x,ff);
        MatrixOps.layerNorm(x);

        return x;
    }
}