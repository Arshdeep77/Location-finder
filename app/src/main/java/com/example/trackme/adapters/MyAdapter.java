package com.example.trackme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackme.MyClickListner;
import com.example.trackme.R;
import com.example.trackme.model.ContactUser;
import com.example.trackme.model.User;


import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private MyClickListner myClickListner;
    Context context;
    ArrayList<ContactUser> data;

    public MyAdapter(Context context, ArrayList<ContactUser> data) {
        this.context = context;
        this.data = data;
    }
    public void setMyItemClickListner(MyClickListner listner){
        myClickListner=listner;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.user_info,parent,false);
        MyViewHolder myView=new MyViewHolder(view,myClickListner);


        return myView;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ContactUser user=data.get(position);
        String name=user.getName();
        String phone= user.getPhNo();
        holder.tvPhone.setText(phone);
        holder.tvName.setText(name);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}


