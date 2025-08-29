package com.example.artbook;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.artbook.databinding.ActivityArtBinding;
import com.example.artbook.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import javax.xml.transform.Result;

public class ArtActivity extends AppCompatActivity {

    private ActivityArtBinding binding ;
    ActivityResultLauncher<Intent> intentActivityResultLauncher;
    ActivityResultLauncher<String> Permissionlauncher;
    Bitmap selectedimage;
    SQLiteDatabase database;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityArtBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        database= this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);

        registerLauncher();

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        if(info.equals("new")){
            binding.nametext.setText("");
            binding.artisttextt.setText("");
            binding.yeartext.setText("");
            binding.nametext.setText("");
            binding.button.setVisibility(View.VISIBLE);
            binding.imageView.setClickable(true);
            binding.imageView.setImageResource(R.drawable.selectimage);
        }else{
            int artid= intent.getIntExtra("artid",1);
            binding.button.setVisibility(View.INVISIBLE);
            binding.imageView.setClickable(false);
            try{

                Cursor cursor = database.rawQuery("SELECT * FROM arts WHERE id= ?",new String[] {String.valueOf(artid)});
                int nameindex = cursor.getColumnIndex("name");
                int artistnameindex = cursor.getColumnIndex("artsitname");
                int yearindex = cursor.getColumnIndex("year");
                int imageindex = cursor.getColumnIndex("image");

                while(cursor.moveToNext()){
                    binding.nametext.setText(cursor.getString(nameindex));
                    binding.artisttextt.setText(cursor.getString(artistnameindex));
                    binding.yeartext.setText(cursor.getString(yearindex));
                    byte[] bytes = cursor.getBlob(imageindex);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
                    binding.imageView.setImageBitmap(bitmap);

                }
                cursor.close();

            }catch (Exception e ){
                e.printStackTrace();
            }
        }




    }


    public void save(View view ){

        String name = binding.nametext.getText().toString();
        String artsitname = binding.artisttextt.getText().toString();
        String yeartext  = binding.yeartext.getText().toString();

        Bitmap smallimage= makesmallerimage(selectedimage,300);
        ByteArrayOutputStream  outputstream = new ByteArrayOutputStream();
        smallimage.compress(Bitmap.CompressFormat.PNG,50,outputstream);
        byte[] bytearray = outputstream.toByteArray();

        try{
            database.execSQL("CREATE TABLE IF NOT EXISTS arts (id INTEGER PRIMARY KEY , name VARCHAR ,artsitname VARCHAR,year VARCHAR ,image BLOB)");

            String sglstring = "INSERT INTO arts (name,artsitname,year,image) VALUES(?, ?, ?, ?) ";
            SQLiteStatement statement = database.compileStatement(sglstring);
            statement .bindString(1,name);
            statement .bindString(2,artsitname);
            statement .bindString(3,yeartext);
            statement .bindBlob(4,bytearray);
            statement.execute();

        }catch (Exception e ){
            e.printStackTrace();
        }

        Intent intent = new Intent(ArtActivity.this,MainActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public Bitmap makesmallerimage ( Bitmap image , int maximumsize){

        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapratio= (float)  width / (float) height;
        if (bitmapratio  >1 ){
            // landscape image
            width= maximumsize;
            height= (int) (height / bitmapratio);
        }
        else{
            width= maximumsize;
            height= (int) (height * bitmapratio);
        }
        return  Bitmap.createScaledBitmap(image, width,height,true);
    }


    public void selectimage(View view){

         if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
             if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                 if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) {
                     Snackbar.make(view, "Permission needed for gallery!", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             Permissionlauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                         }
                     }).show();
                 } else {
                     Permissionlauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                 }
             } else {
                 Intent intenttogallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                 intentActivityResultLauncher.launch(intenttogallery);
             }
         }else {
             if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                 if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                     Snackbar.make(view, "Permission needed for gallery!", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             Permissionlauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                         }
                     }).show();
                 } else {
                     Permissionlauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                 }
             } else {
                 Intent intenttogallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                 intentActivityResultLauncher.launch(intenttogallery);
             }
         }

    }
    private void  registerLauncher(){

        intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result ) {
                if(result.getResultCode() == RESULT_OK){
                    Intent intentfromresult = result.getData();
                    if(intentfromresult!= null){
                        Uri imagedata = intentfromresult.getData();
                        try{
                            if(Build.VERSION.SDK_INT >=28){
                                ImageDecoder .Source source = ImageDecoder.createSource(ArtActivity.this.getContentResolver(),imagedata);
                                selectedimage= ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(selectedimage);
                            }else{
                                selectedimage= MediaStore.Images.Media.getBitmap(ArtActivity.this.getContentResolver(),imagedata);
                                binding.imageView.setImageBitmap(selectedimage);
                            }
                        }catch (Exception e ){
                                e.printStackTrace();
                        }
                    }
                }
            }
        });

        Permissionlauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean o) {
                if(o){
                    Intent intentogallery = new Intent (Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intentActivityResultLauncher.launch(intentogallery);
                }else{
                    Toast.makeText(ArtActivity.this,"Permission needed!",Toast.LENGTH_LONG);
                }
            }
        });
    }
}