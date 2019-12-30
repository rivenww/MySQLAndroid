package com.wrw.graduation.mysqlmonitor;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.litepal.crud.DataSupport;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ConsoleActivity extends AppCompatActivity {

    public static final int UPDATE_TEXT = 1;//根据本地信息更新界面的标识
    public static final int Con = 2;//连接成功的标识
    public static final int Con_Fail = 3;//连接成功的标识
    public static final int UPDATE_SQL = 4;//更新SQL语句及其返回值

    public static final String INFO_IP = "info_ip";

    private Toolbar toolbar;

    private Connection conn;
    private Button onConn;
    private Button onSend;

    private String ip;
    private String port;
    private String name;
    private String usr;
    private String pw;
    private String dbname;
    private String URL;
    private String sendSQL;
    private String recSQL;
    private String consoleSQL;

    private TextView TextIP;
    private TextView TextPort;
    private TextView TextName;
    private TextView TextUsr;
    private TextView TextDbname;
    private TextView TextConsole;
    private EditText EditPw;
    private EditText EditSQL;



    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case UPDATE_TEXT:
                    TextIP.setText(ip);
                    TextPort.setText(port);
                    TextName.setText(name);
                    TextUsr.setText(usr);
                    TextDbname.setText(dbname);
                    break;
                case Con:
                    onSend.setEnabled(true);
                    onConn.setEnabled(false);
                    TextConsole.setText(consoleSQL);
                case Con_Fail:
                    TextConsole.setText(consoleSQL);
                case UPDATE_SQL:
                    EditSQL.setText("");
                    TextConsole.setText(consoleSQL);
                    int offset=TextConsole.getLineCount()*TextConsole.getLineHeight();
                    if(offset>TextConsole.getHeight()){
                        TextConsole.scrollTo(0,offset-TextConsole.getHeight());
                    }
                default:
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console);
        TextIP=(TextView)findViewById(R.id.IP);
        TextPort=(TextView)findViewById(R.id.Port);
        TextName=(TextView)findViewById(R.id.Name);
        TextUsr=(TextView)findViewById(R.id.Usr);
        TextDbname=(TextView)findViewById(R.id.Dbname);
        TextConsole=(TextView)findViewById(R.id.console);
        EditPw = (EditText)findViewById(R.id.editPw);
        EditSQL = (EditText)findViewById(R.id.editSQL);

        onConn = (Button) findViewById(R.id.connect);
        onSend = (Button) findViewById(R.id.send);

        consoleSQL = "";

        onSend.setEnabled(false);

        TextConsole.setMovementMethod(ScrollingMovementMethod.getInstance());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //设置是否显示主标题
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //设置标题
        getSupportActionBar().setTitle("控制台");
        //设置是否显示左侧的按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //给左侧的按钮
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里处理返回按钮的逻辑
                Back();
            }
        });

        //暂时停止服务
        Intent stopIntent = new Intent(this,UpdateService.class);
        stopService(stopIntent);

        Intent intent = getIntent();
        String infoIP = intent.getStringExtra(INFO_IP);



        List<Monitor> monitors = DataSupport.where("ip = ?",infoIP).find(Monitor.class);

        for (Monitor monitor: monitors) {
            ip = monitor.getIp();
            port = monitor.getPort();
            name = monitor.getName();
            usr = monitor.getUsr();
            dbname = monitor.getDbname();
            Log.d("ConsoleActivity",ip);
        }
        //ip = TextIP.getText().toString();
        //port = TextPort.getText().toString();
        //usr = TextUsr.getText().toString();
        pw = EditPw.getText().toString();
        //dbname = TextDbname.getText().toString();
        //URL = "jdbc:mysql://" + ip + ":" + port + "/" + dbname;
        //conn = Util.openConnection(URL, usr, pw);
        //Log.i("onConn", "onConn");
        setUpdateText();
    }//OnCreate 结束


    @Override
    //重写返回键事件
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK){
            Back();
        }
        return true;
    }



    private void Back () {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ConsoleActivity.this);
        dialog.setTitle("退出");
        dialog.setMessage("退出将中断控制台的连接");
        dialog.setCancelable(true);
        dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(ConsoleActivity.this, "退出控制台",Toast.LENGTH_SHORT).show();
                ConsoleActivity.this.finish();
            }
        });
        dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void onConn(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                pw = EditPw.getText().toString();
                URL = "jdbc:mysql://" + ip + ":" + port + "/" + dbname;
                conn = Util.openConnection(URL, usr, pw);
                Log.i("onConn", "onConn");
                if(conn != null){
                    consoleSQL ="连接成功\n";
                    Message message = new Message();
                    message.what = Con;
                    handler.sendMessage(message);
                }
                else{
                    consoleSQL ="连接失败\n";
                    Message message = new Message();
                    message.what = Con_Fail;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    public void onSend(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendSQL = EditSQL.getText().toString().toLowerCase();
                Log.d("ConsoleActivity",sendSQL);
                if(sendSQL.indexOf("select")!=-1){
                    System.out.println("查");
                    recSQL = Util.query(conn, sendSQL);
                    Log.i("onQuery", "onQuery");
                    consoleSQL = consoleSQL + sendSQL + "\n" + recSQL;
                    Message message = new Message();
                    message.what = UPDATE_SQL;
                    handler.sendMessage(message);
                    try {
                        conn.close();
                        Log.i("onDestroy", "onDestroy");
                    } catch (SQLException e) {
                        conn = null;
                    } finally {
                        conn = null;
                    }
                }else{
                    System.out.println("增删改");
                    boolean exec = Util.execSQL(conn, sendSQL);
                    Log.i("onInsert", "onInsert");
                    if(!exec){
                        consoleSQL = consoleSQL + sendSQL + "\n" + "操作失败，请检查语句\n";
                    }
                    else {
                        consoleSQL = consoleSQL + sendSQL + "\n" + "操作成功\n";
                    }

                    Message message = new Message();
                    message.what = UPDATE_SQL;
                    handler.sendMessage(message);

                    try {
                        conn.close();
                        Log.i("onDestroy", "onDestroy");
                    } catch (SQLException e) {
                        conn = null;
                    } finally {
                        conn = null;
                    }
                }
                conn = Util.openConnection(URL, usr, pw);
                Log.i("onConn", "onConn");
            }
        }).start();
    }

    /*public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connect:
                pw = EditPw.getText().toString();
                URL = "jdbc:mysql://" + ip + ":" + port + "/" + dbname;
                conn = Util.openConnection(URL, usr, pw);
                Log.i("onConn", "onConn");
                break;
            case R.id.send:
                //toSQL = EditSQL.getText().toString();
                break;
            default:
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conn != null) {
            try {
                conn.close();
                Log.i("onDestroy", "onDestroy");
            } catch (SQLException e) {
                conn = null;
            } finally {
                conn = null;
            }
        }
        Intent StartIntent = new Intent(this, UpdateService.class);
        startService(StartIntent);//启动监控服务
    }

    public void setUpdateText() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = UPDATE_TEXT;
                handler.sendMessage(message);

            }
        }).start();
    }

    //toolbar按钮
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.console_toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.edit:
                Toast.makeText(this,"修改监控信息",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ConsoleActivity.this,ChangeActivity.class);
                intent.putExtra(ChangeActivity.GET_IP,ip);
                startActivity(intent);
                break;
            case R.id.delete:
                AlertDialog.Builder dialog_cancel = new AlertDialog.Builder(ConsoleActivity.this);
                dialog_cancel.setTitle("删除");
                dialog_cancel.setMessage("删除当前监控信息，且不可恢复！\n当前连接会自行断开。");
                dialog_cancel.setCancelable(true);
                dialog_cancel.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ConsoleActivity.this, "返回主菜单",Toast.LENGTH_SHORT).show();
                        DataSupport.deleteAll(Monitor.class,"ip = ?",ip);
                        finish();
                    }
                });
                dialog_cancel.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ConsoleActivity.this, "返回填写",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                dialog_cancel.show();

                break;
            default:
        }
        return true;
    }

}
