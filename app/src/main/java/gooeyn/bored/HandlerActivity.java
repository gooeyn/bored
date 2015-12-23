package gooeyn.bored;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

/*
INITIALIZE FACEBOOK SDK
CHECK IF USER IS LOGGED IN
HANDLES THE CONNECTIVITY WITH XMPP SERVER
 */
public class HandlerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext()); //INITIALIZE FACEBOOK SDK
        setContentView(R.layout.activity_handler);

        if (!isLoggedIn()) { //IF USER IS NOT LOGGED IN
            Intent i = new Intent(this, LoginActivity.class); //LOGIN ACTIVITY INTENT
            startActivity(i); //START LOGIN ACTIVITY
            finish(); //FINISHES MAIN ACTIVITY
        } else { //IF USER IS LOGGED IN, CONNECT TO XMPP SERVER
            new ConnectAndLoad().execute();
        }
    }

    //CHECKS IF USER IS LOGGED IN USING FACEBOOK
    public boolean isLoggedIn()
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken(); //GET THE CURRENT ACCESS TOKEN OF USER
        return accessToken != null; //IF THE ACCESS TOKEN IS NULL, RETURN FALSE. OTHERWISE RETURN TRUE.
    }

    /*
    ASYNC TASK: CONNECT TO XMPP SERVER
     */
    public class ConnectAndLoad extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... arg0) {
            MyConnectionManager.getInstance().setConnectionConfiguration(getApplicationContext());
            MyConnectionManager.getInstance().connect();
            MyConnectionManager.getInstance().login();
            return true;
        }

        protected void onPostExecute(Boolean boo) //AFTER CONNECTION, START MAIN ACTIVITY
        {
            Intent i = new Intent(HandlerActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }
}
