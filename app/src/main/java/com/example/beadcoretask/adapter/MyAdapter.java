package com.example.beadcoretask.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beadcoretask.ModelClass;
import com.example.beadcoretask.R;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    List<ModelClass> modelClassList;
    Context context;

    public MyAdapter(List<ModelClass> modelClassList, Context context) {
        this.modelClassList = modelClassList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.row_activity, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelClass modelClass = modelClassList.get(position);
        holder.pic.setImageBitmap(modelClass.getImage());
        holder.name.setText(modelClass.getImageName());
    }

    @Override
    public int getItemCount() {
        return null != modelClassList?modelClassList.size():0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView pic;
        public TextView name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            pic = itemView.findViewById(R.id.image_row);
            name = itemView.findViewById(R.id.name_row);
        }
    }

}
