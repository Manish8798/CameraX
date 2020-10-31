package com.example.beadcoretask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.beadcoretask.adapter.MyAdapter;

public class GalleryAct extends AppCompatActivity {

    private DataBaseHandler dataBaseHandler;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        recyclerView = findViewById(R.id.recycler_view);
        empty = findViewById(R.id.empty_data);
        dataBaseHandler = new DataBaseHandler(this);

        try {
            myAdapter = new MyAdapter(dataBaseHandler.getAllImageData(), this);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
            recyclerView.setAdapter(myAdapter);

        }
        catch (Exception e){
            e.printStackTrace();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}