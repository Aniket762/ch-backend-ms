package aniket762.combinehealth.tokenizer;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Tokenizer {
    private Map<String,Integer> token2id;
    private Map<Integer,String> id2token;
    private int vocabSize;

    public Tokenizer(){
        token2id = new HashMap<>();
        id2token = new HashMap<>();
        vocabSize = 0;
        addToken("<PAD>");
        addToken("<UNK>");
    }

    public void addToken(String token){
        if(!token2id.containsKey(token)){
            token2id.put(token,vocabSize);
            id2token.put(vocabSize,token);
            vocabSize++;
        }
        token2id.get(token);
    }

    public int[] encode(String text){
        String[] words = text.split("\\s+");
        int[] tokens = new int[words.length];
        for(int i=0;i<words.length;i++){
            tokens[i] = token2id.getOrDefault(words[i],token2id.get("<PAD>"));
        }
        return tokens;
    }

    public String decode(int[] tokens){
        StringBuilder sb = new StringBuilder();
        for(int id:tokens){
            sb.append(id2token.getOrDefault(id,"<PAD>")).append(" ");
        }
        return sb.toString().trim();
    }
}