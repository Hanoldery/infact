package com.hanoldery.pierre.scaneat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.hanoldery.pierre.scaneat.objects.Product;

import java.io.Serializable;

/**
 * Created by Pierre on 25/01/2017.
 */

public class DetailsActivity extends Activity {

    Product product;

    public DetailsActivity(){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        product = (Product)getIntent().getSerializableExtra("product");


    }



}
