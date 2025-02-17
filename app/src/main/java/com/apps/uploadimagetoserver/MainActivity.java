package com.apps.uploadimagetoserver;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {


    TextView show_tv;
    Button upload_btn;

    ImageView imageView,edit_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit_image = findViewById(R.id.edit_image);
        imageView = findViewById(R.id.pick_image);
        upload_btn = findViewById(R.id.upload_btn);
        show_tv = findViewById(R.id.show_tv);


        //.........................pick image ........................................

        edit_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //..................................ImagePicker.....................................

                ImagePicker.with(MainActivity.this)
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .createIntent(new Function1<Intent, Unit>() {
                            @Override
                            public Unit invoke(Intent intent) {
                                activityResultLauncher.launch(intent);
                                return null;
                            }
                        });
                //..................................ImagePicker.....................................





                /*
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);


                if (checkCameraPermission()){

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivity(intent);

                }

                */



            }
        });
        //.........................pick image ........................................







        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                String encodedImage = Base64.encodeToString(imageBytes,Base64.DEFAULT);

                stringRequestServer(encodedImage);

            }
        });








    }


//.............................ActivityResultLauncher........................................................................
    ActivityResultLauncher<Intent>
            activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if (result.getResultCode()== Activity.RESULT_OK){
                Toast.makeText(MainActivity.this, "Image Selected", Toast.LENGTH_SHORT).show();
                Intent intent = result.getData();
                Uri uri = intent.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                    imageView.setImageBitmap(bitmap);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
            else {
                Toast.makeText(MainActivity.this, "Image Not Selected", Toast.LENGTH_SHORT).show();
            }

        }
    });
//.............................ActivityResultLauncher........................................................................





/*
    private boolean checkCameraPermission() {
        boolean hasPermission = false;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED){
            hasPermission = true;
        }
        else {

            hasPermission = false;
            String [] permission = {Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this,permission,102);

        }


        return hasPermission;
    }

*/






    private void stringRequestServer(String image64) {


        String URL = "http://192.168.0.117/apps/image_file.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                show_tv.setText(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                show_tv.setText(error.toString());

            }
        }){

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map map = new HashMap<String,String>();
                map.put("images",image64);

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);












    }
}