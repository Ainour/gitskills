package com.zet.ml.pmml;

public class PmmlParameters {

    private String modelPath;

    private Boolean maxProbability;

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    public Boolean getMaxProbability() {
        return maxProbability;
    }

    public void setMaxProbability(Boolean maxProbability) {
        this.maxProbability = maxProbability;
    }
}
