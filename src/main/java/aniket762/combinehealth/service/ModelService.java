package aniket762.combinehealth.service;

import aniket762.combinehealth.nn.Config;
import aniket762.combinehealth.nn.TransformerDecoder;
import aniket762.combinehealth.rag.DocumentChunk;
import aniket762.combinehealth.rag.Retriver;
import aniket762.combinehealth.rag.VectorStore;
import aniket762.combinehealth.tokenizer.BPETrainer;
import aniket762.combinehealth.tokenizer.Tokenizer;
import aniket762.combinehealth.util.Utils;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ModelService {

    private TransformerDecoder model;
    private Tokenizer tokenizer;
    private Retriver retriver;

    @Getter
    private volatile boolean ready = false;
    @Getter
    private volatile TrainingStatus status = TrainingStatus.NOT_TRAINED;

    public synchronized void trainFromUrl(String url) throws Exception {
        System.out.println("Reading article from URL...");
        String article = Utils.readFromUrl(url);
        article = Utils.normalize(article);

        // Train BPE tokenizer
        BPETrainer bpe = new BPETrainer();
        bpe.train(article, 100);

        tokenizer = new Tokenizer();
        tokenizer.loadFromBPE(bpe);

        model = new TransformerDecoder(
                100,
                1,
                32,
                2,
                64
        );

        buildVectorStore(article);

        ready = true;
        System.out.println("Model is ready!");
    }

    private void buildVectorStore(String article){
        VectorStore store = new VectorStore();

        String[] chunks = article.split("\\. ");
        for(String chunk : chunks){
            float[] embedding = new float[Config.D_MODEL];
            int[] tokens = tokenizer.encode(chunk);
            for(int i=0; i<tokens.length && i<embedding.length; i++){
                embedding[i] = tokens[i] / 100f;
            }

            store.add(new DocumentChunk(chunk, embedding));
        }
        retriver = new Retriver(store);
    }


    public String answer(String question) {
        if (status != TrainingStatus.READY) {
            throw new IllegalStateException("Model not ready");
        }

        question = Utils.normalize(question);

        if (retriver == null) {
            throw new IllegalStateException("Retriever not initialized");
        }

        int[] qTokens = tokenizer.encode(question);
        float[] qEmbedding = new float[Config.D_MODEL];
        for(int i=0; i<qTokens.length && i<qEmbedding.length; i++){
            qEmbedding[i] = qTokens[i] / 100f;
        }
        List<DocumentChunk> docs = retriver.retrieve(qEmbedding, 3);

        StringBuilder response = new StringBuilder();
        for (DocumentChunk chunk : docs) {
            response.append(chunk.text).append(" ");
        }

        String prompt = response.toString().trim();

        int[] inputTokens = tokenizer.encode(prompt);

        List<Integer> seq = new ArrayList<>();
        for (int t : inputTokens) seq.add(t);

        int eos = tokenizer.getEosId();
        int maxNewTokens = 5;

        try {
            for (int i = 0; i < maxNewTokens; i++) {
                int[] cur = seq.stream().mapToInt(Integer::intValue).toArray();
                float[] logits = model.forward(cur).lastRow();

                int next = argmax(logits);
                if (next == eos) break;

                seq.add(next);
            }
        } catch (Exception e) {
            return prompt;
        }

        int start = inputTokens.length;
        if (seq.size() <= start) return prompt;

        int[] outTokens = new int[seq.size() - start];
        for (int i = start; i < seq.size(); i++) {
            outTokens[i - start] = seq.get(i);
        }

        String generated = tokenizer.decode(outTokens);

        return prompt + " " + generated;
    }


    private int argmax(float[] arr) {
        int idx = 0;
        float best = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > best) {
                best = arr[i];
                idx = i;
            }
        }
        return idx;
    }
}
