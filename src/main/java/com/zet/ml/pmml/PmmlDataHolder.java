/*
 * Copyright 2018, Zetyun StreamTau All rights reserved.
 */

package com.zet.ml.pmml;

import org.jpmml.evaluator.Evaluator;

public class PmmlDataHolder {
    private Evaluator evaluator;

    public Evaluator getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
    }
}
