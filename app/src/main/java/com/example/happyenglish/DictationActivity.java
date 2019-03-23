package com.example.happyenglish;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DictationActivity extends ActivityCollector {
    private Button buttonClear;
    private Button buttonNext;
    private Button buttonLast;
    private Button buttonRepeat;
    private Button buttonSubmit;
    private TextView textViewName;
    private TextView textViewProgress;
    private EditText editTextInputAnswer;

    private int currentPosition;

    private final int GET_POSITION=1;
    private final int CLOSE_Thread=2;
    private List<Integer> timesList=new ArrayList<>();
    private List<String> answersList=new ArrayList<>();
    private List<String> myAnswersList=new ArrayList<>();
    private List<String> answersListPopup=new ArrayList<>();
    private List<String> myAnswersListPopup=new ArrayList<>();
    private int count=0;
    private int countBack=0;
    private int total=0; //总的段数

    private boolean firstTime=true;
    private boolean exit=false;

    PopupWindow popupWindow;
    View popupView;
    TranslateAnimation animation;

    private Thread playThread=new Thread(){

        @Override
        public void run() {
            while(true && !exit){
                Message message=new Message();
                try{
                    Thread.sleep(1000);
                }catch(Exception e){
                    e.printStackTrace();
                }
                if(mediaPlayer.isPlaying()) {
                    currentPosition = mediaPlayer.getCurrentPosition();
                    message.obj = currentPosition;
                    message.what = GET_POSITION;
                    //将数据发送给Activity
                    handler.sendMessage(message);
                }
            }
        }
    };
    Records record=new Records();

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case GET_POSITION:
                    //判断是否到了暂停的时间
                    if(count+1<timesList.size()) {
                        if ((int) msg.obj >= timesList.get(count)) {
                            //暂停
                            Log.d("TestTime", "当前时间: " + msg.obj);
                            Log.d("TestTime", "下一个时间: " + timesList.get(count + 1));
                            mediaPlayer.pause();
                            count += 1;
                        }
                    }else{
                        if((int)msg.obj>=timesList.get(count)) {
                            mediaPlayer.pause();
                            mediaPlayer.seekTo(0);
                            exit = false;
                            countBack=count;
                            count=0;
                            Log.d("DictationActivity", "结束了！");
                            Toast.makeText(DictationActivity.this, "Over！Click Back To Save！", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                    default:
                        break;
            }
        }
    };

    private MediaPlayer mediaPlayer=new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buttonClear=findViewById(R.id.button_clear);
        buttonNext=findViewById(R.id.button_dictation_next);
        buttonLast=findViewById(R.id.button_dictation_pre);
        buttonRepeat=findViewById(R.id.button_dictation_repeat);
        buttonSubmit=findViewById(R.id.button_submit);
        textViewName=findViewById(R.id.textView_dictation_name);
        textViewProgress=findViewById(R.id.textVIew_dictation_progress);
        editTextInputAnswer=findViewById(R.id.editText_answer);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击这个按钮，显示结果
                checkResult();
            }
        });

        //接受对象
        Intent intent=getIntent();
        record=(Records) intent.getSerializableExtra("record");

        //获取当前的时间节点和进度
        for(String time:record.getTimes()){
            timesList.add(Integer.parseInt(time)*1000);
        }

        //设置界面
        String[] progress=record.getProgress().split("/");
        count=Integer.valueOf(progress[0]);
        total=Integer.valueOf(progress[1]);
        textViewName.setText(record.getName());
        textViewProgress.setText((count+1)+"/"+total);

        //答案
        for(String answer:record.getAnswers()){
            answersList.add(answer);
        }

        openRecord();

        //下一段的按钮
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextPart();
            }
        });

        //上一段
        buttonLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastPart();
            }
        });

        //重复刚才的一段
        buttonRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               repeatPart();
            }
        });

        //设置两个按钮的作用
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextInputAnswer.setText("");
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextInputAnswer.getText().toString().equals("")){
                    Toast.makeText(DictationActivity.this, "Please input your answer!", Toast.LENGTH_SHORT).show();
                }else{
                    if(!mediaPlayer.isPlaying()){
                        myAnswersList.add(editTextInputAnswer.getText().toString());
                        Toast.makeText(DictationActivity.this, "Submitted!", Toast.LENGTH_SHORT).show();
                        editTextInputAnswer.setText("");
                    }
                }
            }
        });

    }

    private void openRecord(){
        File recordToOpen= new File(Environment.getExternalStorageDirectory()+"/MyRecords"+"/"+record.getName()+".mp3");

        if(recordToOpen!=null){
            try{
                mediaPlayer.setDataSource(recordToOpen.getPath());
                mediaPlayer.prepare();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //将当前的进度保存至文件
        onSave();
        super.onBackPressed();


    }

    @Override
    protected void onDestroy() {
        //将当前的进度保存至文件
        onSave();
        super.onDestroy();
    }

    private void onSave(){
        //以back+1为准
        record.setProgress(String.valueOf((countBack+1)/total));
        //还有答案，在之前会设置,在这里计算一次准确率
        int correct=0;
        int totalWords=0;
        for(int i=0;i<myAnswersList.size();i++){
            String[] myAnswers=myAnswersList.get(i).split(" ");
            totalWords=totalWords+myAnswers.length;
            String[] answers=answersList.get(i).split(" ");
            for(int j=0;j<(myAnswers.length >answers.length ? answers.length:myAnswers.length);j++){
                if(myAnswers[j].equals(answers[j])){
                    correct=correct+1;
                }
            }
        }
        record.setMyAnswers(myAnswersList);
        record.setAccuracy(correct+"/"+totalWords);
        record.setProgress(String.valueOf(countBack+1)+"/"+total);
        record.setOnProgress(true);
        Gson gson=new Gson();
        String newData=gson.toJson(record);
        File outPut= new File(Environment.getExternalStorageDirectory()+"/MyRecords"+"/",record.getName()+".json");
        try{
            if(outPut.exists()){
                outPut.delete();
            }
            outPut.createNewFile();
            FileOutputStream outputStream=new FileOutputStream(outPut);
            outputStream.write(newData.getBytes());
            outputStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void nextPart(){
        if(firstTime){
            if(count!=0) {
                mediaPlayer.seekTo(timesList.get(count - 1));
            }
            firstTime=false;
            playThread.start();
        }

        if(!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            //设置进度
            textViewProgress.setText(count+1+"/"+total);
        }
    }

    private void repeatPart(){
        if(countBack!=0){
            mediaPlayer.seekTo(timesList.get(countBack-1));
            count=countBack;
            mediaPlayer.start();
            textViewProgress.setText(count+1+"/"+total);
        }else{
            if(!mediaPlayer.isPlaying()) {
                if(count-2>=0) {
                    mediaPlayer.seekTo(timesList.get(count - 2));
                    count=count-1;
                }else{
                    mediaPlayer.seekTo(0);
                    count=0;
                }

                mediaPlayer.start();
                //设置进度
                textViewProgress.setText(count+1+"/"+total);
            }
        }
    }

    private void lastPart(){
        //判断是不是末尾
        if(countBack!=0){
            //如果真，说明已经是播放完了的情况下重复上一段或者是重复前面那一段
            mediaPlayer.seekTo(timesList.get(countBack-2));
            count=countBack-1;
            mediaPlayer.start();
            textViewProgress.setText(count+1+"/"+total);
        }else{
            if(!mediaPlayer.isPlaying()) {
                if(count-3>=0) {
                    mediaPlayer.seekTo(timesList.get(count - 3));
                    count=count-2;
                }else{
                    mediaPlayer.seekTo(0);
                    count=0;
                }

                mediaPlayer.start();
                //设置进度
                textViewProgress.setText(count+1+"/"+total);
            }
        }
    }

    private void checkResult(){
        answersListPopup=answersList;
        myAnswersListPopup=myAnswersList;
        int totalWords=0;
        int correct=0;
        for(int i=0;i<myAnswersListPopup.size();i++){
            StringBuilder builder=new StringBuilder();
            String[] myAnswers=myAnswersListPopup.get(i).split(" ");
            totalWords=totalWords+myAnswers.length;
            String[] answers=answersListPopup.get(i).split(" ");

            for(int j=0;j<(myAnswers.length >answers.length ? answers.length:myAnswers.length);j++){
                if(myAnswers[j].equals(answers[j])){
                    correct=correct+1;
                    builder.append(myAnswers[j]);
                }else{
                    myAnswers[j]="<font color='#FF0000'>"+myAnswers[j]+"</font>";
                    builder.append(myAnswers[j]);
                }
                builder.append(" ");
            }
            myAnswersListPopup.set(i,builder.toString());
        }
        resultPopup();
    }


    //显示结果的popup窗口
    private void resultPopup() {


        popupView = View.inflate(this, R.layout.popup_result,null);
        RecyclerView recyclerView=popupView.findViewById(R.id.recyclerVIew_result);
        LinearLayoutManager manager=new LinearLayoutManager(DictationActivity.this);
        recyclerView.setLayoutManager(manager);
        ResultAdapter adapter=new ResultAdapter(answersListPopup,myAnswersListPopup);
        recyclerView.setAdapter(adapter);

        Button button=popupView.findViewById(R.id.button_result_OK);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                popupView=null;
                onBackPressed();
            }
        });


        // 参数2,3：指明popupwindow的宽度和高度
        popupWindow = new PopupWindow(popupView, 800, 1000);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.4f;
        getWindow().setAttributes(lp);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });

        // 设置背景图片， 必须设置，不然动画没作用
        //popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);

        // 设置点击popupwindow外屏幕其它地方消失
        popupWindow.setOutsideTouchable(true);

        // 平移动画相对于手机屏幕的底部开始，X轴不变，Y轴从1变0
        animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_PARENT, 0);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setDuration(200);


        // 设置popupWindow的显示位置，此处是在手机屏幕底部且水平居中的位置
        popupWindow.showAtLocation(findViewById(R.id.editText_answer), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
        popupView.startAnimation(animation);
    }


}
