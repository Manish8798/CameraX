package com.example.beadcoretask;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beadcoretask.adapter.MyAdapter;

public class GalleryAct extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        DataBaseHandler dataBaseHandler;
        RecyclerView recyclerView;
        MyAdapter myAdapter;

        recyclerView = findViewById(R.id.recycler_view);
        dataBaseHandler = new DataBaseHandler(this);

        try {
            myAdapter = new MyAdapter(dataBaseHandler.getAllImageData(), this);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            recyclerView.setAdapter(myAdapter);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}