package com.example.happyenglish;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends ActivityCollector {

    private LinearLayout linearLayout_home;
    private LinearLayout linearLayout_record;

    private RecyclerView recyclerView_learning_records;
    private RecyclerView recyclerView_records_records;

    //文件列表
    File[] files;
    List<Records> recordsList=new ArrayList<>();
    List<Records> recordsListRecords=new ArrayList<>();

    PopupWindow popupWindow;
    View popupView;
    TranslateAnimation animation;

    Records records=new Records();



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    linearLayout_record.setVisibility(View.GONE);
                    linearLayout_home.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_dashboard:
                    linearLayout_home.setVisibility(View.GONE);
                    linearLayout_record.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout_home=findViewById(R.id.linearLayout_home);
        linearLayout_record=findViewById(R.id.linearLayout_record);

        recyclerView_learning_records=findViewById(R.id.recyclerVIew_learning_list);
        recyclerView_records_records=findViewById(R.id.recyclerVIew_record_list);


        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        android.support.v7.widget.Toolbar toolbar=findViewById(R.id.toolBar_main);
        setSupportActionBar(toolbar);

        //加载文件,文件使用JSON格式,运行时权限处理，通过JSON格式文件去获取音频，获取音频的动作在列表的点击事件中完成
        //判断权限
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else{
            //把目录下所有.json文件读取出来
            readJson();
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        LinearLayoutManager manager=new LinearLayoutManager(MainActivity.this);
        recyclerView_learning_records.setLayoutManager(manager);
        RecordAdapter adapter=new RecordAdapter(recordsList);
        recyclerView_learning_records.setAdapter(adapter);
        adapter.setOnItemClickListener(new RecordAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //打开听力活动，加载对应名字的Mp3和节点
                records=recordsList.get(position);
                detailPopup();
            }
        });

        recordsListRecords.clear();
        for(Records records:recordsList){
            if(records.isOnProgress()){
                recordsListRecords.add(records);
            }
        }

        LinearLayoutManager layoutManager=new LinearLayoutManager(MainActivity.this);
        recyclerView_records_records.setLayoutManager(layoutManager);
        RecordAdapterRecord recordAdapterRecord=new RecordAdapterRecord(recordsListRecords);
        recyclerView_records_records.setAdapter(recordAdapterRecord);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED &&grantResults[1]==PackageManager.PERMISSION_GRANTED){
                    //读取json
                }else{
                    Toast.makeText(this, "Permission denied！", Toast.LENGTH_SHORT).show();
                    ActivityCollector.finishAll();
                }
        }
    }

    private void readJson(){
        File dir= new File(Environment.getExternalStorageDirectory()+"/MyRecords");
        files=dir.listFiles();
        if(files!=null){
            for(File file:files){
                if(file.getName().endsWith(".json")){
                    try{
                        InputStream inputStream=new FileInputStream(file);
                        if(inputStream!=null){
                            InputStreamReader reader=new InputStreamReader(inputStream);
                            BufferedReader bufferedReader=new BufferedReader(reader);
                            String line="",content=new String();
                            while((line=bufferedReader.readLine())!=null){
                                content+=line+"\n";
                            }
                            inputStream.close();
                            Log.d("MainActivity", "readJson: "+content);
                            //这里读取出了文件
                            Gson gson=new Gson();
                            Records records=gson.fromJson(content,Records.class);
                            recordsList.add(records);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                }
            }
        }
    }


    //显示结果的popup窗口
    private void detailPopup() {


        popupView = View.inflate(this, R.layout.popup_detail_show,null);
        TextView textViewName=popupView.findViewById(R.id.textView_chapter_name_popup);
        TextView textViewProgress=popupView.findViewById(R.id.textView_chapter_progress_popup);
        TextView textViewDifficulty=popupView.findViewById(R.id.textView_chapter_difficulty_popup);
        textViewName.setText(records.getName());
        textViewProgress.setText(records.getProgress());
        textViewDifficulty.setText(records.getDifficulty());
        Button button=popupView.findViewById(R.id.button_start_chapter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,DictationActivity.class);
                intent.putExtra("record",records);
                startActivity(intent);
                popupWindow.dismiss();
                popupView=null;
            }
        });

        // 参数2,3：指明popupwindow的宽度和高度
        popupWindow = new PopupWindow(popupView, 600, 600);
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
        popupWindow.showAtLocation(findViewById(R.id.recyclerVIew_learning_list), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
        popupView.startAnimation(animation);
    }



}
