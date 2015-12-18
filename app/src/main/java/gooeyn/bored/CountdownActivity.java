package gooeyn.bored;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class CountdownActivity extends Activity {

    //public static final long DISCONNECT_TIMEOUT = 300000; // 5 min = 5 * 60 * 1000 ms
    public static final long DISCONNECT_TIMEOUT = 60000; // 5 min = 5 * 60 * 1000 ms
    private String TAG = "myshit";
    private static Handler disconnectHandler = new Handler(){
        public void handleMessage(Message msg) {
        }
    };

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            MyConnectionManager.getInstance().notBored();
        }
    };

    public void resetDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction(){
        resetDisconnectTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        resetDisconnectTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }
}