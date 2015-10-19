/*
* Author- Shahriar Nirjon, nirjon@cs.unc.edu
* Comments (10/19/2015)
*   - Works with Nexus 5 (Marshmallow). Target SDK = 22 (i.e. old style PERMISSIONS)
*   - Bean was loaded with AccelerationReader (but it should not matter what code is there)
*   - Used a very bad way to display accelerations on screen. GUI is not updated in real-time.
*   - Used an odd way to determine which Bean is sending data (if multiple beans are connected)
*   - Scans for Beans and connects to all of them. There should have been a list/check boxes etc.
* */
package com.example.shahriar.beanocularapp;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanDiscoveryListener;
import com.punchthrough.bean.sdk.BeanListener;
import com.punchthrough.bean.sdk.BeanManager;
import com.punchthrough.bean.sdk.message.Acceleration;
import com.punchthrough.bean.sdk.message.BeanError;
import com.punchthrough.bean.sdk.message.Callback;
import com.punchthrough.bean.sdk.message.ScratchBank;

import java.util.ArrayList;
import java.util.List;


public class BeanocularActivity extends AppCompatActivity {

    final List<Bean> beans = new ArrayList<>();
    Activity mActivity = this;
    TextView textView = null;
    List<String> bleAddressList = new ArrayList<>();
    int totalThreads = 0;

    class MyThread implements Runnable {

        TextView tv;
        int num;
        Bean b;
        Thread th;

        MyThread(TextView tv1, int num1, Bean b1) {
            tv = tv1;
            num = num1;
            b = b1;
            th = new Thread(this);
            th.setPriority(Thread.MAX_PRIORITY);
            th.start();
        }

        @Override
        public void run() {
            for(int i = 0; i < 60; i++) {
                b.readAcceleration(new Callback<Acceleration>() {
                    @Override
                    public void onResult(Acceleration result) {
                        tv.append("\nBean #" + num + ": " + result.x() + ", " + result.y() + ", " + result.z());
                        tv.invalidate();
                    }
                });
                try {
                    th.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    BeanDiscoveryListener listener = new BeanDiscoveryListener() {
        @Override
        public void onBeanDiscovered(Bean bean, int rssi) {
            beans.add(bean);
            bean.connect(mActivity, beanListener);
        }

        @Override
        public void onDiscoveryComplete() {
            System.out.println("Total beans discovered: " + beans.size());
            for (Bean bean : beans) {
                System.out.println(bean.getDevice().getName());   // "Bean"              (example)
                System.out.println(bean.getDevice().getAddress());    // "B4:99:4C:1E:BC:75" (example)
            }
        }
    };

    BeanListener beanListener = new BeanListener() {
        @Override
        public void onConnected() {
            textView.append("\nA new bean has been connected.");
            System.out.println("A bean has connected.");
            System.out.println("Total bean: " + beans.size());
            System.out.println("Total threads started so far: " + totalThreads);

            for(int i = 0; i < beans.size(); i++){
                Bean b = beans.get(i);
                String baddr = b.getDevice().getAddress();
                if(b.isConnected()){
                    if(bleAddressList.contains(baddr) == false){
                        bleAddressList.add(baddr);
                        ++totalThreads;
                        new MyThread(textView, totalThreads, b);
                        System.out.println("Total threads started now: " + totalThreads);
                        return;
                    }
                }
            }
        }

        @Override
        public void onError(BeanError berr){
            System.out.println("Bean has errors..");
        }

        @Override
        public void onConnectionFailed(){
            System.out.println("Bean connection failed");
        }

        @Override
        public void onDisconnected(){
            System.out.println("Bean disconnected");
        }

        @Override
        public void onScratchValueChanged(ScratchBank bank, byte[] value){
            System.out.println("Bean scratch value changed");
        }

        @Override
        public void onSerialMessageReceived(byte[] data){
            System.out.println("data received: " + data.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beanocular);
        textView = (TextView) findViewById(R.id.text1);
        textView.setMovementMethod(new ScrollingMovementMethod());
        BeanManager.getInstance().startDiscovery(listener);
    }

}
