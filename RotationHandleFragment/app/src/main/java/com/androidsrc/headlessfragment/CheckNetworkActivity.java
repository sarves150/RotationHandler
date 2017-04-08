package com.androidsrc.headlessfragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by vikalp on 1/30/2017.
 */



public class CheckNetworkActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this); // register EventBus
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this); // unregister EventBus
    }

    // method that will be called when someone posts an event NetworkStateChanged
    public void onEventMainThread(NetworkStateChanged event) {
        if (!event.isInternetConnected()) {
            Toast.makeText(this, "No Internet connection!", Toast.LENGTH_SHORT).show();
        }
    }



    // event
    public class NetworkStateChanged {

        private boolean mIsInternetConnected;

        public NetworkStateChanged(boolean isInternetConnected) {
            this.mIsInternetConnected = isInternetConnected;
        }

        public boolean isInternetConnected() {
            return this.mIsInternetConnected;
        }
    }



    public class NetworkStateReceiver extends BroadcastReceiver {

        // post event if there is no Internet connection
        public void onReceive(Context context, Intent intent) {
//            super.onReceive(context, intent);
            if (intent.getExtras() != null) {
                NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                    // there is Internet connection
                } else if (intent
                        .getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                    // no Internet connection, send network state changed
                    EventBus.getDefault().post(new NetworkStateChanged(false));
                }
            }
        }


    }


}