package com.example.arrebol.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arrebol.R;
import com.example.arrebol.entity.Chapter;

import java.util.ArrayList;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {

    private ArrayList<Chapter> chapterArrayList;
    private Context context;

    private OnItemClickListener onItemClickListener;

    public ChapterAdapter(Context context, ArrayList<Chapter> chapterArrayList){
        this.context = context;
        this.chapterArrayList = chapterArrayList;
    }
    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //实例化得到Item布局文件的View对象
        View v = View.inflate(context, R.layout.item_chapter, null);
        //返回TopViewHolder对象
        return new ChapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChapterViewHolder holder, final int position) {
        holder.chapter_name_tv.setText(chapterArrayList.get(position).getCurrentChapter());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(chapterArrayList.get(position).getUrl());
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return chapterArrayList.size();
    }


    class ChapterViewHolder extends RecyclerView.ViewHolder{

        private TextView chapter_name_tv;
        private View itemView;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);

            this.chapter_name_tv = itemView.findViewById(R.id.chapter_name_tv);
            this.itemView = itemView;

        }
    }

    public interface OnItemClickListener{
        void onItemClick(String url);
    }
}
