package com.example.arrebol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.arrebol.R;
import com.example.arrebol.entity.Chapter;

import java.util.ArrayList;

public class CartoonContentAdapter extends RecyclerView.Adapter<CartoonContentAdapter.CartoonContentViewHolder> {

    private ArrayList<String> imgUrls;
    private Context context;

    private OnItemClickListener onItemClickListener;

    public CartoonContentAdapter(Context context, ArrayList<String> imgUrls){
        this.context = context;
        this.imgUrls = imgUrls;
    }
    @NonNull
    @Override
    public CartoonContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //实例化得到Item布局文件的View对象
        View v = LayoutInflater.from(context).inflate(R.layout.item_cartoon_content, parent, false);
        //返回TopViewHolder对象
        return new CartoonContentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartoonContentViewHolder holder, final int position) {
        Glide.with(context).load(imgUrls.get(position))
                .placeholder(context.getResources().getDrawable(R.drawable.loading))
                .into(holder.content_imv);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(position);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return imgUrls.size();
    }


    class CartoonContentViewHolder extends RecyclerView.ViewHolder{

        private ImageView content_imv;
        private View itemView;

        public CartoonContentViewHolder(@NonNull View itemView) {
            super(itemView);

            this.content_imv = itemView.findViewById(R.id.cartoon_content_imv);
            this.itemView = itemView;

        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
