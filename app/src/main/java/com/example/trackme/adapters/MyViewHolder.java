package com.example.trackme.adapters;

import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackme.MyClickListner;
import com.example.trackme.R;

public class MyViewHolder extends RecyclerView.ViewHolder {
   public TextView tvPhone;
    public TextView tvName;



    public MyViewHolder(View itemView, MyClickListner listner)  {
        super(itemView);
        tvName=itemView.findViewById(R.id.name);
        tvPhone=itemView.findViewById(R.id.number);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(listner!=null){
                   int pos=getAdapterPosition();
                   if(pos!=RecyclerView.NO_POSITION){
                       listner.MyClick(pos);
                   }
               }
            }
        });


    }
}
