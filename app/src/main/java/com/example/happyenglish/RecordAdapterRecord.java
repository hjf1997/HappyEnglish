package com.example.happyenglish;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RecordAdapterRecord extends RecyclerView.Adapter<RecordAdapterRecord.ViewHolder> {
    private List<Records> mRecordsList;
    private RecordAdapter.OnItemClickListener mOnItemClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView records_name;
        TextView records_accuracy;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            imageView=itemView.findViewById(R.id.imageView_chapter);
            records_name=itemView.findViewById(R.id.textView_chapter_name);
            records_accuracy=itemView.findViewById(R.id.textView_chapter_accuracy);
        }
    }

    public RecordAdapterRecord(List<Records> recordsList){
        mRecordsList=recordsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.records_item,viewGroup,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Records records=mRecordsList.get(i);
        viewHolder.records_name.setText(records.getName());
        viewHolder.records_accuracy.setText("Accuracy: "+records.getAccuracy());
    }

    @Override
    public int getItemCount() {
        return mRecordsList.size();
    }

    public void setOnItemClickListener(RecordAdapter.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
