package gooeyn.bored;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    CallbackManager callbackManager; //call back manager for the login button
    HashMap<String, String> hashData = new HashMap<>(); //hash map containing the data from facebook to be added to mysql database
    LoginButton loginButton;
    String TAG = "myshit/LoginActivity";
    String server = "http://ec2-54-84-237-97.compute-1.amazonaws.com/insert.php";
    String host = "54.84.237.97";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext()); //initialize facebook sdk
        callbackManager = CallbackManager.Factory.create(); //create call back manager
        setContentView(R.layout.activity_login); //set content view

        loginButton = (LoginButton) findViewById(R.id.login_button);
        setLoginButtonStyle();
        setLoginCallback();
    }

    private void setLoginCallback()
    {
        loginButton.setReadPermissions("user_friends");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() { //register login button call back
            @Override
            public void onSuccess(LoginResult loginResult) { //if successfull login
                makeRequest();
            }

            @Override
            public void onCancel() { //on canceled login
                Log.e(TAG, "Cancel on Facebook Login");
                Toast.makeText(getApplicationContext(), "Hey, complete the login so you won't be bored anymore!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) { //on login error
                Log.e(TAG, "Error on Facebook Login: " + exception.toString());
                Toast.makeText(getApplicationContext(), "There was an error trying to login with Facebook, try again in a little bit.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void makeRequest()
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken(); // get current access token
        GraphRequest request = GraphRequest.newMeRequest( //make graph request for facebook data
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() { //callback from graph request
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) { //on completed request
                        try {
                            // INSERTING DATA FROM FACEBOOK INTO HASHTABLE TO BE INSERTED INTO MYSQL
                            hashData.put("name", object.getString("name"));
                            hashData.put("first_name", object.getString("first_name"));
                            hashData.put("last_name", object.getString("last_name"));
                            hashData.put("facebook_id", object.getString("id"));

                            if (object.getString("gender").equals("male")) {
                                hashData.put("gender", "m");
                            } else {
                                hashData.put("gender", "f");
                            }
                            hashData.put("friend_name", object.getJSONObject("friends").getJSONArray("data").getJSONObject(0).getString("name"));
                            hashData.put("friend_id", object.getJSONObject("friends").getJSONArray("data").getJSONObject(0).getString("id"));

                            insertToDatabase(); //insert all the data to the database
                        } catch (JSONException e) {
                            Log.e(TAG, e.toString());
                            Log.e(TAG, object.toString());
                        }

                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,first_name,last_name,gender,id, friends");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void setLoginButtonStyle() {
        float fbIconScale = 1.45F;
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), com.facebook.R.drawable.com_facebook_button_icon);
        drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth() * fbIconScale),
                (int) (drawable.getIntrinsicHeight() * fbIconScale));
        loginButton.setCompoundDrawables(drawable, null, null, null);
        loginButton.setCompoundDrawablePadding(loginButton.getResources().
                getDimensionPixelSize(R.dimen.fb_margin_override_textpadding));
        loginButton.setPadding(
                loginButton.getResources().getDimensionPixelSize(
                        R.dimen.fb_margin_override_lr),
                loginButton.getResources().getDimensionPixelSize(
                        R.dimen.fb_margin_override_top),
                0,
                loginButton.getResources().getDimensionPixelSize(
                        R.dimen.fb_margin_override_bottom));
    }

    /*
    INSERT TO DATABASE FUNCTION: CREATES ASYNCKTASK TO INSERT USER'S INFORMATION TO MYSQL DATABASE VIA PHP
     */
    private void insertToDatabase() {
        class InsertTask extends AsyncTask<Integer, Void, Void> { //Create async task insert task
            private ProgressDialog dialog;

            public InsertTask(Activity activity)
            {
                this.dialog = new ProgressDialog(activity);
                this.dialog.setTitle("Hello, it's me.");
                this.dialog.setMessage("Soon enough you won't be bored anymore");
                dialog.show();
            }

            @Override
            protected Void doInBackground(Integer... params) { //insert in the background to database
                try
                {
                    insert(); //insert function is called

                    MyConnectionManager.getInstance().setConnectionConfiguration(getApplicationContext());
                    MyConnectionManager.getInstance().connect();

                    boolean newAccount = MyConnectionManager.getInstance().createAccount(hashData.get("facebook_id"), "smack");
                    MyConnectionManager.getInstance().login();

                    if(newAccount)
                    {
                        Log.v(TAG, "Nome: " + hashData.get("friend_name") + ". ID: " + hashData.get("friend_id"));
                        MyConnectionManager.getInstance().addFriend(hashData.get("friend_id") + "@" + host);
                    }
                }
                catch (Exception e) //catches IO exception
                {
                    Log.e(TAG, e.toString());
                }

                return null;
            }
            protected void onPostExecute(Void v)
            {
                dialog.dismiss();
                Intent i = new Intent(LoginActivity.this, BoredActivity.class);
                i.putExtra("user", hashData.get("facebook_id"));
                i.putExtra("pass", "android");
                startActivity(i);
                finish();
            }
        }
        InsertTask task = new InsertTask(LoginActivity.this); //new insert task
        task.execute(); //execute insert task
    }

    /*
    INSERT FUNCTION: INSERT USER'S INFORMATION DO MYSQL DATABASE VIA PHP
     */
    private Boolean insert() throws IOException {
        URL url = new URL(server); //server url
        InputStream is = null; //create new input stream
        try {
            // OPEN CONNECTION FOR GIVEN URL AND SET CONNECTION SETTINGS
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000); //IF TIMEOUT EXPIRES, EXCEPTION IS RAISED
            conn.setConnectTimeout(15000); //IF TIMEOUT EXPIRES, EXCEPTION IS RAISED
            conn.setRequestMethod("POST"); //METHOD POST
            conn.setDoInput(true); //USING URL FOR INPUT
            conn.setDoOutput(true); //USING URL FOR OUTPUT

            //OUTPUT STREAM TO GIVE PHP SCRIPT HASHDATA OUTPUT
            OutputStream os = conn.getOutputStream(); //get output stream from connection
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8")); //create new buffed writer
            writer.write(fromHashToPostString(hashData)); //write POST string to output stream
            writer.flush(); //flushes the output stream, forces any buffered output bytes to be written out
            writer.close(); //close buffed writer
            os.close(); //close output stream

            conn.connect(); //opens a communication link with given url
            int response = conn.getResponseCode(); //get responde code from url
            Log.e(TAG, "The response is: " + response);
            is = conn.getInputStream(); //get input stream
            Log.e(TAG, is.toString());
        } finally {
            if (is != null) { //if input stream was opened
                is.close(); //closes input stream
            }
        }
        return true;
    }

    /*
    CONVERTS A HASH MAP CONTAINING USER'S INFORMATION FOR A PHP READABLE POST STRING
     */
    private String fromHashToPostString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder(); //creates new string builder
        boolean first = true; //new boolean first string
        for(Map.Entry<String, String> entry : params.entrySet()){ //for each entry
            if (first) //if is first
                first = false; //first equals false
            else
                result.append("&"); //if is not first, add &

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8")); //add key to the string
            result.append("="); //add = to the string
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8")); //add value to the string
        } //example string: NAME=GUILHERME&AGE=19
        return result.toString(); //return full POST string
    }

    //ACTIVITY RESULT FOR FACEBOOK LOGIN (CALLING CALLBACK MANAGER)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
