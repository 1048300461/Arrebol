package com.example.arrebol.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arrebol.R;
import com.example.arrebol.entity.History;

import java.util.ArrayList;

public class HistorySearchAdapter extends RecyclerView.Adapter<HistorySearchAdapter.HistoryViewHolder> {

    private ArrayList<History> histories;
    private Context context;
    private static final int MAX_LENGTH = 8;

    //文字点击的回调事件
    private TextViewClickListener textViewClickListener;

    public HistorySearchAdapter(ArrayList<History> histories, Context context){
        this.histories = histories;
        this.context = context;
    }

    public void setTextViewClickListener(TextViewClickListener textViewClickListener) {
        this.textViewClickListener = textViewClickListener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //实例化得到Item布局文件的View对象
        View v = View.inflate(context, R.layout.item_history, null);
        //返回TopViewHolder对象
        return new HistoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final HistoryViewHolder holder, int position) {
        holder.name_tv.setText(histories.get(position).getName());
        //点击事件的处理
        holder.name_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewClickListener.onTvClickListener(holder.name_tv.getText().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return histories.size();
    }

    public void clearData(){
        histories.clear();
        notifyDataSetChanged();
    }

    public void addData(History history){
        for(History history1:histories){
            if(history1.getName().equals(history.getName())){
                int index = histories.indexOf(history1);

                if(index != 0){
                    histories.remove(history1);
                    histories.add(0, history1);
                }
                notifyDataSetChanged();
                return;
            }
        }
        if(histories.size() == 8){
            //删除最后一个元素
            histories.remove(histories.size() - 1);

            //添加至第一个元素
            histories.add(0, history);
        }else{
            histories.add(0,history);
        }
        notifyDataSetChanged();
    }

    public ArrayList<History> getHistories(){
        return histories;
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder{
        TextView name_tv;
        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            name_tv = itemView.findViewById(R.id.history_name);
        }
    }

    public interface TextViewClickListener {
        void onTvClickListener(String name);
    }
}
