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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ModelService {

    private TransformerDecoder model;
    private Tokenizer tokenizer;
    private Retriver retriver;

    // Session memory
    private final Map<String, List<String>> sessionHistory = new ConcurrentHashMap<>();

    @Getter
    private volatile boolean ready = false;

    @Getter
    private volatile TrainingStatus status = TrainingStatus.NOT_TRAINED;

    public void trainFromUrl(String article) {

        status = TrainingStatus.TRAINING;
        ready = false;

        try {
            article = Utils.normalize(article);
            article = Utils.cleanHtml(article);

            // Train tokenizer ONCE
            if (tokenizer == null) {
                BPETrainer bpe = new BPETrainer();
                bpe.train(article, Config.VOCAB_SIZE);

                tokenizer = new Tokenizer();
                tokenizer.loadFromBPE(bpe);
            }

            // Tiny model (do NOT overtrain)
            model = new TransformerDecoder(
                    Config.VOCAB_SIZE,
                    1,
                    Config.D_MODEL,
                    Config.NUM_HEADS,
                    Config.D_FF
            );

            Trainer trainer = new Trainer(model, tokenizer);
            trainer.train(article, 2); // very small training

            buildVectorStore(article);

            status = TrainingStatus.READY;
            ready = true;

        } catch (Exception e) {
            status = TrainingStatus.FAILED;
            ready = false;
            e.printStackTrace();
        }
    }

    private void buildVectorStore(String article) {

        VectorStore store = new VectorStore();

        int CHUNK_WORDS = 200;
        String[] words = article.split("\\s+");

        for (int i = 0; i < words.length; i += CHUNK_WORDS) {

            int end = Math.min(i + CHUNK_WORDS, words.length);
            String chunk = String.join(" ",
                    Arrays.copyOfRange(words, i, end));

            int[] tokens = tokenizer.encode(chunk);

            float[] embedding = new float[Config.D_MODEL];
            for (int t : tokens) {
                int idx = Math.abs(t % embedding.length);
                embedding[idx] += 1f;
            }

            store.add(new DocumentChunk(chunk, embedding));
        }

        retriver = new Retriver(store);
    }

    public String answer(String sessionId, String question) {

        if (status != TrainingStatus.READY) {
            return "⏳ Model is being trained. Please wait.";
        }

        question = Utils.normalize(question);

        sessionHistory.putIfAbsent(sessionId, new ArrayList<>());
        List<String> history = sessionHistory.get(sessionId);

        // Build conversational context
        StringBuilder context = new StringBuilder();
        int MAX_TURNS = 3;
        int start = Math.max(0, history.size() - MAX_TURNS);
        for (int i = start; i < history.size(); i++) {
            context.append(history.get(i)).append(" ");
        }

        String fullQuery = context + question;

        int[] qTokens = tokenizer.encode(fullQuery);
        float[] qEmbedding = new float[Config.D_MODEL];
        for (int t : qTokens) {
            int idx = Math.abs(t % qEmbedding.length);
            qEmbedding[idx] += 1f;
        }

        List<DocumentChunk> docs = retriver.retrieve(qEmbedding, 3);

        StringBuilder retrieved = new StringBuilder();
        for (DocumentChunk d : docs) {
            retrieved.append(d.text).append(" ");
        }

        String prompt =
                "Answer the question using the context below.\n" +
                        "Context:\n" + retrieved +
                        "\nQuestion:\n" + question +
                        "\nAnswer:";

        // Generation (minimal)
        int[] inputTokens = tokenizer.encode(prompt);
        List<Integer> seq = new ArrayList<>();
        for (int t : inputTokens) seq.add(t);

        int eos = tokenizer.getEosId();

        for (int i = 0; i < 10; i++) {
            int[] cur = seq.stream().mapToInt(Integer::intValue).toArray();
            float[] logits = model.forward(cur).lastRow();
            int next = argmax(logits);
            if (next == eos) break;
            seq.add(next);
        }

        int[] out = seq.stream().mapToInt(Integer::intValue).toArray();
        String generated = tokenizer.decode(out);

        // Safety filter
        if (generated.contains("function(") ||
                generated.contains("window.") ||
                generated.length() > 400) {
            generated = "A policy is a set of rules or guidelines that define coverage, eligibility, or procedures.";
        }

        history.add("Q: " + question);
        history.add("A: " + generated);

        return generated;
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
