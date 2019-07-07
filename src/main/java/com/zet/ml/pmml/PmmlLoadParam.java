/*
 * Copyright 2018, Zetyun StreamTau All rights reserved.
 */

package com.zet.ml.pmml;

public class PmmlLoadParam {
    private PmmlParameters pmmlParam;
    private PmmlDataHolder pmmlDataHolder;

    public PmmlParameters getPmmlParam() {
        return pmmlParam;
    }

    public void setPmmlParam(PmmlParameters pmmlParam) {
        this.pmmlParam = pmmlParam;
    }

    public PmmlDataHolder getPmmlDataHolder() {
        return pmmlDataHolder;
    }

    public void setPmmlDataHolder(PmmlDataHolder pmmlDataHolder) {
        this.pmmlDataHolder = pmmlDataHolder;
    }

    /**
     * Create pmmlLoadParam from pmmlParam.
     *
     * @param pmmlParam pmml param
     * @param pmmlDataHolder pmml data holder
     * @return pmml load parameter
     */
    public static PmmlLoadParam from(PmmlParameters pmmlParam, PmmlDataHolder pmmlDataHolder) {
        PmmlLoadParam loadParam = new PmmlLoadParam();
        loadParam.setPmmlParam(pmmlParam);
        loadParam.setPmmlDataHolder(pmmlDataHolder);
        return loadParam;
    }
}
