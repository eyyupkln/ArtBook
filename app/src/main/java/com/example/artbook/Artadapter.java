package com.example.artbook;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artbook.databinding.RecyclerviewBinding;

import java.util.ArrayList;

public class Artadapter extends RecyclerView.Adapter<Artadapter.artholder> {
    ArrayList<Art> list;
    public Artadapter(ArrayList<Art> list){
        this.list= list;
    }

    @NonNull

    @Override
    public artholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerviewBinding recyclerviewBinding = RecyclerviewBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new artholder(recyclerviewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull artholder holder, int position) {
        holder.binding.recyclertest.setText(list.get(position).name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent (holder.itemView.getContext(),ArtActivity.class);
                intent.putExtra("info","old");
                intent.putExtra("artid",list.get(position).id);
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class artholder extends RecyclerView.ViewHolder{
        private RecyclerviewBinding binding;
        public artholder(RecyclerviewBinding binding) {
            super(binding.getRoot());
            this.binding= binding;
        }
    }
}
