package aniket762.combinehealth.training;

import aniket762.combinehealth.core.Matrix;

public class Loss {

    public static float crossEntropy(Matrix logits, int[] targets) {
        int T = targets.length;
        int V = logits.cols;

        float loss = 0f;

        for (int i = 0; i < T; i++) {
            float max = Float.NEGATIVE_INFINITY;
            for (int j = 0; j < V; j++) {
                max = Math.max(max, logits.data[i][j]);
            }

            float sum = 0f;
            for (int j = 0; j < V; j++) {
                sum += Math.exp(logits.data[i][j] - max);
            }

            int target = targets[i];
            float logProb =
                    (logits.data[i][target] - max) - (float) Math.log(sum);

            loss -= logProb;
        }

        return loss / T;
    }
}
