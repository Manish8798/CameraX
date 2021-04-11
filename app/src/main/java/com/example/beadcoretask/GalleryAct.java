package com.example.beadcoretask;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.beadcoretask.adapter.MyAdapter;
import com.example.beadcoretask.databinding.ActivityGalleryBinding;

public class GalleryAct extends AppCompatActivity {

    ActivityGalleryBinding binding;
    DataBaseHandler dataBaseHandler;
    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dataBaseHandler = new DataBaseHandler(this);

        try {
            myAdapter = new MyAdapter(dataBaseHandler.getAllImageData(), this);
            binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            binding.recyclerView.setAdapter(myAdapter);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}