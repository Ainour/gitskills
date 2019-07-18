package com.zet.ml;

import com.zet.ml.pmml.PmmlEngine;
import com.zet.ml.pmml.PmmlParameters;
import org.dmg.pmml.Model;
import org.dmg.pmml.PMML;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

public class GetPmml {
    public static void main( String[] args )
    {
        String modelPath = "E:\\maven\\work_use_pmml\\rtmachinelearning-master\\MulticlassClassification\\lr_multiclassification\\LR_Multiclass.pmml";
        Boolean maxProb = false;

        PmmlParameters parameters = new PmmlParameters();
        parameters.setModelPath(modelPath);
        parameters.setMaxProbability(maxProb);

        loadPmml(parameters, modelPath);
    }

    private static void loadPmml(PmmlParameters parameters, String modelPath)
    {
        try {
            FileInputStream is = new FileInputStream(modelPath);
            PMML pmml = PmmlEngine.createPmmlDataHolder(is);
            Model_Rt model_rt = new Model_Rt();

            for (Model model:pmml.getModels()){
                String model_s = model.toString();
                model_s = model_s.split("@")[0];
                String model_use = model_s;
                model_s = model_s.split("\\.")[model_use.split("\\.").length-1];
                //System.out.println(model_s);查看具体属于那个模型用于后面的调用

                switch (model_s){
                    case "RegressionModel":
                        model_rt = RegressionPmml.extract(pmml);
                        break;
                    case "MiningModel":
                        model_rt = MiningModelPmml.extract(pmml);
                        break;
                }
            }

            /*查看model_rt
            String name = model_rt.getName();
            String target = model_rt.getTarget();
            Map<String, Map<String,Double>> p = model_rt.getParameters();
            Map<String,Double> models = model_rt.getModels();
            System.out.println("name: "+name);
            System.out.println("target: "+target);
            System.out.println("parameters:");
            for(String key:p.keySet()){
                System.out.println(key+":");
                for (String pa:p.get(key).keySet()){
                    System.out.println(pa+": "+p.get(key).get(pa));
                }
            }
            System.out.println("models:");
            for(String key:models.keySet()){
                System.out.println(key+": "+models.get(key));
            }
             */

        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println( "Model file IO exception" );
        }
    }
}
