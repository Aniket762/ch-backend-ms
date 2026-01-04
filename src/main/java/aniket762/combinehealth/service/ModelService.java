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
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModelService {
    private TransformerDecoder model;
    private Tokenizer tokenizer;
    private Retriver retriver;
    @Getter
    private volatile TrainingStatus status = TrainingStatus.NOT_TRAINED;
    @Getter
    private volatile String lastError = null;


    // Adding threadSafe lock
    public synchronized void trainFromUrl(String articleUrl) throws Exception{
        if(status == TrainingStatus.TRAINING){
            throw new IllegalStateException("Training already in progress");
        }

        status = TrainingStatus.TRAINING;
        lastError = null;

        // Thread to log ongoing ongoing
        Thread monitor = new Thread(() -> {
            try {
                while (ModelService.this.status == TrainingStatus.TRAINING) {
                    System.out.println("Training ongoing...");
                    Thread.sleep(10000);
                }
            } catch (InterruptedException ignored) {
                System.out.println("Monitor interrupted");
            }
        });
        monitor.setDaemon(true);
        monitor.start();

        try{
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

            // running one forward pass on the first sentence to catch shape errors immediately
            String[] sentences = article.split("\\.\\s+");
            if (sentences.length > 0) {
                int[] sampleTokens = tokenizer.encode(sentences[0]);
                try {
                    model.forward(sampleTokens);
                } catch (Throwable t) {
                    status = TrainingStatus.FAILED;
                    lastError = t.getMessage();
                    t.printStackTrace();
                    throw t;
                }
            }

            Trainer trainer = new Trainer(model, tokenizer);
            try {
                trainer.train(sentences, 5);
            } catch (Throwable t) {
                status = TrainingStatus.FAILED;
                lastError = t.getMessage();
                t.printStackTrace();
                throw t;
            }

            buildVectorStore(article);
            status = TrainingStatus.READY;
            System.out.println("Model Training Completed!!!!");
        }catch (Exception e){
            status = TrainingStatus.FAILED;
            if (lastError == null) lastError = e.getMessage();
            e.printStackTrace();
            throw e;
        }
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
        if(status != TrainingStatus.READY){
            throw new IllegalStateException("Model not read. Status: "+ status);
        }

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
