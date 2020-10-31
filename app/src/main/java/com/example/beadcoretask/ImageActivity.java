package com.example.beadcoretask;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.FileNotFoundException;

public class ImageActivity extends AppCompatActivity {

    TextView file_name;
    Bitmap src;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageView = findViewById(R.id.image_view);
        file_name = findViewById(R.id.file_name);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        if(getIntent()!= null){
            try {
                src = BitmapFactory.decodeStream(openFileInput(name));
            } catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }
        file_name.setText(name);
        Glide.with(this).asBitmap().load(src).into(imageView);
    }

    public void clear_btn(View view) {
        Intent backIntent = new Intent(this, MainActivity.class);
        startActivity(backIntent);
        finish();
    }

    public void save_btn(View view) {
    }
}