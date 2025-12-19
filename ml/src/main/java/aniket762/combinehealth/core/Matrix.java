package aniket762.combinehealth.core;

class Matrix{
    int rows,cols;
    float[][] data;

    Matrix(int r, int c){
        rows=r;
        cols=c;
        data = new float[r][c];
    }

    static Matrix random(int r, int c){
        Matrix m = new Matrix(r,c);
        for(int i=0;i<r;i++){
            for(int j=0;j<c;j++){
                m.data[i][j] = (float)(Math.random()*0.02-0.01);
            }
        }
        return m;
    }
}