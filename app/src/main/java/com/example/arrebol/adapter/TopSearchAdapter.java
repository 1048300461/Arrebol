package com.example.arrebol.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arrebol.R;
import com.example.arrebol.entity.Top;

import java.util.ArrayList;

public class TopSearchAdapter extends RecyclerView.Adapter<TopSearchAdapter.TopViewHolder> {

    private ArrayList<Top> topArrayList;
    private Context context;
    private onItemClickListener onItemClickListener;

    public TopSearchAdapter(ArrayList<Top> tops, Context context){
        this.topArrayList = tops;
        this.context = context;
    }

    @NonNull
    @Override
    public TopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //实例化得到Item布局文件的View对象
        View v = View.inflate(context, R.layout.item_top, null);
        //返回TopViewHolder对象
        return new TopViewHolder(v);
    }

    public void setOnItemClickListener(TopSearchAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final TopViewHolder holder, int position) {
        holder.rank_tv.setBackgroundColor(context.getResources().getColor(R.color.topOtherColor));
        if(topArrayList.size() > 3){
            if(position == 0){
                holder.rank_tv.setBackgroundColor(context.getResources().getColor(R.color.top1Color));
            }else if(position == 1){
                holder.rank_tv.setBackgroundColor(context.getResources().getColor(R.color.top2Color));
            }else if(position == 2){
                holder.rank_tv.setBackgroundColor(context.getResources().getColor(R.color.top3Color));
            }
        }
        if(!topArrayList.get(position).isTopStatus()){
            holder.rank_tv.setVisibility(View.GONE);
        }else {
            holder.rank_tv.setVisibility(View.VISIBLE);
        }
        holder.rank_tv.setText(topArrayList.get(position).getRank());
        holder.name_tv.setText(topArrayList.get(position).getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(holder.name_tv.getText().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return topArrayList.size();
    }

    class TopViewHolder extends RecyclerView.ViewHolder{

        TextView rank_tv, name_tv;
        View itemView;

        public TopViewHolder(@NonNull View itemView) {
            super(itemView);

            rank_tv = itemView.findViewById(R.id.rank_tv);
            name_tv = itemView.findViewById(R.id.name_tv);
            this.itemView = itemView;
        }
    }

    public void setFilter(ArrayList<Top> tops){
        topArrayList = new ArrayList<>();

        for(int i = 0; i < tops.size(); i++){
            tops.get(i).setTopStatus(false);
        }

        topArrayList.addAll(tops);
        notifyDataSetChanged();
    }

    public void closeFilter(ArrayList<Top> tops){
        topArrayList = new ArrayList<>();

        for(int i = 0; i < tops.size(); i++){
            tops.get(i).setTopStatus(true);
        }

        topArrayList.addAll(tops);
        notifyDataSetChanged();
    }

    //子项目的点击事件
    public interface onItemClickListener{
        void onItemClick(String searchName);
    }
}
