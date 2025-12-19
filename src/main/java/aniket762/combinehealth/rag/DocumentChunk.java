package aniket762.combinehealth.rag;

public class DocumentChunk{
    public String text;
    public float[] embedding;

    public DocumentChunk(String text, float[] embedding){
        this.text = text;
        this.embedding = embedding;
    }
}