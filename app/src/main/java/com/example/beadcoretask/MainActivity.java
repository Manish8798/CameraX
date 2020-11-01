package com.example.beadcoretask;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

//    private final Executor executor = Executors.newSingleThreadExecutor();
    private final int REQ_CODE_PERMISSION = 1001;
    private final String[] REQ_PERMS = new String[]{
            "android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"
    };

//    private List<UseCase> mUseCases;
    private ExecutorService mImageCaptureExecutorService;
    FusedLocationProviderClient fusedLocationProviderClient;
    PreviewView previewView;
    Button capture, gallery;
    Preview preview;
    Bitmap prevBmp;
    String file_name;
    RelativeLayout relativeLayout_main;
    TextView textView1, textView2, textView3, textView4, textView_error;
    List<Address> addresses = new ArrayList<>();
    String add = "unknown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.viewFinder);
        relativeLayout_main = findViewById(R.id.relative_layout);
        capture = findViewById(R.id.capture);
        gallery = findViewById(R.id.gallery);
        textView_error = findViewById(R.id.error_loc);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mImageCaptureExecutorService = Executors.newSingleThreadExecutor();

        if (allPermissionsGranted()) {
            startCamera();
            getUserLocation();
        } else {
            ActivityCompat.requestPermissions(this, REQ_PERMS,
                    REQ_CODE_PERMISSION);
        }
//        if(ActivityCompat.checkSelfPermission(MainActivity.this,
//                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//            getUserLocation();
//        }else {
//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
//        }

    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            Toast.makeText(this, "Allow Location Permission", Toast.LENGTH_SHORT).show();
        }
        else {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                Location location = task.getResult();
                if (location != null) {
                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    try {
                        addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );
                        textView1.setText(Html.fromHtml(
                                "<font color = '#6200E'><b>Latitude : </b><b></font>"
                                        + addresses.get(0).getLatitude()
                        ));
                        textView2.setText(Html.fromHtml(
                                "<font color = '#6200E'><b>Longitude : </b><b></font>"
                                        + addresses.get(0).getLongitude()
                        ));
                        textView3.setText(Html.fromHtml(
                                "<font color = '#6200E'><b>Country : </b><b></font>"
                                        + addresses.get(0).getCountryName()
                        ));
                        textView4.setText(Html.fromHtml(
                                "<font color = '#6200E'><b>Locality : </b><b></font>"
                                        + addresses.get(0).getLocality()
                        ));
                        textView_error.setVisibility(View.GONE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void startCamera() {

        final ListenableFuture<ProcessCameraProvider>
                cameraProviderListenableFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderListenableFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderListenableFuture.get();
                bindPreview(cameraProvider);
            }catch (ExecutionException | InterruptedException e){
                Toast.makeText(MainActivity.this, "Error "+e.toString(), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

     void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().build();

        ImageCapture.Builder builder = new ImageCapture.Builder();

        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);
        if(hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)){
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        }

        final ImageCapture imageCapture = builder
                .setTargetRotation(this.getWindowManager()
                        .getDefaultDisplay().getRotation())
                .build();

        preview.setSurfaceProvider(previewView.createSurfaceProvider());
        cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture,
                imageAnalysis, preview);

        capture.setOnClickListener(v -> {
            relativeLayout_main.setVisibility(View.VISIBLE);
            textView_error.setVisibility(View.GONE);
            createDefaultFolderIfNotExist();
//                File file = new File(getBatchDirectoryName(),
//                        "Test_"+dateFormat.format(new Date())+".jpg");
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.
                        OutputFileOptions.Builder(getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues).build();

            imageCapture.takePicture(outputFileOptions, mImageCaptureExecutorService,
                    new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    Activity activity = MainActivity.this;
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
////                                Thread.sleep(1000);
//
//                                Log.d( "Saved", outputFileResults.getSavedUri().toString());
//                            }catch (Exception e){
//                                e.printStackTrace();
//                            }
//
//                        }
//                    });

                    activity.runOnUiThread(() -> {
                        try {
                            add = addresses.get(0).getAddressLine(0);
                            prevBmp = previewView.getBitmap();
                            Intent prevIntent = new Intent(MainActivity.this, ImageActivity.class);
                            prevIntent.putExtra("Bitmap", saveBitmap(prevBmp));
//                            saveBitmap(prevBmp);
//                            prevIntent.putExtra("name", file_name);
                            prevIntent.putExtra("address", add);
                            MainActivity.this.startActivity(prevIntent);
                            MainActivity.this.finish();
                        }
                        catch (Exception e){
                            e.printStackTrace();
                            textView_error.setVisibility(View.VISIBLE);
                            getUserLocation();
                            Toast.makeText(MainActivity.this, "Turn Location ON & Restart App",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    exception.printStackTrace();
                }
            });
        });
    }

    private String saveBitmap(Bitmap prevBmp) {

        try {
            file_name = System.currentTimeMillis()
                    +"_"+addresses.get(0).getLocality()+"_"+addresses.get(0).getCountryName();

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            prevBmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(file_name, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            fo.close();

        }catch (Exception e){
            e.printStackTrace();
            file_name = null;

        }
        return file_name;
    }


    public void createDefaultFolderIfNotExist(){
        File picFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if(!picFolder.exists() && !picFolder.mkdir()){
            Toast.makeText(this, "failed to create folder", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean allPermissionsGranted() {
        for(String permission : REQ_PERMS){
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if(requestCode == REQ_CODE_PERMISSION && allPermissionsGranted()){
            startCamera();
            getUserLocation();
        }
        else {
//            Toast.makeText(this, "Allow Permissions", Toast.LENGTH_SHORT).show();
//            this.finish();
            AlertDialog.Builder alertDialogueBuilder = new AlertDialog.Builder(this);
            alertDialogueBuilder.setTitle("Allow All Permissions");
            alertDialogueBuilder.setMessage("Exit App!")
                    .setCancelable(false)
                    .setPositiveButton("Exit", (dialog, which) -> {
                        //code
                        System.exit(0);
                    }).setNegativeButton("Stay", (dialog, which) -> {
                    dialog.cancel();
                ActivityCompat.requestPermissions(this, REQ_PERMS,
                        REQ_CODE_PERMISSION);
                    });

            AlertDialog alertDialog = alertDialogueBuilder.create();
            alertDialog.show();

        }

    }

    public void gallery(View view) {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()+"/Pictures/");
//        intent.setDataAndType(uri, "image/jpeg");
//        startActivity(Intent.createChooser(intent, "Gallery"));

        Intent intent = new Intent(MainActivity.this, GalleryAct.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageCaptureExecutorService.shutdown();
    }
}