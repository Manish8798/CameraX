package com.example.beadcoretask;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.beadcoretask.databinding.ActivityImageBinding;

import java.io.FileNotFoundException;

public class ImageActivity extends AppCompatActivity {

    Bitmap src;
    String address, name;
    DataBaseHandler dataBaseHandler;
    private ActivityImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        saveBtn.setBackgroundColor(getResources().getColor(R.color.design_default_color_secondary_variant));
//        clearBtn.setBackgroundColor(getResources().getColor(R.color.design_default_color_secondary_variant));

        dataBaseHandler = new DataBaseHandler(this);

        Intent intent = getIntent();
        name = intent.getStringExtra("Bitmap");
        address = intent.getStringExtra("address");
        if (getIntent() != null) {
            try {
                binding.fileName.setText(name);
                src = BitmapFactory.decodeStream(openFileInput(name));
                Glide.with(this).asBitmap().load(src).into(binding.imageView);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        binding.imageView.setOnLongClickListener(v -> {
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
            if (binding.imageView.getDrawable() != null) {
                dataBaseHandler.storeImage(new ModelClass(name, src));
            } else {
                Toast.makeText(this, "No Image", Toast.LENGTH_SHORT).show();
            }
            Intent backIntent = new Intent(this, MainActivity.class);
            this.startActivity(backIntent);
            this.finish();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}