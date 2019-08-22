/*
 * Copyright 2018, Zetyun StreamTau All rights reserved.
 */

package com.zet.ml.pmml;

import com.google.common.collect.BiMap;
import org.dmg.pmml.Entity;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.tree.Node;
import org.jpmml.evaluator.Classification;
import org.jpmml.evaluator.Computable;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.HasEntityId;
import org.jpmml.evaluator.HasEntityRegistry;
import org.jpmml.evaluator.HasProbability;
import org.jpmml.evaluator.TargetField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultPmmlResultConverter implements Converter<Map, Map> {

    @Override
    public Class<Map> getSourceClass() {
        return Map.class;
    }

    @Override
    public Class<Map> getTargetClass() {
        return Map.class;
    }

    @Override
    public Map convert(Map source) throws ConversionException {
        throw new UnsupportedOperationException("Direct conversion not supported");
    }

    /**
     * Convert a pmml evaluation result to a sensible output.
     * @param source  source
     * @param context  context
     * @return a map that inteprets the original pmml response
     * @throws ConversionException  conversion exception
     */
    public Map convert(Map source, Object context) throws ConversionException {
        Map<FieldName, ?> evaluatorOutput = (Map<FieldName, ?>)source;
        Map<String, Object> result = new HashMap<String, Object>();
        Evaluator evaluator = (Evaluator)context;
        List<TargetField> targetFields = evaluator.getTargetFields();
        for (TargetField targetField : targetFields) {
            FieldName targetFieldName = targetField.getName();
            try {
                if (targetFieldName != null) {
                    Object targetFieldValue = evaluatorOutput.get(targetFieldName);
                    result.put(targetFieldName.getValue(), convertTargetFieldVal(targetFieldName,
                            targetFieldValue, evaluator));
                } else { // No label case, Clustering...
                    for (Map.Entry nextEntry : evaluatorOutput.entrySet()) {
                        FieldName fieldName = (FieldName) nextEntry.getKey();
                        if (fieldName != null) {
                            result.put(fieldName.getValue(), nextEntry.getValue());
                        }
                    }
                }
            }
            catch (Exception ex) {
                System.out.println("Error converting pmml result field " + targetFieldName.getValue());
            }
        }
        return result;
    }

    protected Object convertTargetFieldVal(FieldName targetFieldName, Object targetFieldValue,
            Evaluator evaluator) {
        Object finalVal = targetFieldValue;
        // For classification type, customer usually needs result and probabilities of each entry
        // Pass the field value for RtEvent for further processing
        if (targetFieldValue instanceof Classification) {
            return finalVal;
        }
        if (targetFieldValue instanceof Computable) {
            Computable computable = (Computable)targetFieldValue;
            finalVal = computable.getResult();
        }
        if (targetFieldValue instanceof HasEntityId) {
            HasEntityId hasEntityId = (HasEntityId)targetFieldValue;
            HasEntityRegistry<?> hasEntityRegistry = (HasEntityRegistry<?>)evaluator;
            BiMap<String, ? extends Entity> entities = hasEntityRegistry.getEntityRegistry();
            Entity winner = entities.get(hasEntityId.getEntityId());

            // Test for "probability" result feature
            if (targetFieldValue instanceof HasProbability) {
                HasProbability hasProbability = (HasProbability)targetFieldValue;
                finalVal = hasProbability.getProbability(((Node) winner).getScore());
            }
        }
        return finalVal;
    }
}
