package aniket762.combinehealth.service;

import aniket762.combinehealth.nn.Config;
import aniket762.combinehealth.nn.TransformerDecoder;
import aniket762.combinehealth.rag.DocumentChunk;
import aniket762.combinehealth.rag.Retriver;
import aniket762.combinehealth.rag.VectorStore;
import aniket762.combinehealth.tokenizer.BPETrainer;
import aniket762.combinehealth.tokenizer.Tokenizer;
import aniket762.combinehealth.training.Trainer;
import aniket762.combinehealth.util.Utils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ModelService {
    private TransformerDecoder model;
    private Tokenizer tokenizer;
    private Retriver retriver;

    public void trainFromUrl(String articleUrl) throws Exception{
        System.out.println("Reading article..........");
        String article = Utils.readFromUrl(articleUrl);

        // Train BPE
        BPETrainer bpe = new BPETrainer();
        bpe.train(article, Config.VOCAB_SIZE);

        tokenizer = new Tokenizer();
        for(String token: bpe.getVocab().keySet()){
            tokenizer.addToken(token);
        }

        model = new TransformerDecoder(
                Config.VOCAB_SIZE,
                Config.NUM_LAYERS,
                Config.D_MODEL,
                Config.NUM_HEADS,
                Config.D_FF
        );

        // train
        Trainer trainer = new Trainer(model, tokenizer);
        trainer.train(Collections.singleton(article).toArray(new String[0]),5);

        // build with RAG Store
        buildVectorStore(article);

        System.out.println("Model Training Completed!!!!");
    }

    private void buildVectorStore(String article){
        VectorStore store = new VectorStore();

        String[] chunks = article.split("\\. ");
        for(String chunk:chunks){
            float[] embedding = new float[Config.D_MODEL];
            store.add(new DocumentChunk(chunk,embedding));
        }
        retriver = new Retriver(store);
    }

    public String answer(String question){
        int[] qTokens = tokenizer.encode(question);
        float[] qEmbedding = new float[Config.D_MODEL];

        // top 3 closest
        List<DocumentChunk> docs = retriver.retrieve(qEmbedding,3);

        StringBuilder context = new StringBuilder(question).append(" ");
        for(DocumentChunk chunk:docs){
            context.append(chunk.text).append(" ");
        }

        int[] input = tokenizer.encode(context.toString());
        return model.forward(input).toString();
    }
}
