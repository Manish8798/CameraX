package com.example.beadcoretask;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class DataBaseHandler extends SQLiteOpenHelper {

    Context context;
    private static final String DATABASE_NAME = "mydb.db";
    private static final int DATABASE_VERSION = 1;
    private static String createTableQuery = "create table imageInfo (imageName TEXT" +
            ", image BLOB)";
    private ByteArrayOutputStream objectByteArrayOutputStream;
    private byte[] imageInBytes;

    public DataBaseHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(createTableQuery);
            Toast.makeText(context, "Database created successfully", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void storeImage(ModelClass objectModelClass){
        try {
            SQLiteDatabase objectSQLiteDatabase = this.getWritableDatabase();
            Bitmap imageToStoreBitmap = objectModelClass.getImage();
            objectByteArrayOutputStream = new ByteArrayOutputStream();
            imageToStoreBitmap.compress(Bitmap.CompressFormat.JPEG, 100, objectByteArrayOutputStream);
            imageInBytes = objectByteArrayOutputStream.toByteArray();
            ContentValues objectContentValues = new ContentValues();

            objectContentValues.put("imageName", objectModelClass.getImageName());
            objectContentValues.put("image", imageInBytes);

            long check = objectSQLiteDatabase.insert("imageInfo", null, objectContentValues);
            if (check != -1){
                Toast.makeText(context, "Data added", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(context, "failed to add", Toast.LENGTH_SHORT).show();
            }

        }
        catch (Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList<ModelClass> getAllImageData(){
        try {
            SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            ArrayList<ModelClass> modelClassArrayList = new ArrayList<>();
            Cursor cursor = sqLiteDatabase.rawQuery("select * from imageInfo", null);
            if (cursor.getCount() != 0){
                while (cursor.moveToNext()){
                    String nameOfImage = cursor.getString(0);
                    byte[] imageBytes = cursor.getBlob(1);

                    Bitmap objBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    modelClassArrayList.add(new ModelClass(nameOfImage, objBitmap));
                }

                cursor.close();
                sqLiteDatabase.close();
                return modelClassArrayList;
            }
            else {
                Toast.makeText(context, "No file", Toast.LENGTH_SHORT).show();
                return null;
            }
        }
        catch (Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
