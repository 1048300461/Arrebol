package com.example.arrebol.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arrebol.R;
import com.example.arrebol.entity.Chapter;
import com.example.arrebol.entity.Section;

import java.util.ArrayList;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.SectionViewHolder> {

    private ArrayList<Section> sectionArrayList;
    private Context context;

    private OnItemClickListener onItemClickListener;

    public SectionAdapter(Context context, ArrayList<Section> sectionArrayList){
        this.context = context;
        this.sectionArrayList = sectionArrayList;
    }
    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //实例化得到Item布局文件的View对象
        View v = View.inflate(context, R.layout.item_chapter, null);
        //返回TopViewHolder对象
        return new SectionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final SectionViewHolder holder, final int position) {
        holder.chapter_name_tv.setText(sectionArrayList.get(position).getCurrentSection());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(sectionArrayList.get(position));
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return sectionArrayList.size();
    }


    class SectionViewHolder extends RecyclerView.ViewHolder{

        private TextView chapter_name_tv;
        private View itemView;

        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);

            this.chapter_name_tv = itemView.findViewById(R.id.chapter_name_tv);
            this.itemView = itemView;

        }
    }

    public interface OnItemClickListener{
        void onItemClick(Section section);
    }
}
