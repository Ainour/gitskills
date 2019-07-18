package com.zet.ml;

import org.dmg.pmml.*;
import org.dmg.pmml.regression.NumericPredictor;
import org.dmg.pmml.regression.RegressionModel;
import org.dmg.pmml.regression.RegressionTable;
import org.dmg.pmml.MiningSchema;

import java.util.HashMap;
import java.util.Map;

//二分类的回归模型
public class RegressionPmml {

    //接受来自PmmlEvaluator的参数，
    public static Model_Rt extract(PMML pmml) {

        Model_Rt model_rt = new Model_Rt();
        Map<String,Map<String,Double>> parameters = new HashMap<>();
        Map<String,Double> models = new HashMap<>();

        String target = null;

        for(Model model:pmml.getModels()){
            target = extractTarget(model.getMiningSchema());
            parameters = extractParameters((RegressionModel)model);
            models = extractModels();
            break;
        }

        model_rt.setName("Regression");
        model_rt.setTarget(target);
        model_rt.setParameters(parameters);
        model_rt.setModels(models);

        return model_rt;
    }

    //接受来自MiningModelPmml的参数
    public static Model_Rt extract(Model model){

        Model_Rt model_rt = new Model_Rt();
        RegressionModel regressionModel = (RegressionModel)model;

        Map<String,Double> models = extractParameters(regressionModel).get("0");
        model_rt.setModels(models);

        return model_rt;

    }

    //获得结果的名字
    private static String extractTarget(MiningSchema miningSchema) {
        String target = null;

        for(MiningField miningField:miningSchema.getMiningFields()){
            if(miningField.getUsageType().name().equals("TARGET")) {
                target = miningField.getName().toString();
                break;
            }
        }

        return target;
    }

    //参数的权重
    private static Map<String,Map<String,Double>> extractParameters(RegressionModel model){
        int time = 0;
        Map<String,Map<String,Double>> map = new HashMap<>();
        Map<String,Double> parameters = new HashMap<>();
        int max = model.getRegressionTables().size();

        for(RegressionTable regressionTable:model.getRegressionTables()){
            Double intercept = regressionTable.getIntercept();
            parameters.put("intercept",intercept);
            for(NumericPredictor numericPredictor:regressionTable.getNumericPredictors()){
                parameters.put(numericPredictor.getName().toString(),numericPredictor.getCoefficient());
            }
            map.put(String.valueOf(time),parameters);
            time++;
            if(max == 2)
                break;
        }

        return map;
    }

    //获得每个模型的权重，暂时没用
    private static Map<String,Double> extractModels(){
        Map<String,Double> models = new HashMap<>();
        models.put("0",1.0);
        return models;
    }
}






















