package aniket762.combinehealth.training;

import aniket762.combinehealth.core.Matrix;

public class Optimizer{
    public float learningRate;

    public Optimizer(float lr){
        this.learningRate =lr;
    }

    // Gradients for Linear Layers + FFN
    public void step(Matrix weight, Matrix grad, float lr){
        for(int i=0;i<weight.rows;i++){
            for(int j=0;j<weight.cols;j++){
                weight.data[i][j] -= lr*grad.data[i][j];
            }
        }
    }
}