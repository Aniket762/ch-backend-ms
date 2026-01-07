package aniket762.combinehealth.nn;

import aniket762.combinehealth.core.Matrix;

public class Embedding {

    public final int vocabSize;
    private final int dModel;
    private final Matrix weights;

    public Embedding(int vocabSize, int dModel) {
        this.vocabSize = vocabSize;
        this.dModel = dModel;
        this.weights = Matrix.random(vocabSize, dModel);
    }

    public Matrix forward(int[] tokens) {
        Matrix out = new Matrix(tokens.length, dModel);

        for (int i = 0; i < tokens.length; i++) {
            int id = tokens[i];

            // 🔒 SAFETY CLIP (VERY IMPORTANT)
            if (id < 0) id = 0;
            if (id >= vocabSize) id = vocabSize - 1;

            for (int j = 0; j < dModel; j++) {
                out.data[i][j] = weights.data[id][j];
            }
        }
        return out;
    }
}
