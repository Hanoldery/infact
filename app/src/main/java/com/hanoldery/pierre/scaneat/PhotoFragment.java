package com.hanoldery.pierre.scaneat;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.hanoldery.pierre.scaneat.helpers.inFactHelper;
import com.hanoldery.pierre.scaneat.helpers.urlHelpers;
import com.hanoldery.pierre.scaneat.objects.Product;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.impl.conn.ProxySelectorRoutePlanner;
import me.dm7.barcodescanner.zxing.ZXingScannerView;


/**
 * Created by Pierre on 21/01/2017.
 */




public class PhotoFragment extends Fragment implements ZXingScannerView.ResultHandler {
private ZXingScannerView mScannerView;

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mScannerView = new ZXingScannerView(getActivity());
        return mScannerView;
        }

@Override
public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        }

@Override

public void handleResult(Result rawResult) {
        /*Toast.makeText(getActivity(), "Contents = " + rawResult.getText() +
        ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();*/
    urlHelpers.with(getActivity()).getDetailsProduct(rawResult.getText().toString(), new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            try {
                JSONObject resp = inFactHelper.getRespWithControls(getActivity(), new String(responseBody));

                if (resp != null) {
                    Log.d("TEST", resp.getString("status_verbose"));
                    if (resp.getString("status") == "0")
                        Toast.makeText(getActivity(), "Contents = NO PORDUCT HERE", Toast.LENGTH_SHORT).show();
                    else {
                        Product product = new Product(resp.getJSONObject("product"));

                        //TEST
                        if (product.ingredients.size() > 0)
                            Toast.makeText(getActivity(), "Contents = " + product.product_name + " " + product.nutriments.carbohydrates_100g + " " + product.ingredients.get(0).id, Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getActivity(), "Contents = " + product.product_name + " " + product.nutriments.carbohydrates_100g + " none", Toast.LENGTH_SHORT).show();
                        //TEST

                        Intent i = new Intent(getContext(), DetailsActivity.class);
                        i.putExtra("product", product);
                        startActivity(i);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            error.printStackTrace();
        }

        @Override
        public void onFinish() {
            super.onFinish();
        }
    });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
@Override
public void run() {
        mScannerView.resumeCameraPreview(PhotoFragment.this);
        }
        }, 2000);
        }

@Override
public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
        }
    @Override
    public void onDestroy() {
        super.onDestroy();

        mScannerView.stopCamera();
        mScannerView = null;

    }
        }



/*

        Toast.makeText(getActivity(), "Contents = " + rawResult.getText() +
                ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();
        urlHelpers.with(getActivity()).getDetailsProduct(rawResult.getBarcodeFormat().toString(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject resp = inFactHelper.getRespWithControls(getActivity(), new String(responseBody));

                    if (resp != null) {
                        JSONArray itemsJSON = resp.getJSONArray("items");
                        for(int i = 0; i < itemsJSON.length(); i++) {
                            Log.d("TEST", itemsJSON.get(i).toString());
                            //items.add(new OrderItem(itemsJSON.getJSONObject(i)));
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
 */





/*

SCANNER EMMEBEDBED

public class PhotoFragment extends Fragment implements View.OnClickListener {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v;
        v = inflater.inflate(R.layout.fragment_photo, container, false);

        Button b = (Button) v.findViewById(R.id.launchScan);

        b.setOnClickListener(myhandler);
        return v;
    }

    View.OnClickListener myhandler = new View.OnClickListener() {
        public void onClick(View v) {
            launchScan();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v)
    {
        launchScan();
    }


    public void launchScan() {
        new IntentIntegrator(getActivity()).initiateScan();
    }


}
*/




/*
 GOOGLE STUFF
public class PhotoFragment extends Fragment {


    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private TextView barcodeInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v;
        v = inflater.inflate(R.layout.fragment_photo, container, false);

        cameraView = (SurfaceView) v.findViewById(R.id.camera_view);
        barcodeInfo = (TextView) v.findViewById(R.id.code_info);

        barcodeDetector =
                new BarcodeDetector.Builder(getActivity())
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();

        cameraSource = new CameraSource
                .Builder(getActivity(), barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException ie) {
                    Log.e("CAMERA SOURCE", ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    barcodeInfo.post(new Runnable() {    // Use the post method of the TextView
                        public void run() {
                            barcodeInfo.setText(    // Update the TextView
                                    barcodes.valueAt(0).displayValue
                            );
                        }
                    });
                }
            }
        });

        return v;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraSource.release();
        barcodeDetector.release();
    }

}*/







//ZXING FRAGMENT 2
/*
public class PhotoFragment extends Fragment {

    private CompoundBarcodeView barcodeView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View v;
        v = inflater.inflate(R.layout.activity_main_embeded, container, false);

        barcodeView = (CompoundBarcodeView) v.findViewById(R.id.barcode_scanner);
        barcodeView.decodeContinuous(callback);
        barcodeView.resume();

        return v;
    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                barcodeView.setStatusText(result.getText());
            }

            //Do something with code result
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    public void onResume() {
        barcodeView.resume();
        super.onResume();
    }

    @Override
    public void onPause() {
        barcodeView.pause();
        super.onPause();
    }
}
*/




/*
//ZXING FRAGMENT 1

public class PhotoFragment extends Fragment implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private LinearLayout qrCameraLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo, container, false);

        qrCameraLayout = (LinearLayout) v.findViewById(R.id.ll_qrcamera);
        mScannerView = new ZXingScannerView(getActivity().getApplicationContext());
        mScannerView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        qrCameraLayout.addView(mScannerView);

        List<BarcodeFormat> formats = new ArrayList<>();
        formats.add(BarcodeFormat.QR_CODE);
        mScannerView.setFormats(formats);
        mScannerView.startCamera();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    public void resume(){
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    public void pause(){
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(final Result result) {
        //handling results
    }

}*/





/*


public class PhotoFragment extends Fragment implements ZBarScannerView.ResultHandler  {
    private ZBarScannerView mScannerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mScannerView = new ZBarScannerView(getActivity());
        return mScannerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        Toast.makeText(getActivity(), "Contents = " + rawResult.getContents() +
                ", Format = " + rawResult.getBarcodeFormat().getName(), Toast.LENGTH_SHORT).show();
        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(PhotoFragment.this);
            }
        }, 2000);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }
}




*/









/*
public class PhotoFragment extends Fragment implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;


    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v(TAG, rawResult.toString()); // Prints scan results
        //Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mScannerView = new ZXingScannerView(getContext());   // Programmatically initialize the scanner view
        return mScannerView;

        //return inflater.inflate(R.layout.fragment_photo, container, false);
    }
}*/
