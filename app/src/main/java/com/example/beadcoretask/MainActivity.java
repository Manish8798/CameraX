package com.example.beadcoretask;

import android.app.Activity;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.UseCase;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final int REQ_CODE_PERMISSION = 1001;
    private final String[] REQ_PERMS = new String[]{
            "android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"
    };

    private List<UseCase> mUseCases;
    private ExecutorService mImageCaptureExecutorService;


    PreviewView previewView;
    Button capture, gallery;
    Preview preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.viewFinder);
        capture = findViewById(R.id.capture);
        gallery = findViewById(R.id.gallery);

        mImageCaptureExecutorService = Executors.newSingleThreadExecutor();

        if(allPermissionsGranted()){
            startCamera();
        } else{
            ActivityCompat.requestPermissions(this, REQ_PERMS,
                    REQ_CODE_PERMISSION);
        }

    }

    private void startCamera() {

        final ListenableFuture<ProcessCameraProvider>
                cameraProviderListenableFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderListenableFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderListenableFuture.get();
                    bindPreview(cameraProvider);
                }catch (ExecutionException | InterruptedException e){
                    Toast.makeText(MainActivity.this, "Error "+e.toString(), Toast.LENGTH_SHORT).show();
                }
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
            try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
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
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d( "Saved", outputFileResults.getSavedUri().toString());
                            Toast.makeText(MainActivity.this, "Image Saved", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    exception.printStackTrace();
                }
            });} catch (Exception e){

            }
        });
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQ_CODE_PERMISSION && allPermissionsGranted()){
            startCamera();
        }else {
            Toast.makeText(this, "Allow Permissions", Toast.LENGTH_SHORT).show();
            this.finish();

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageCaptureExecutorService.shutdown();
    }
}