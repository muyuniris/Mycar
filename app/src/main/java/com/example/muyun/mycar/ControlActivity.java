package com.example.muyun.mycar;

import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class ControlActivity extends AppCompatActivity {

    private RockerView rockerView1;
    RockerView rockerViewyuntai;
    private Socket socket=null;
    boolean isconnect=false;
    OutputStream writer;

    static PrintWriter mPrintWriterClient = null;
    static BufferedReader mBufferedReaderClient = null;
    Switch openswitch;
    Switch lightswitch;
    TextView tv1;
    public WifiManager wifiManager;
    public ConnectivityManager connectManager;
    public NetworkInfo netInfo;
    public WifiInfo wifiInfo;
    public DhcpInfo dhcpInfo;

    List<ScanResult> list;

    int screenWidth;
    int screenHeight;
    Handler handler;
    byte start=(byte)0xcc;
    byte end=(byte)0xff;
    byte speed=(byte)0x00;
    byte dir=(byte)0x00;
    byte che=(byte)0x200;
    byte duoji=(byte)0x00;
    byte light=(byte)0x00;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        handler=new MyHandle(this);
        WebView webView=(WebView)findViewById(R.id.webView);
        webView.loadUrl("http://192.168.8.1:8080/?action=stream");
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        Toolbar mToolbar =  (Toolbar)findViewById(R.id.control_toolbar);
        //设置Toolbar
        setSupportActionBar(mToolbar);
        //显示NavigationIcon,这个方法是ActionBar的方法.Toolbar没有这个方法.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //设置icon
        mToolbar.setNavigationIcon(R.drawable.iconreturn);
        //设置监听.必须在setSupportActionBar()之后调
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(socket!=null){
                        socket.close();
                        socket=null;}
                    else{Toast.makeText(ControlActivity.this,"断开连接失败",Toast.LENGTH_SHORT).show();}
                    if(writer!=null){
                        writer.close();
                        writer=null;}
                    else{Toast.makeText(ControlActivity.this,"断开连接失败",Toast.LENGTH_SHORT).show();}
                    Toast.makeText(ControlActivity.this,"断开连接",Toast.LENGTH_SHORT).show();
                    isconnect=false;
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(ControlActivity.this,"断开连接失败",Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        rockerView1 = (RockerView) findViewById(R.id.rockerView1);
        //rockerView2 = (RockerView) findViewById(R.id.rockerView2);

        rockerView1.setRockerChangeListener(new RockerView.RockerChangeListener() {

            @Override
            public void report(double jiaodu) {
                if(jiaodu>0){
                    dir=(byte)0x00;
                    che=(byte)jiaodu;
                }
                else if(jiaodu<0){
                    dir=(byte)0x01;
                    jiaodu=-jiaodu;
                    che=(byte)jiaodu;
                }
                sendmessage();
               
            }
        });
        rockerViewyuntai = (RockerView) findViewById(R.id.yuntaiView);
        //rockerView2 = (RockerView) findViewById(R.id.rockerView2);

        rockerViewyuntai.setRockerChangeListener(new RockerView.RockerChangeListener() {

            @Override
            public void report(double jiaodu) {
                if(jiaodu==0){
                    duoji=(byte)0x00;
                }
                if(jiaodu<0){
                    jiaodu=-jiaodu;
                    duoji=(byte)jiaodu;
                }
                sendmessage();

            }
        });
        RadioGroup radioGroup=findViewById(R.id.r);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.r1:
                        speed=(byte)0x00;
                        sendmessage();
                        break;
                    case R.id.r2:
                        speed=(byte)0x01;
                        sendmessage();
                        break;
                }
            }
        });
        openswitch=findViewById(R.id.openswitch);
        openswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                   // isconnect=true;
                    new Thread(){
                        public void run(){
                            try{
                                socket=new Socket("192.168.8.1",2002);
                                Log.e("Connect","建立连接："+socket);
                                handler.sendEmptyMessage(1);
                                isconnect=true;
                                 writer=socket.getOutputStream();
                               // mPrintWriterClient=new PrintWriter(socket.getOutputStream());
                            } catch (UnknownHostException e) {
                                handler.sendEmptyMessage(0);
                                e.printStackTrace();
                            } catch (IOException e) {
                                handler.sendEmptyMessage(0);
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
                else{

                    try {
                        if(socket!=null){
                        socket.close();
                        socket=null;}
                        else{Toast.makeText(ControlActivity.this,"断开连接失败",Toast.LENGTH_SHORT).show();}
                        if(mBufferedReaderClient!=null){
                        mBufferedReaderClient.close();
                        mBufferedReaderClient=null;}
                        else{Toast.makeText(ControlActivity.this,"断开连接失败",Toast.LENGTH_SHORT).show();}
                        Toast.makeText(ControlActivity.this,"断开连接",Toast.LENGTH_SHORT).show();
                        isconnect=false;
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(ControlActivity.this,"断开连接失败",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        lightswitch=findViewById(R.id.lightswitch);
        lightswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    light=(byte)0x01;
                    sendmessage();
                }
                else{
                    light=(byte)0x00;
                    sendmessage();
                }
            }
        });
        new Thread(){
            @Override
            public void run() {
                while(true){
                    //  SystemClock.sleep(1000);

                    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                    connectManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                    netInfo = connectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    dhcpInfo = wifiManager.getDhcpInfo();
                    wifiInfo = wifiManager.getConnectionInfo();
                    list = (List<ScanResult>) wifiManager.getScanResults();

                    wifiInfo.getSSID();
                    tv1 = (TextView)findViewById(R.id.tv1);
                    final String wifiProperty = "当前连接WIFI信息如下:" + "ip:" + FormatString(dhcpInfo.ipAddress) +
                            //"netgate:" + FormatString(dhcpInfo.gateway) + '\n' +
                           // "dns:" + FormatString(dhcpInfo.dns1) + '\n' +
                            "rssi:"+ wifiInfo.getRssi()+
                           "距离"+DisByRssi(wifiInfo.getRssi());



                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv1.setText(wifiProperty);
                        }
                    });

                    Log.i("he",wifiProperty);
                }

            }

            public String DisByRssi(int rssi){

                int iRssi = Math.abs(rssi);
                double power = (iRssi- 35)/(10*2.3);
                double number=Math.pow(10,power);
                java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
                return df.format(number);
            }

        }.start();



    }



    public String FormatString(int value){
        String strValue ="";
        byte[] ary = intToByteArray(value);
        for(int i = ary.length-1;i>0;i--){
            strValue += (ary[i]& 0xFF);
            if(i>0){
                strValue +=".";

            }
        }
        return strValue;
    }
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        // 由高位到低位
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }



    public void sendmessage(){

        final byte[] msg={start,speed,dir,che,duoji,light,end};
        if(isconnect){
            new Thread(){
                public void run(){
                    try {
                        writer.write(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(3);
                    }

                    Log.e("Connect","发送结束,发送内容为"+msg);
                }
            }.start();
        }
        else{
            Toast.makeText(ControlActivity.this,"请先建立连接",Toast.LENGTH_SHORT).show();
        }
    }
    static class MyHandle extends Handler{

        private WeakReference<ControlActivity> activityWeakReference;

        public MyHandle(ControlActivity activity){
            this.activityWeakReference=new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                Toast.makeText(activityWeakReference.get(),"连接成功",Toast.LENGTH_SHORT).show();
            }
            else if(msg.what==0){
                Toast.makeText(activityWeakReference.get(),"连接失败，请重新连接",Toast.LENGTH_SHORT).show();
            }
            else if(msg.what==3){
                Toast.makeText(activityWeakReference.get(),"发送消息失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

}
