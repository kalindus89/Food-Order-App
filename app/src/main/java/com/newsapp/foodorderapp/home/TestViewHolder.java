package com.newsapp.foodorderapp.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.newsapp.foodorderapp.R;


public class TestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView cateImage;
    private ImageView catName;
    private TestItemClickedListner testItemClickedListner;

    public TestViewHolder(@NonNull View itemView) {
        super(itemView);

        cateImage = itemView.findViewById(R.id.cateImage);
        catName = itemView.findViewById(R.id.catName);
    }

    public void setTestItemClickedListner(TestItemClickedListner testItemClickedListner){
        this.testItemClickedListner=testItemClickedListner;

    }

    @Override
    public void onClick(View view) {
        testItemClickedListner.onClick(view,getAbsoluteAdapterPosition(),false);
    }
}
