package com.example.happyenglish;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {
    private List<String> mAnswerList;
    private List<String> mMyAnswerList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView_answer;
        TextView textView_my_answer;
        TextView textView_part;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_answer=itemView.findViewById(R.id.textView_answer);
            textView_my_answer=itemView.findViewById(R.id.textView_my_answer);
            textView_part=itemView.findViewById(R.id.textView_order);
        }
    }

    public ResultAdapter(List<String> answerList,List<String> myAnswerList){
        mAnswerList=answerList;
        mMyAnswerList=myAnswerList;
    }

    @NonNull
    @Override
    public ResultAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.result_item,viewGroup,false);
        ResultAdapter.ViewHolder holder=new ResultAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ResultAdapter.ViewHolder viewHolder, final int i) {

        String answer=mAnswerList.get(i);
        String myAnswer=mMyAnswerList.get(i);
        viewHolder.textView_answer.setText(Html.fromHtml(answer));
        viewHolder.textView_my_answer.setText(Html.fromHtml(myAnswer));
        viewHolder.textView_part.setText("Sentence "+(i+1));

    }

    @Override
    public int getItemCount() {
        return mMyAnswerList.size();
    }
}
