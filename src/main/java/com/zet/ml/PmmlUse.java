package com.zet.ml;

import com.zet.ml.pmml.PmmlDataHolder;
import com.zet.ml.pmml.PmmlLoadParam;
import com.zet.ml.pmml.PmmlParameters;
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.ModelEvaluatorFactory;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PmmlUse {
    public Evaluator loadPmml(PmmlLoadParam pmmlLoadParam) {

        PmmlParameters origParams = pmmlLoadParam.getPmmlParam();
        String pmmlName = origParams.getModelPath();

        PMML pmml = new PMML();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(pmmlName);
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        try {
            pmml = org.jpmml.model.PMMLUtil.unmarshal(inputStream);
        } catch (
                SAXException e1) {
            e1.printStackTrace();
        } catch (
                JAXBException e1) {
            e1.printStackTrace();
        }
        ModelEvaluatorFactory modelEvaluatorFactory = ModelEvaluatorFactory.newInstance();
        Evaluator evaluator = modelEvaluatorFactory.newModelEvaluator(pmml);


        PmmlDataHolder curHolder = pmmlLoadParam.getPmmlDataHolder();

        if (curHolder == null) {
            throw new IllegalArgumentException("No pmml module found by name: " + pmmlName);
        }
        return  evaluator;
    }
}
