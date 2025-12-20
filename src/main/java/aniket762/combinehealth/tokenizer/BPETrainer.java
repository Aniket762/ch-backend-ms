package aniket762.combinehealth.tokenizer;

import lombok.Getter;

import java.util.*;

@Getter
public class BPETrainer {
    private Map<String,Integer> vocab = new HashMap<>();

    public void train(String text, int vocabSize){
        List<String> tokens = new ArrayList<>();
        for(char ch: text.toCharArray()) tokens.add(String.valueOf(ch));

        Map<String,Integer> freq = new HashMap<>();
        for(String t:tokens) freq.put(t,freq.getOrDefault(t,0)+1);

        while (freq.size()<vocabSize){
            // Most frequent pair
            Map<String, Integer> pairs = new HashMap<>();
            for(int i=0;i<tokens.size()-1;i++){
                String pair = tokens.get(i) + tokens.get(i+1);
                pairs.put(pair, pairs.getOrDefault(pair,0)+1);
            }
            if(pairs.isEmpty()) break;

            String best = Collections.max(
                    pairs.entrySet(),
                    Map.Entry.comparingByValue()
            ).getKey();

            // merge
            for(int i=0;i<tokens.size()-1;i++){
                if((tokens.get(i)+tokens.get(i+1)).equals(best)){
                    tokens.set(i,best);
                    tokens.remove(i+1);
                }
            }

            // update freq
            freq.clear();
            for(String t: tokens) freq.put(t, freq.getOrDefault(t,0)+1);
        }

        // final vocab
        for(String t: tokens) vocab.put(t,vocab.size());
    }

    public Map<String,Integer> getVocab(){
        return vocab;
    }
}