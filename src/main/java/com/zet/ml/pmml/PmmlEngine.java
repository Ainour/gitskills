/*
 * Copyright 2018, Zetyun StreamTau All rights reserved.
 */

package com.zet.ml.pmml;

import org.dmg.pmml.FieldName;
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.InputField;
import org.jpmml.evaluator.ModelEvaluatorFactory;
import org.jpmml.evaluator.ReportingValueFactoryFactory;
import org.jpmml.evaluator.ValueFactoryFactory;
import org.jpmml.evaluator.visitors.ElementInternerBattery;
import org.jpmml.model.PMMLUtil;
import org.jpmml.model.VisitorBattery;
import org.jpmml.model.visitors.AttributeInternerBattery;
import org.jpmml.model.visitors.LocatorNullifier;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PmmlEngine {
    private static Converter<Map, Map> defaultResConverter = new DefaultPmmlResultConverter();

    private static PMML unmarshal(InputStream is) {
        try {
            return PMMLUtil.unmarshal(is);
        } catch (Exception ex) {
            throw  new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    /**
     * Load pmml model from input stream.
     *
     * @param is input stream
     * @return pmml model data holder
     */
    public static PmmlDataHolder createPmmlDataHolder(InputStream is) {
        PmmlDataHolder res = new PmmlDataHolder();
        PMML pmml = unmarshal(is);
        VisitorBattery visitorBattery = new VisitorBattery();
        // Getting rid of SAX Locator information
        visitorBattery.add(LocatorNullifier.class);

        // Getting rid of duplicate PMML attribute values and PMML elements
        visitorBattery.addAll(new AttributeInternerBattery());
        visitorBattery.addAll(new ElementInternerBattery());

        visitorBattery.applyTo(pmml);
        ModelEvaluatorFactory modelEvaluatorFactory = ModelEvaluatorFactory.newInstance();
        ValueFactoryFactory valueFactoryFactory = ReportingValueFactoryFactory.newInstance();
        modelEvaluatorFactory.setValueFactoryFactory(valueFactoryFactory);
        Evaluator evaluator = (Evaluator)modelEvaluatorFactory.newModelEvaluator(pmml);
        evaluator.verify();
        res.setEvaluator(evaluator);
        return res;
    }

    /**
     * Evaluate.
     * @param pmmlLoadParam pmml load parameter
     * @param arguments parameters
     * @return result map
     */
    public static Map evaluate(Map<String, Object> arguments,
                               Evaluator evaluator) {
        Map<String, Object> result = null;

        Map<FieldName, FieldValue> evaluatorInput = new LinkedHashMap<>();
        populateInputParams(evaluator, arguments, evaluatorInput);

        Map<FieldName, ?> evaluatorOutput = evaluator.evaluate(evaluatorInput);


        try {
            result = defaultResConverter.convert(evaluatorOutput, evaluator);
        } catch (ConversionException e) {
            System.out.println("Error convertering evaluation result");
        }

        return result == null ? Collections.emptyMap() : result;
    }

    protected static void populateInputParams(Evaluator evaluator,
                                              Map<String, Object> arguments,
                                              Map<FieldName, FieldValue> evaluatorInput) {
        List<InputField> inputFields = evaluator.getInputFields();
        FieldName inputFieldName = null;
        FieldValue inputFieldValue = null;
        Object rawValue = null;
        for (InputField inputField : inputFields) {
            inputFieldName = inputField.getName();
            rawValue = arguments.get(inputField.getName().getValue());
            inputFieldValue = inputField.prepare(rawValue);
            evaluatorInput.put(inputFieldName, inputFieldValue);
        }
    }
}
