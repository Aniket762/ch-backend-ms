package aniket762.combinehealth.rag;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class VectorStore {
    private final List<DocumentChunk> chunks = new ArrayList<>();

    public void add(DocumentChunk documentChunk) {
        chunks.add(documentChunk);
    }
}