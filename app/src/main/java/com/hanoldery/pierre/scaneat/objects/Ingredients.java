package com.hanoldery.pierre.scaneat.objects;

import java.io.Serializable;

/**
 * Created by Pierre on 24/01/2017.
 */

public class Ingredients implements Serializable {
    public String id;
    public int percent;

    public Ingredients(String _id, int _percent){
        id = _id;
        percent = _percent;
    }
}
