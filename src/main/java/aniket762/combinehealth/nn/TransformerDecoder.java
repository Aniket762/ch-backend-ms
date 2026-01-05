package aniket762.combinehealth.nn;

import aniket762.combinehealth.core.Matrix;
import aniket762.combinehealth.core.MatrixOps;

import java.util.ArrayList;
import java.util.List;

public class TransformerDecoder {

    private final Embedding embedding;
    private final List<DecoderBlock> blocks;
    private final Matrix lmHead;

    public TransformerDecoder(int vocab, int layers, int dModel, int heads, int dff){
        embedding = new Embedding(vocab, dModel);
        blocks = new ArrayList<>();
        for(int i=0; i<layers; i++){
            blocks.add(new DecoderBlock(dModel, heads, dff));
        }
        lmHead = Matrix.random(dModel, vocab);
    }

    public Matrix forward(int[] tokens){
        Matrix x = embedding.forward(tokens);
        PositionalEncoding.add(x);

        for(DecoderBlock b : blocks) x = b.forward(x);

        Matrix logits = MatrixOps.matmul(x, lmHead);

        return logits;
    }
}
