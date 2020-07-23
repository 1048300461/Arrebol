package com.example.arrebol.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.arrebol.R;
import com.example.arrebol.entity.SearchResult;

import java.util.ArrayList;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ResultViewHolder> {

    private Context context;

    private ArrayList<SearchResult> searchResultList;

    public SearchResultAdapter(Context context, ArrayList<SearchResult> searchResults){
        this.context = context;
        this.searchResultList = searchResults;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //实例化得到Item布局文件的View对象
        View v = View.inflate(context, R.layout.item_search, null);
        //返回TopViewHolder对象
        return new ResultViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        holder.search_name.setText(searchResultList.get(position).getName());
        holder.search_author.setText(context.getString(R.string.author)
                + searchResultList.get(position).getAuthor());
        holder.search_tag.setText(context.getString(R.string.type)
                + searchResultList.get(position).getTag());
        if(!"null".equals(searchResultList.get(position).getIntroduce())){
            //介绍不为null标记时
            holder.search_introduce.setText(context.getString(R.string.introduce)
                    + searchResultList.get(position).getIntroduce());
            holder.search_introduce.setVisibility(View.VISIBLE);
        }else{
            holder.search_introduce.setVisibility(View.INVISIBLE);
        }


        Glide.with(context).load(searchResultList.get(position).getCover())
                            .placeholder(R.drawable.cover)
                            .into(holder.cover_iv);

    }

    @Override
    public int getItemCount() {
        return searchResultList.size();
    }

    class ResultViewHolder extends RecyclerView.ViewHolder{

        private TextView search_name, search_tag, search_author,
                    search_introduce;
        private ImageView cover_iv;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);

            search_name = itemView.findViewById(R.id.search_name_tv);
            search_tag = itemView.findViewById(R.id.search_tag_tv);
            search_author = itemView.findViewById(R.id.search_author_tv);
            search_introduce = itemView.findViewById(R.id.search_introduce_tv);

            cover_iv = itemView.findViewById(R.id.cover_iv);
        }
    }
}
