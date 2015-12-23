package gooeyn.bored;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

/*
ACTIVITY CREATES A COUNTDOWN. RUNNABLE EXECUTES WHEN IT REACHES THE TIMEOUT WITHOUT USER INTERACTION
 */
public class CountdownActivity extends Activity {

    public static final long DISCONNECT_TIMEOUT = 3600000; // 60 min = 60 * 60 * 1000 ms
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