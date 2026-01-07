package aniket762.combinehealth.nn;

import aniket762.combinehealth.core.Matrix;
import aniket762.combinehealth.core.MatrixOps;

import java.util.ArrayList;
import java.util.List;

public class TransformerDecoder {
    private final Embedding embedding;
    private final List<DecoderBlock> blocks;
    private final Matrix lmHead;

    public TransformerDecoder(int vocab, int layers, int dModel, int heads, int dff) {
        embedding = new Embedding(vocab, dModel);
        blocks = new ArrayList<>();
        for (int i = 0; i < layers; i++) {
            blocks.add(new DecoderBlock(dModel, heads, dff));
        }
        lmHead = Matrix.random(dModel, vocab);
    }

    public Matrix forward(int[] tokens) {
        Matrix x = embedding.forward(tokens);
        PositionalEncoding.add(x);

        for (DecoderBlock b : blocks) {
            x = b.forward(x);
        }

        return MatrixOps.matmul(x, lmHead);
    }


    public void backward(int[] target) {
        // Make sure target length does not exceed logits
        Matrix logits = lastForwardOutput; // store from last forward
        int rows = Math.min(logits.rows, target.length);

        // Cross-entropy gradient placeholder
        Matrix grad = new Matrix(rows, logits.cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < logits.cols; j++) {
                float p = logits.data[i][j];
                grad.data[i][j] = (j == target[i] ? p - 1 : p);
            }
        }

        // Backprop through LM head
        // For simplicity, we update lmHead directly with small learning rate
        float lr = 0.01f;
        for (int i = 0; i < lmHead.rows; i++) {
            for (int j = 0; j < lmHead.cols; j++) {
                lmHead.data[i][j] -= lr * grad.data[i][j];
            }
        }

        // Optionally propagate to decoder blocks and embedding
        // (left as is for prototype simplicity)
    }

    private Matrix lastForwardOutput;

    public void cacheForward(Matrix out) {
        this.lastForwardOutput = out;
    }
}
