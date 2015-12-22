package gooeyn.bored;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

public class HandlerActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_handler2);

        if (!isLoggedIn()) { //IF USER IS NOT LOGGED IN
            Intent i = new Intent(this, LoginActivity.class); //LOGIN ACTIVITY INTENT
            startActivity(i); //START LOGIN ACTIVITY
            finish(); //FINISHES MAIN ACTIVITY
        } else {
            new ConnectAndLoad().execute();
        }
    }
    public boolean isLoggedIn()
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken(); //GET THE CURRENT ACCESS TOKEN OF USER
        return accessToken != null; //IF THE ACCESS TOKEN IS NULL, RETURN FALSE. OTHERWISE RETURN TRUE.
    }

    public class ConnectAndLoad extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... arg0) {
            MyConnectionManager.getInstance().setConnectionConfiguration(getApplicationContext());
            MyConnectionManager.getInstance().connect();
            MyConnectionManager.getInstance().login();
            return true;
        }

        protected void onPostExecute(Boolean boo)
        {
            Intent i = new Intent(HandlerActivity2.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }
}
