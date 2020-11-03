package com.example.beadcoretask;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.FileNotFoundException;

public class ImageActivity extends AppCompatActivity {

    TextView file_name;
    Bitmap src;
    String address, name;
    ImageView imageView;
    DataBaseHandler dataBaseHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageView = findViewById(R.id.image_view);
        file_name = findViewById(R.id.file_name);

        dataBaseHandler = new DataBaseHandler(this);

        Intent intent = getIntent();
        name = intent.getStringExtra("Bitmap");
        address = intent.getStringExtra("address");
        if(getIntent()!= null){
            try {
                file_name.setText(name);
                src = BitmapFactory.decodeStream(openFileInput(name));
                Glide.with(this).asBitmap().load(src).into(imageView);
            }
            catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }

        imageView.setOnLongClickListener(v -> {
            Toast.makeText(ImageActivity.this, address, Toast.LENGTH_SHORT).show();
            return false;
        });
    }


    public void clear_btn(View view) {
        Intent backIntent = new Intent(this, MainActivity.class);
        this.startActivity(backIntent);
        this.finish();
    }

    public void save_btn(View view) {
        try {
            if (imageView.getDrawable() != null){
                dataBaseHandler.storeImage(new ModelClass(name, src));
            }
            else {
                Toast.makeText(this, "No Image", Toast.LENGTH_SHORT).show();
            }
            Intent backIntent = new Intent(this, MainActivity.class);
            this.startActivity(backIntent);
            this.finish();
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}