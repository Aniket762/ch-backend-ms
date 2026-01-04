package aniket762.combinehealth.core;

public class MatrixOps{
    // Project embeddings into Q,K,V
    public static Matrix matmul(Matrix A, Matrix B){
        if(A.cols != B.rows) throw new IllegalArgumentException("Incompatible shapes");

        Matrix C = new Matrix(A.rows, B.cols);

        for(int i=0;i<A.rows;i++){
            for(int j=0; j<B.cols;j++){
                float sum = 0f;
                for(int k=0;k<A.cols;k++){
                    sum += A.data[i][k] * B.data[k][j];
                }
                C.data[i][j]= sum;
            }
        }

        return C;
    }

    // Residual Connection ( x + attention(x))
    // Training Stabilization
    public static Matrix add(Matrix A, Matrix B){
        if(A.rows != B.rows || A.cols!=B.cols) throw new IllegalArgumentException("Incompatible shapes");

        Matrix C = new Matrix(A.rows, A.cols);
        for(int i=0;i<A.rows;i++){
            for(int j=0;j<A.cols;j++){
                C.data[i][j] = A.data[i][j] + B.data[i][j];
            }
        }
        return C;
    }

    // Attention uses Q * Kt
    public static Matrix transpose(Matrix A){
        Matrix C = new Matrix(A.cols,A.rows);
        for(int i=0;i<A.rows;i++){
            for(int j=0;j<A.cols;j++){
                C.data[j][i] = A.data[i][j];
            }
        }
        return C;
    }

    // Attention weight sum to 1 : Score to P(E)
    public static void softMax(Matrix A){
        for(int i=0;i<A.rows;i++){
            float max = Float.NEGATIVE_INFINITY;
            for(int j=0;j<A.cols;j++){
                max = Math.max(max,A.data[i][j]);
            }

            float sum = 0;
            for(int j=0;j<A.cols;j++){
                A.data[i][j] = (float) Math.exp(A.data[i][j]-max);
                sum += A.data[i][j];
            }

            for(int j=0;j<A.cols;j++){
                A.data[i][j] = A.data[i][j]/sum;
            }
        }
    }

    // Stabilize deep stacking -> faster convergence
    public static void layerNorm(Matrix A){
        float epsilon = 1e-5f;

        for(int i=0;i<A.rows;i++){
            float mean = 0f;
            for(int j=0;j<A.cols;j++){
                mean += A.data[i][j];
            }
            mean /= A.cols;

            float variance = 0f;
            for(int j=0;j<A.cols;j++){
                float diff = A.data[i][j] - mean;
                variance += diff* diff;
            }
            variance /= A.cols;

            float denom = (float) Math.sqrt(variance+epsilon);
            for(int j=0;j<A.cols;j++){
                A.data[i][j] = (A.data[i][j]-mean)/denom;
            }
        }
    }

    public static void relu(Matrix A){
        for(int i=0;i<A.rows;i++){
            for(int j=0;j< A.cols;j++){
                A.data[i][j] = Math.max(0f, A.data[i][j]);
            }
        }
    }

}