package com.example.jjj;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by project on 2015-04-30.
 */
public class Recipe {
    public List<String> conditions = new ArrayList<>();
    public List<Integer> conditionValues = new ArrayList<>();
    public String action;

    public Recipe(List<String> conditions, List<Integer> values, String action){
        this.conditions = conditions;
        this.conditionValues = values;
        this.action = action;
    }
}
