package com.wrw.graduation.mysqlmonitor;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.litepal.crud.DataSupport;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UpdateService extends Service {
    private Connection conn;

    private String ip;
    private String port;
    private String usr;
    private String pw;
    private String dbname;
    private String URL;

    public UpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyService", "onCreate executed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MyService", "onStartCommand executed");
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
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int wait = 5*60*1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + wait;
        Intent i = new Intent(this,UpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MyService", "onDestroy executed");
    }
}
