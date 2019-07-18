package com.zet.ml;

import java.util.Map;

public class Model_Rt {
    /*
    name是模型的名字
    target是结果的名字
    parameters是在多分类或者二分类中，每一个模型的参数的权重
    models是在多分类或者二分类中，每一个模型的权重
     */
    private String name;
    private String target;
    private Map<String,Map<String,Double>> parameters;
    private Map<String,Double> models;

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }

    public void setTarget(String target){
        this.target = target;
    }
    public String getTarget(){
        return this.target;
    }

    public void setParameters(Map<String,Map<String,Double>> parameters){
        this.parameters = parameters;
    }
    public Map<String,Map<String,Double>> getParameters(){
        return this.parameters;
    }

    public void setModels(Map<String,Double> models){
        this.models = models;
    }
    public Map<String,Double> getModels(){
        return this.models;
    }
}

















