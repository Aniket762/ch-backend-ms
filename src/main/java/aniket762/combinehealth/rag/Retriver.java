package aniket762.combinehealth.rag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

// Given a query vector, returns top-K closest chunks
public class Retriver {
    private VectorStore store;

    public Retriver(VectorStore store){
        this.store = store;
    }

    public List<DocumentChunk> retrieve(float[] queryEmbedding, int topK){
        // Max heap
        PriorityQueue<DocumentChunk> pq =
                new PriorityQueue<>(
                        Comparator.comparingDouble(
                                c -> -cosineSimilarity(queryEmbedding, c.embedding)
                        )
                );

        for(DocumentChunk chunk: store.getChunks()){
            pq.add(chunk);
        }

        List<DocumentChunk> res = new ArrayList<>();
        for(int i=0;i<topK && !pq.isEmpty();i++){
            res.add(pq.poll()); // fetches the top element + removes from the heap
        }
        return res;
    }

    private float cosineSimilarity(float[] a, float[] b) {
        float dot = 0f, normA=0f, normB=0f;
        for(int i=0;i<a.length;i++){
            dot += a[i]*b[i];
            normA += a[i]*a[i];
            normB += b[i]*b[i];
        }
        return dot/((float)Math.sqrt(normA))*((float)Math.sqrt(normB)+1e-8f);
    }
}