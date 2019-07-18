package com.zet.ml;

import org.dmg.pmml.MiningField;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.Model;
import org.dmg.pmml.PMML;
import org.dmg.pmml.mining.MiningModel;
import org.dmg.pmml.mining.Segment;
import org.dmg.pmml.mining.Segmentation;

import java.util.HashMap;
import java.util.Map;

public class MiningModelPmml {
    public static Model_Rt extract(PMML pmml) {

        Model_Rt model_rt = new Model_Rt();
        Map<String, Map<String,Double>> parameters = new HashMap<String, Map<String, Double>>();
        Map<String,Double> models = new HashMap<String,Double>();

        String target = null;
        Map<String,Double> parameter = new HashMap<String,Double>();

        for(Model model:pmml.getModels()){
            target = extractTarget(model.getMiningSchema());
            parameters = extractParameters((MiningModel)model);
            models = extractModels((MiningModel)model);
            break;
        }

        model_rt.setName("MiningModel");
        model_rt.setTarget(target);
        model_rt.setParameters(parameters);
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

    //获得每个模型的参数的权重
    private static Map<String,Map<String,Double>> extractParameters(MiningModel model){
        int time = 0;
        Map<String,Map<String,Double>> map = new HashMap<>();
        Map<String,Double> parameters = new HashMap<>();

        Segmentation segmentation = model.getSegmentation();
        int max = segmentation.getSegments().size();
        Model_Rt model_rt = new Model_Rt();

        for(Segment segment:segmentation.getSegments()){
            String type = segment.getModel().getMiningFunction().name();
            switch (type){
                case "REGRESSION":
                    model_rt = RegressionPmml.extract(segment.getModel());
                    break;
            }
            map.put(String.valueOf(time),model_rt.getModels());//这里应该写成model_rt.getParameters().get(String.valueOf(time)),然后再调用RegressionPmml的时候多传一个参数，不过因为实际上Model_Rt中models和parameters的一个value形式是一样的，所以就这么写了
            time++;
            if(time >= max-1)
                break;
}
        return map;
                }

    //获得每个模型的权重
    private static Map<String,Double> extractModels(MiningModel model){
        int time = 0;
        Map<String,Double> map = new HashMap<>();

        Segmentation segmentation = model.getSegmentation();
        int max = segmentation.getSegments().size();
        Model_Rt model_rt = new Model_Rt();

        for(Segment segment:segmentation.getSegments()){
            time++;
            if(time == max){
                String type = segment.getModel().getMiningFunction().name();
                switch (type){
                    case "REGRESSION":
                        model_rt = RegressionPmml.extract(segment.getModel());
                        break;
                    case "CLASSIFICATION":
                        model_rt = RegressionPmml.extract(segment.getModel());
                        break;
                }

                map = model_rt.getModels();
                break;
            }
        }
        return map;
    }
}
























