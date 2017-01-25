package com.hanoldery.pierre.scaneat.objects;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


/**
 * Created by Pierre on 24/01/2017.
 */

public class Nutriments implements Serializable {

    public Double sugar_100g;
    public Double carbohydrates_100g;
    public Double energy_100g;
    public Double fat_100g;
    public Double saturated_fat_100g;
    public Double fiber_100g;
    public Double proteins_100g;
    public Double salt_100g;
    public Double sodium_100g;
    public Double nutrition_score_fr_100g;

    public Nutriments(JSONObject json) {
        try {
            if(json.has("sugar_100g"))              sugar_100g = Double.valueOf(json.getString("sugar_100g"));
            if(json.has("carbohydrates_100g"))      carbohydrates_100g = Double.valueOf(json.getString("carbohydrates_100g"));
            if(json.has("energy_100g"))             energy_100g = Double.valueOf(json.getString("energy_100g"));
            if(json.has("fat_100g"))                fat_100g = Double.valueOf(json.getString("fat_100g"));
            if(json.has("saturated_fat_100g"))      saturated_fat_100g = Double.valueOf(json.getString("saturated_fat_100g"));
            if(json.has("fiber_100g"))              fiber_100g = Double.valueOf(json.getString("fiber_100g"));
            if(json.has("proteins_100g"))           proteins_100g = Double.valueOf(json.getString("proteins_100g"));
            if(json.has("salt_100g"))               salt_100g = Double.valueOf(json.getString("salt_100g"));
            if(json.has("sodium_100g"))             sodium_100g = Double.valueOf(json.getString("sodium_100g"));
            if(json.has("nutrition_score_fr_100g")) nutrition_score_fr_100g = Double.valueOf(json.getString("nutrition-score-fr_100g"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
