package com.example.beadcoretask.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beadcoretask.ModelClass;
import com.example.beadcoretask.ZoomAct;
import com.example.beadcoretask.databinding.RowActivityBinding;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    public List<ModelClass> modelClassList;
    public Context context;

    public MyAdapter(List<ModelClass> modelClassList, Context context) {
        this.modelClassList = modelClassList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new MyViewHolder(RowActivityBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelClass modelClass = modelClassList.get(position);
        holder.binding.imageRow.setImageBitmap(modelClass.getImage());
        holder.binding.nameRow.setText(modelClass.getImageName());
        holder.binding.imageRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(context, ZoomAct.class);
//                sendIntent.putExtra("pic_name", modelClass.getImageName());
                sendIntent.putExtra("pic", createToBmp(modelClass.getImage()));
//                createToBmp(modelClass.getImage());
                context.startActivity(sendIntent);
            }

            private String createToBmp(Bitmap image) {
                String save_file = modelClass.getImageName();
                try {
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    FileOutputStream fo = context.openFileOutput(save_file, Context.MODE_PRIVATE);
                    fo.write(bytes.toByteArray());
                    // remember close file output
                    fo.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return save_file;
            }

        });
    }


    @Override
    public int getItemCount() {
        return null != modelClassList ? modelClassList.size() : 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        RowActivityBinding binding;

        public MyViewHolder(@NonNull RowActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
