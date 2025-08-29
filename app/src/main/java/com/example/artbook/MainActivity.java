package com.example.artbook;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.artbook.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding ;
    ArrayList<Art> list ;
    Artadapter artadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        list= new ArrayList<>();

        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        artadapter= new Artadapter(list);
          binding.recyclerview.setAdapter(artadapter);
        getdata();


    }

    public void getdata (){
        try{
            SQLiteDatabase db = this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
            Cursor cursor= db.rawQuery("SELECT * FROM arts" ,null);
            int nameindex = cursor.getColumnIndex("name");
            int idindex = cursor.getColumnIndex("id");

            while(cursor.moveToNext()){
                String name= cursor.getString(nameindex);
                int id= cursor.getInt(idindex);
                Art art = new Art(name,id);
                list.add(art);
            }
            artadapter.notifyDataSetChanged();
            cursor.close();

        }catch (Exception e ){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.artmenu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()== R.id.addart){
            Intent intent = new Intent(this,ArtActivity.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}