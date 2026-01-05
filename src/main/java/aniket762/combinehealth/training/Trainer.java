package aniket762.combinehealth.training;

import aniket762.combinehealth.nn.TransformerDecoder;
import aniket762.combinehealth.tokenizer.Tokenizer;

public class Trainer {

    private final TransformerDecoder model;
    private final Tokenizer tokenizer;

    public Trainer(TransformerDecoder model, Tokenizer tokenizer) {
        this.model = model;
        this.tokenizer = tokenizer;
    }

    public void train(String[] sentences, int epochs) {
        for (int e = 0; e < epochs; e++) {
            System.out.println("Epoch " + (e + 1));
            for (String s : sentences) {
                int[] tokens = tokenizer.encode(s);
                if (tokens.length < 2) continue;
                int[] input = new int[tokens.length - 1];
                System.arraycopy(tokens, 0, input, 0, input.length);
                model.forward(input);
            }
        }
    }
}
