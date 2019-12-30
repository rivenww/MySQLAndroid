package com.wrw.graduation.mysqlmonitor;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.util.List;

public class ChangeActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String GET_IP = "get_ip";

    private EditText edit_IP;
    private EditText edit_Port;
    private EditText edit_Name;
    private EditText edit_Usr;
    private EditText edit_Pw;
    private EditText edit_dbname;

    private Toolbar toolbar;

    private String get_ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //设置是否显示主标题
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //设置标题
        getSupportActionBar().setTitle("修改监控信息");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //给左侧的按钮
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //处理返回按钮的逻辑
                Back();
            }
        });

        edit_IP = (EditText) findViewById(R.id.editIP);
        edit_Port = (EditText) findViewById(R.id.editPort);
        edit_Name = (EditText) findViewById(R.id.editName);
        edit_Usr = (EditText) findViewById(R.id.editUsr);
        edit_Pw = (EditText) findViewById(R.id.editPw);
        edit_dbname = (EditText) findViewById(R.id.editDbname);
        Button OnSave = (Button) findViewById(R.id.save);
        Button OnCancel = (Button) findViewById(R.id.cancel);
        OnSave.setOnClickListener(this);
        OnCancel.setOnClickListener(this);

        Intent intent = getIntent();
        get_ip = intent.getStringExtra(GET_IP);

        List<Monitor> monitors = DataSupport.where("ip = ?",get_ip).find(Monitor.class);

        for (Monitor monitor: monitors) {
            edit_IP.setText(monitor.getIp());
            edit_Port.setText(monitor.getPort());
            edit_Name.setText(monitor.getName());
            edit_Usr.setText(monitor.getUsr());
            edit_dbname.setText(monitor.getDbname());
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save:
                AlertDialog.Builder dialog = new AlertDialog.Builder(ChangeActivity.this);
                dialog.setTitle("保存");
                dialog.setMessage("是否保存？");
                dialog.setCancelable(true);
                dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Monitor mon = new Monitor();
                        mon.setIp(edit_IP.getText().toString());
                        mon.setPort(edit_Port.getText().toString());
                        mon.setName(edit_Name.getText().toString());
                        mon.setPw(edit_Pw.getText().toString());
                        mon.setUsr(edit_Usr.getText().toString());
                        mon.setDbname(edit_dbname.getText().toString());
                        mon.setSvstatus("off");
                        mon.setDbstatus("off");
                        mon.updateAll("ip = ?",get_ip);
                        dialog.dismiss();
                        Toast.makeText(ChangeActivity.this, "已保存",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ChangeActivity.this, "取消保存",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                dialog.show();

                break;
            case R.id.cancel:
                Back();
                break;
            default:
                break;
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK){
            Back();
        }
        return true;
    }

    private void Back () {
        AlertDialog.Builder dialog_cancel = new AlertDialog.Builder(ChangeActivity.this);
        dialog_cancel.setTitle("返回主菜单");
        dialog_cancel.setMessage("您当前填写的内容将不会被保存！");
        dialog_cancel.setCancelable(true);
        dialog_cancel.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ChangeActivity.this, "返回主菜单",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        dialog_cancel.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ChangeActivity.this, "返回填写",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog_cancel.show();
    }
}
