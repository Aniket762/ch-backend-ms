package aniket762.combinehealth.training;

import aniket762.combinehealth.core.Matrix;

public class Loss {
    public static float crossEntropy(Matrix logits,int[] target){
        float loss = 0f;
        for(int i=0;i<target.length;i++){
            int t=target[i];
            float p = logits.data[i][t];
            loss -= Math.log(Math.max(p,1e-8f));
        }
        return loss/target.length;
    }
}