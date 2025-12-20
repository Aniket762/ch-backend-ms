package aniket762.combinehealth.training;

import aniket762.combinehealth.core.Matrix;
import aniket762.combinehealth.nn.TransformerDecoder;
import aniket762.combinehealth.tokenizer.Tokenizer;


public class Trainer {
    private final TransformerDecoder model;
    private final Tokenizer tokenizer;

    public Trainer(TransformerDecoder model, Tokenizer tokenizer){
        this.model = model;
        this.tokenizer = tokenizer;
    }

    public void train(String[] sentences, int epochs){
        for(int e=0;e<epochs;e++){
            float epochLoss = 0f;
            for(String s:sentences){
                int[] tokens = tokenizer.encode(s);
                Matrix logits = model.forward(tokens);
                epochLoss += Loss.crossEntropy(logits,tokens);
            }
            System.out.println("Epoch "+ e + "Loss: " + epochLoss/ sentences.length);
        }
    }
}