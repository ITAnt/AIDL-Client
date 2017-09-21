package com.itant.aidl_client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.itant.aidl.Book;
import com.itant.aidl.IEasyLink;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //intent = new Intent();
        //intent.setComponent(new ComponentName("com.itant.aidl_server", "com.itant.aidl_server.EasyService"));
        findViewById(R.id.btn_bind).setOnClickListener(this);
    }


    private IEasyLink mEasyLink;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mEasyLink = IEasyLink.Stub.asInterface(iBinder);
            Book book = new Book();
            book.setName("Android AIDL");
            try {
                mEasyLink.anotherMethod("哈哈", book);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_bind:
                // 安卓5.0以下：
                //Intent intent = new Intent("com.ithouse.aidl");
                //bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

                // 安卓5.0以上：
                // 方法一（推荐）
                Intent mIntent = new Intent();
                mIntent.setAction("com.ithouse.aidl");//你定义的service的action
                mIntent.setPackage("com.itant.aidl_server");//这里你需要设置Server的包名
                bindService(mIntent, mConnection, Context.BIND_AUTO_CREATE);

                // 方法二
                //Intent mIntent = new Intent();
                //mIntent.setAction("com.ithouse.aidl");
                //Intent eIntent = new Intent(getExplicitIntent(this, mIntent));
                //bindService(eIntent, mConnection, Context.BIND_AUTO_CREATE);
                break;
        }
    }

    public static Intent getExplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }
        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);
        // Set the component to be explicit
        explicitIntent.setComponent(component);
        return explicitIntent;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mConnection != null) {
            unbindService(mConnection);
        }
    }
}
