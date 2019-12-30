package com.wrw.graduation.mysqlmonitor;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private Connection conn;

    private String ip;
    private String port;
    private String usr;
    private String pw;
    private String dbname;
    private String URL;

    private DrawerLayout mDrawerLayout;

    private  List<Dbinfo> infoList = new ArrayList<>();

    private InfoAdapter adapter;

    private String info_ip;
    private String info_status;
    private int info_count;

    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LitePal.getDatabase();//调用数据库
        Intent StartIntent = new Intent(this, UpdateService.class);
        startService(StartIntent);//启动监控服务
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);//左侧导航栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        navView.setCheckedItem(R.id.nav_main);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        //卡片布局
        initInfo();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new InfoAdapter(infoList);
        recyclerView.setAdapter(adapter);

        //下拉刷新
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshInfo();
            }
        });


    }//OnCreate



    private void initInfo(){//获取监控数据
        infoList.clear();
        Intent stopIntent = new Intent(this,UpdateService.class);
        stopService(stopIntent);
        Intent StartIntent = new Intent(this, UpdateService.class);
        startService(StartIntent);//启动监控服务

        List<Monitor> monitors = DataSupport.findAll(Monitor.class);
        info_count = 0;
        for (Monitor monitor: monitors) {
            info_ip = monitor.getIp();
            info_status = monitor.getDbstatus();
            Dbinfo info = new Dbinfo(info_ip,info_status);
            infoList.add(info);

        }

    }

    /*private void updateInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                List<Monitor> monitors = DataSupport.findAll(Monitor.class);
                for (Monitor monitor: monitors) {
                    ip = monitor.getIp();
                    port = monitor.getPort();
                    usr = monitor.getUsr();
                    pw = monitor.getPw();
                    dbname = monitor.getDbname();
                    URL = "jdbc:mysql://" + ip + ":" + port + "/" + dbname;
                    conn = Util.openConnection(URL, usr, pw);
                    Log.i("onConn", "onConn");
                    if (conn != null) {
                        try {
                            monitor.setDbstatus("on");
                            monitor.setSvstatus("on");
                            monitor.save();
                            conn.close();
                        } catch (SQLException e) {
                            conn = null;
                        } finally {
                            conn = null;
                        }
                    }
                    else {
                        monitor.setDbstatus("off");
                        monitor.setSvstatus("off");
                    }
                }

            }
        }).start();
    }*/


    private  void refreshInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(1000);

                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initInfo();
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.add:
                Toast.makeText(this, "新建数据库信息", Toast.LENGTH_SHORT).show();
                Intent intent_add = new Intent(ListActivity.this, AddActivity.class);
                startActivity(intent_add);
                /*Monitor mon = new Monitor();
                mon.setIp("39.96.166.162");
                mon.setPort("3306");
                mon.setName("android");
                mon.setPw("123456");
                mon.setUsr("android");
                mon.setDbname("test");
                mon.setSvstatus("on");
                mon.setDbstatus("off");
                mon.save();*/
                List<Monitor> monitors = DataSupport.findAll(Monitor.class);
                /*for (Monitor monitor: monitors) {
                    Log.d("ListActivity",monitor.getIp());
                    Log.d("ListActivity",monitor.getPort());
                    Log.d("ListActivity",monitor.getName());
                    Log.d("ListActivity",monitor.getSvstatus());
                    Log.d("ListActivity",monitor.getDbstatus());
                }*/
                break;
            case R.id.test:
                Toast.makeText(this, "测试页面", Toast.LENGTH_SHORT).show();
                Intent intent_test = new Intent(ListActivity.this, ConsoleActivity.class);
                startActivity(intent_test);
                break;
            default:
        }
        return true;
    }
}
