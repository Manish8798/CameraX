package com.example.beadcoretask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.FileNotFoundException;

public class ZoomAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);
        ImageView imageView = findViewById(R.id.zoom_image);
        TextView textView_z = findViewById(R.id.zoom_name);

        String name_z = getIntent().getStringExtra("pic");
        if (getIntent() != null){

            try {
                Bitmap zoomBmp = BitmapFactory.decodeStream(openFileInput(name_z));
                Glide.with(this).asBitmap().load(zoomBmp).into(imageView);
                textView_z.setText(name_z);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

    }
}