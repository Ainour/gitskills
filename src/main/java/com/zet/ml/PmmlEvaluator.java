package com.zet.ml;

import com.zet.ml.pmml.PmmlDataHolder;
import com.zet.ml.pmml.PmmlEngine;
import com.zet.ml.pmml.PmmlLoadParam;
import com.zet.ml.pmml.PmmlParameters;
import org.jpmml.evaluator.Classification;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.Value;
import org.jpmml.evaluator.ValueMap;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PmmlEvaluator {
    public static void main( String[] args )
    {
        String modelPath = "E:\\maven\\work_use\\rtmachinelearning-master\\Cluster\\kmeans\\kmeans_plus.pmml";
        String dataPath = "E:\\maven\\work_use\\rtmachinelearning-master\\Cluster\\data\\test.csv";
        String resPath = "E:\\maven\\work_use\\rtmachinelearning-master\\Cluster\\kmeans\\kmeans_y_pred_by_rt.csv";
        Boolean maxProb = false;
        /*
        String modelPath = "/Users/lujun/Downloads/xgboost-classifier.pmml";
        String dataPath = "/Users/lujun/Downloads/iris-data.csv";
        String resPath = "/Users/lujun/Downloads/iris-result.csv";
        Boolean maxProb = true;
        */
        PmmlParameters parameters = new PmmlParameters();
        parameters.setModelPath(modelPath);
        parameters.setMaxProbability(maxProb);
        PmmlLoadParam loadParam = loadPmml(parameters, modelPath);
        PmmlUse pmmlUse = new PmmlUse();
        Evaluator evaluator = pmmlUse.loadPmml(loadParam);
        BufferedReader br = null;
        try {
                List<Map<String, Object>> predictions = new ArrayList<>();
                String[] names = null;
                String line;
                Integer lineIndex = 0;
                br = new BufferedReader(new FileReader(dataPath));
                while ((line = br.readLine()) != null) {
                    if (lineIndex < 1) {
                        names = line.split(",");
                        lineIndex ++;
                        continue;
                    }
                    //long start = System.currentTimeMillis();
                    //long now = System.currentTimeMillis();
                    String[] arr = line.split("\\s*,\\s*");
                    int length = arr.length;

                    Map record = new HashMap<String, Object>(length);
                    for (int i = 0; i < length; i++) {
                        record.put(names[i], arr[i]);
                    }

                    Map<String, Object> res = PmmlEngine.evaluate(record,evaluator);
                    predictions.add(parseResult(parameters, res));
                    lineIndex ++;
                }
                br.close();
                writeResultToFile(resPath, predictions);
            System.out.println( "PMML evaluation is completed!" );
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println( "PMML evaluation is failed " );
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void writeResultToFile(String resPath, List<Map<String, Object>> result)
    {
        FileOutputStream outStr = null;
        BufferedOutputStream buff = null;
        try {
            outStr = new FileOutputStream(new File(resPath));
            buff = new BufferedOutputStream(outStr);
            Boolean header = false;
            String[] columns = new String[7];
            columns[0] = "Label_0";
            columns[1] = "Label_1";
            columns[2] = "Label_2";
            columns[3] = "Label";
            columns[4] = "Label_3";
            columns[5] = "Label_4";
            columns[6] = "Label_5";
            for (Map<String, Object> pred: result) {
                StringBuilder sb = new StringBuilder();
                if (!header) {
                    int i = 0;
                    int j = 0;
                    for (Map.Entry<String, Object> nextEntry : pred.entrySet()) {
                        if (i > 0) {
                            sb.append(",");
                        }
                        String name = nextEntry.getKey();
                        if ((!name.equals(columns[i]))&&(j == 0)){
                            sb.append(columns[i]);
                            sb.append(",");
                            j++;
                        }
                        sb.append(nextEntry.getKey());
                        i ++;
                    }
                    sb.append("\n");
                    buff.write(sb.toString().getBytes());
                    header = true;
                    continue;
                }
                int i = 0;
                int j = 0;
                for (Map.Entry<String, Object> nextEntry : pred.entrySet()) {
                    if (i > 0) {
                        sb.append(",");
                    }
                    String name = nextEntry.getKey();
                    if ((!name.equals(columns[i]))&&(j == 0)){
                        sb.append("0,");
                        j++;
                    }
                    Object val = nextEntry.getValue();
                    sb.append(val.toString());
                    i ++;
                }
                sb.append("\n");
                buff.write(sb.toString().getBytes());
            }
            buff.flush();
            buff.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                buff.close();
                outStr.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static PmmlLoadParam loadPmml(PmmlParameters parameters, String modelPath)
    {
        try {
            FileInputStream is = new FileInputStream(modelPath);
            PmmlDataHolder dataHolder = PmmlEngine.createPmmlDataHolder(is);

            return PmmlLoadParam.from(parameters, dataHolder);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println( "Model file IO exception" );
        }
        return null;
    }

    private static Map<String, Object> parseResult(PmmlParameters parameters, Map<String, Object> res) {
        Map predictions = new HashMap<String, Object>();
        Boolean maxProba = parameters.getMaxProbability();
        for (Map.Entry<String, Object> nextEntry : res.entrySet()) {
            String fieldName = nextEntry.getKey();
            // Output result and probabilities for classification type, result only for regression and others
            if (nextEntry.getValue() instanceof Classification) {
                ValueMap<String, Double> valueMap = ((Classification) nextEntry.getValue()).getValues();
                Double proba = 0D;
                for (Map.Entry<String, Value<Double>> entry : valueMap.entrySet()) {
                    if (!maxProba) {
                        predictions.put(String.join("_", fieldName, entry.getKey()),
                                entry.getValue().getValue());
                    } else {
                        Double ep = 0D;
                        Object v = entry.getValue().getValue();
                        if (v instanceof Float) {
                            ep = Double.valueOf(((Float) v).floatValue());
                        } else if (v instanceof Double) {
                            ep = Double.valueOf(((Double) v).doubleValue());
                        }
                        if (proba < ep) {
                            proba = ep;
                        }
                    }
                }
                // Output the maximum probability if maxProba option is true
                if (maxProba) {
                    predictions.put(String.join("_", fieldName, "maxProba"), proba);
                }
                predictions.put(fieldName, ((Classification) nextEntry.getValue()).getResult());
            } else {
                predictions.put(fieldName, nextEntry.getValue());
            }
        }
        return predictions;
    }

    public static void printUsage() {
        String usage = "Usage:\n" + "\t\t"
                + "java -cp RtMachineLearning-1.0-SNAPSHOT.jar com.zet.ml.PmmlEvaluator "
                + "modelPath dataPath resultPath maxProb"
                + "\nEg:\n\t\t"
                + "java -cp RtMachineLearning-1.0-SNAPSHOT.jar com.zet.ml.PmmlEvaluator "
                + "./model.pmml ./input.csv ./output.csv false";
        System.out.println(usage);
    }
}
