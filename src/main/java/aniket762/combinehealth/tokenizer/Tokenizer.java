package aniket762.combinehealth.tokenizer;

import lombok.Getter;
import java.util.*;

@Getter
public class Tokenizer {

    private final Map<String, Integer> token2id = new HashMap<>();
    private final Map<Integer, String> id2token = new HashMap<>();
    private List<String[]> merges = new ArrayList<>();
    private int vocabSize = 0;

    public Tokenizer() {
        addToken("<PAD>");
        addToken("<UNK>");
        addToken("<EOS>");
    }

    public void loadFromBPE(BPETrainer bpe) {
        token2id.clear();
        id2token.clear();
        vocabSize = 0;
        addToken("<PAD>");
        addToken("<UNK>");
        addToken("<EOS>");
        for (String tok : bpe.getVocab().keySet())
            addToken(tok);
        merges = bpe.getMerges();
    }

    public int addToken(String token) {
        int id = vocabSize;
        token2id.put(token, id);
        id2token.put(id, token);
        vocabSize++;
        return id;
    }

    public int[] encode(String text) {
        text = text.toLowerCase();
        List<Integer> out = new ArrayList<>();
        int unk = token2id.get("<UNK>");

        for (String word : text.split("\\s+")) {
            out.add(token2id.getOrDefault(word, unk));
        }

        out.add(getEosId());
        return out.stream().mapToInt(i -> i).toArray();
    }

    public String decode(int[] tokens) {
        StringBuilder sb = new StringBuilder();
        for (int id : tokens) {
            if (id == getEosId()) break;
            String tok = id2token.getOrDefault(id, "<UNK>");
            if (!tok.equals("<PAD>")) sb.append(tok).append(" ");
        }
        return sb.toString().trim();
    }


    public int getEosId() {
        return token2id.get("<EOS>");
    }
}
