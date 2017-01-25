package com.hanoldery.pierre.scaneat.objects;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Pierre on 24/01/2017.
 */

public class Product implements Serializable {
    public String product_name;
    public List<Ingredients> ingredients = new ArrayList<Ingredients>();
    public Nutriments nutriments;

    public Product(JSONObject json) {
        try {
            if(json.has("product_name"))  product_name = json.getString("product_name");
            if(json.has("ingredients"))
            {
                JSONArray itemsJSON = json.getJSONArray("ingredients");
                for(int i = 0; i < itemsJSON.length(); i++) {
                    Log.d("TEST", itemsJSON.get(i).toString());
                    Ingredients ing = new Ingredients(itemsJSON.getJSONObject(i).get("id").toString(), (itemsJSON.getJSONObject(i).has("percent"))?Integer.valueOf(itemsJSON.getJSONObject(i).get("percent").toString()):0);
                    ingredients.add(ing);
                }
            }
            if(json.has("nutriments") && !json.isNull("nutriments"))  nutriments = new Nutriments(json.getJSONObject("nutriments"));
        } catch (JSONException e){
                e.printStackTrace();
        }
    }
}
