package aniket762.combinehealth.training;

import aniket762.combinehealth.nn.TransformerDecoder;
import aniket762.combinehealth.tokenizer.Tokenizer;
import aniket762.combinehealth.core.Matrix;

public class Trainer {

    private final TransformerDecoder model;
    private final Tokenizer tokenizer;

    public Trainer(TransformerDecoder model, Tokenizer tokenizer) {
        this.model = model;
        this.tokenizer = tokenizer;
    }

    public void train(String text, int epochs) {

        int[] tokens = tokenizer.encode(text);

        if (tokens.length < 2) return;

        for (int e = 0; e < epochs; e++) {

            // Teacher forcing
            int[] input = new int[tokens.length - 1];
            int[] target = new int[tokens.length - 1];

            System.arraycopy(tokens, 0, input, 0, input.length);
            System.arraycopy(tokens, 1, target, 0, target.length);

            // Forward
            Matrix logits = model.forward(input);

            // VERY SIMPLE LOSS (no softmax yet)
            float loss = 0f;
            for (int i = 0; i < target.length; i++) {
                int t = target[i];
                if (t >= logits.cols) t = logits.cols - 1;
                loss += -Math.log(Math.max(logits.data[i][t], 1e-6));
            }

            loss /= target.length;

            if (e % 5 == 0) {
                System.out.println("Epoch " + e + " loss = " + loss);
            }
        }
    }
}
