package aniket762.combinehealth.core;

public class Matrix {
    public int rows;
    public int cols;
    public float[][] data;

    public Matrix(int r, int c){
        rows = r;
        cols = c;
        data = new float[r][c];
    }

    public static Matrix random(int r, int c){
        Matrix m = new Matrix(r,c);
        for(int i=0;i<r;i++){
            for(int j=0;j<c;j++){
                m.data[i][j] = (float)(Math.random()*0.02 - 0.01);
            }
        }
        return m;
    }

    public float[] lastRow() {
        if (rows == 0) return new float[0];
        return data[rows - 1];
    }

    public float get(int r, int c) {
        return data[r][c];
    }

    public int numRows() { return rows; }
    public int numCols() { return cols; }
}
