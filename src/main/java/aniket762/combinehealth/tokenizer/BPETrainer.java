package aniket762.combinehealth.tokenizer;

import lombok.Getter;
import java.util.*;

@Getter
public class BPETrainer {
    private final Map<String, Integer> vocab = new LinkedHashMap<>();
    private final List<String[]> merges = new ArrayList<>();

    public void train(String text, int targetVocabSize) {
        text = text.toLowerCase();
        List<List<String>> corpus = new ArrayList<>();

        for (String word : text.split("\\s+")) {
            List<String> chars = new ArrayList<>();
            for (char c : word.toCharArray()) chars.add(String.valueOf(c));
            corpus.add(chars);
        }

        for (List<String> word : corpus)
            for (String ch : word) vocab.putIfAbsent(ch, vocab.size());

        while (vocab.size() < targetVocabSize) {
            Map<String, Integer> pairFreq = new HashMap<>();
            for (List<String> word : corpus)
                for (int i = 0; i < word.size() - 1; i++)
                    pairFreq.put(word.get(i) + " " + word.get(i + 1),
                            pairFreq.getOrDefault(word.get(i) + " " + word.get(i + 1), 0) + 1);

            if (pairFreq.isEmpty()) break;

            String bestPair = Collections.max(pairFreq.entrySet(), Map.Entry.comparingByValue()).getKey();
            String[] parts = bestPair.split(" ");
            merges.add(parts);
            String merged = parts[0] + parts[1];
            vocab.putIfAbsent(merged, vocab.size());

            for (List<String> word : corpus)
                for (int i = 0; i < word.size() - 1; i++)
                    if (word.get(i).equals(parts[0]) && word.get(i + 1).equals(parts[1])) {
                        word.set(i, merged);
                        word.remove(i + 1);
                    }
        }
    }
}
