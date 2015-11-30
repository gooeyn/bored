package gooeyn.bored;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

public class ButtonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_button);
        if (!isLoggedIn()) { //IF USER IS NOT LOGGED IN
            Intent i = new Intent(this, LoginActivity.class); //LOGIN ACTIVITY INTENT
            startActivity(i); //START LOGIN ACTIVITY
            finish(); //FINISHES MAIN ACTIVITY
        }
        MyConnectionManager.getInstance().connect(getApplicationContext());
        final Button btn = (Button) findViewById(R.id.redbutton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn.setEnabled(false);
                Intent i = new Intent(ButtonActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    //METHOD TO CHECK IF USER IS LOGGED IN
    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken(); //GET THE CURRENT ACCESS TOKEN OF USER
        return accessToken != null; //IF THE ACCESS TOKEN IS NULL, RETURN FALSE. OTHERWISE RETURN TRUE.
    }
}
