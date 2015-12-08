package gooeyn.bored;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;

import org.jivesoftware.smack.chat.*;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

public class BoredActivity extends AppCompatActivity {
    String TAG = "myshit";
    ImageView profileImgView;
    TextView profileTxtView;
    ListView events_list;
    boolean isRegistered = false;
    ArrayList<People> people = new ArrayList<>();
    PeopleAdapter adapter;
    ImageView navImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_bored_nav);

        /* CHECK IF USER IS LOGGED IN OR NOT */
        if (!isLoggedIn()) { //IF USER IS NOT LOGGED IN
            Intent i = new Intent(this, LoginActivity.class); //LOGIN ACTIVITY INTENT
            startActivity(i); //START LOGIN ACTIVITY
            finish(); //FINISHES MAIN ACTIVITY
        }
        else
        {
            getFacebookData();
        /* DECLARE ALL VARIABLES */
            navImageView = (ImageView) findViewById(R.id.imageViewDroid);
            profileImgView = (ImageView) findViewById(R.id.profileImgView);
            profileTxtView = (TextView) findViewById(R.id.profileTxtView);
            events_list = (ListView) findViewById(R.id.peopleBored);
            events_list.setTranscriptMode(2);
            final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            final Button btn = (Button) findViewById(R.id.buttonBored);
            final TextView txt = (TextView) findViewById(R.id.textPress);
            adapter = new PeopleAdapter(this, people);
            events_list.setAdapter(adapter);
        /* SET ALL ON CLICK LISTENERS */
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ConnectAndLoad(BoredActivity.this).execute();
                    events_list.setVisibility(View.VISIBLE);
                    btn.setVisibility(View.INVISIBLE);
                    fab.setVisibility(View.VISIBLE);
                    txt.setVisibility(View.INVISIBLE);
                    isRegistered = true;
                }
            });
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyConnectionManager.getInstance().notBored();
                    events_list.setVisibility(View.INVISIBLE);
                    btn.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.INVISIBLE);
                    txt.setVisibility(View.VISIBLE);
                    isRegistered = false;
                }
            });

            final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

            profileImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawer.openDrawer(GravityCompat.START);
                }
            });
        }
    }

    public void getFacebookData()
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken(); // get current access token
        GraphRequest request = GraphRequest.newMeRequest( //make graph request for facebook data
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() { //callback from graph request
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) { //on completed request
                        try {
                            profileTxtView.setText(object.getString("name"));
                            new DownloadImage().execute(object.getJSONObject("picture").getJSONObject("data").getString("url"));
                        } catch (JSONException e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "picture.type(large),name,cover");
        request.setParameters(parameters);
        request.executeAsync();
    }

    //METHOD TO CHECK IF USER IS LOGGED IN
    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken(); //GET THE CURRENT ACCESS TOKEN OF USER
        return accessToken != null; //IF THE ACCESS TOKEN IS NULL, RETURN FALSE. OTHERWISE RETURN TRUE.
    }

    // CREATE OPTIONS MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bored_menu, menu);
        return true;
    }

    //ON OPTIONS ITEMS SELECTED
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.new_game:
                adapter.notifyDataSetChanged();
                return true;
            case R.id.help:
                LoginManager.getInstance().logOut();
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //ON PREPARE OPTIONS MENU
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem registrar = menu.findItem(R.id.new_game);
        registrar.setVisible(!isRegistered);
        return true;
    }

    /*
    THIS ASYNCTASK CONNECTS TO THE SERVER, IT ALSO LOGINS AND LOAD ALL THE CONTACTS
    AND SET ALL THE LISTENERS.
     */
    public class ConnectAndLoad extends AsyncTask<String, Integer, Boolean> {
        private ProgressDialog dialog;
        private Activity activity;
        public ConnectAndLoad(Activity activity)
        {
            this.dialog = new ProgressDialog(activity);
            this.dialog.setTitle("Hello, it's me.");
            this.dialog.setMessage("Loading bored people..");
            dialog.show();
            this.activity = activity;
        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            MyConnectionManager.getInstance().setConnectionConfiguration(getApplicationContext());
            MyConnectionManager.getInstance().connect();
            MyConnectionManager.getInstance().login();
            return true;
        }

        protected void onPostExecute(Boolean boo)
        {
                MyConnectionManager.getInstance().bored();
                Roster roster = Roster.getInstanceFor(MyConnectionManager.getInstance().getConnection());

                try
                {
                    if (!roster.isLoaded()) roster.reloadAndWait();
                }
                catch (Exception e)
                {
                    Log.e(TAG, "reload");
                }


            roster.addRosterListener(new RosterListener() {
                public void entriesDeleted(Collection<String> addresses) {
                }

                public void entriesUpdated(Collection<String> addresses) {
                }


                public void entriesAdded(Collection<String> addresses) {
                }

                @Override
                public void presenceChanged(Presence presence) {
                    Log.e(TAG, "The following presence has changed: " + presence.getFrom() + " :" + presence.getStatus());
                    if (presence.getStatus() == null) return;
                    if (presence.getStatus().equals("Bored")) {
                        people.add(new People(presence.getFrom(), presence.getStatus()));
                    } else {
                        for (People d : people) {
                            if (d.getName().equals(presence.getFrom())) people.remove(d);
                        }
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
                });

                ChatManager chatmanager = ChatManager.getInstanceFor(MyConnectionManager.getInstance().getConnection());
                chatmanager.addChatListener(new ChatManagerListener() {
                @Override
                public void chatCreated(org.jivesoftware.smack.chat.Chat chat, boolean createdLocally) {
                    chat.addMessageListener(new ChatMessageListener() {
                        @Override
                        public void processMessage(org.jivesoftware.smack.chat.Chat chat, Message message) {
                            Log.e(TAG, chat.getParticipant() + ". m: " + message.getBody());
                            if (message.getBody() != null) {
                                Log.e(TAG, "eba: " + message.getFrom());
                            }
                        }
                    });
                }
            });
            dialog.dismiss();
        }
    }
    /*
    THIS ASYNCTASK DOWNLOADS THE GIVEN IMAGE IN BACKGROUD AND THEN SETS
    IT TO AN IMAGE VIEW.
     */
    public class DownloadImage extends AsyncTask<String, Integer, Drawable> {

        @Override
        protected Drawable doInBackground(String... arg0) {
            URL url;
            InputStream in;
            BufferedInputStream buf;

            try
            {
                url = new URL(arg0[0]);
                in = url.openStream();
                buf = new BufferedInputStream(in);
                Bitmap bMap = BitmapFactory.decodeStream(buf);
                in.close();
                buf.close();
                Bitmap bMap2 = getRoundedBitmap(bMap, 300);
                return new BitmapDrawable(getApplicationContext().getResources(), bMap2);
            }
            catch (Exception e)
            {
                Log.e(TAG, e.toString());
            }

            return null;
        }

        @SuppressWarnings("deprecation")
        protected void onPostExecute(Drawable image)
        {
            if(Build.VERSION.SDK_INT >= 16) {
                profileImgView.setBackground(image);
                //navImageView.setBackground(image);
            } else {
                profileImgView.setBackgroundDrawable(image);
                //navImageView.setBackgroundDrawable(image);
            }
        }

        public Bitmap getRoundedBitmap(Bitmap bmp, int radius) {
            Bitmap sbmp;
            if(bmp.getWidth() != radius || bmp.getHeight() != radius)
                sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
            else
                sbmp = bmp;
            Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
                    sbmp.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setDither(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.parseColor("#BAB399"));
            canvas.drawCircle(sbmp.getWidth() / 2+0.7f, sbmp.getHeight() / 2+0.7f,
                    sbmp.getWidth() / 2+0.1f, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(sbmp, rect, rect, paint);

            return output;
        }
    }
}
