package gooeyn.bored;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import org.jivesoftware.smack.chat.*;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

public class BoredActivity extends CountdownActivity {
    String TAG = "myshit";
    ImageView profileImgView;
    TextView profileTxtView;
    ListView events_list;
    boolean isRegistered = false;
    ArrayList<People> people = new ArrayList<>();
    PeopleAdapter adapter;
    ImageView navImageView;
    Intent intent;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_bored_nav);

        /* CHECK IF USER IS LOGGED IN OR NOT */
        if (!isLoggedIn()) { //IF USER IS NOT LOGGED IN
            Intent i = new Intent(this, LoginActivity.class); //LOGIN ACTIVITY INTENT
            startActivity(i); //START LOGIN ACTIVITY
            finish(); //FINISHES MAIN ACTIVITY
        }
        else //IF USER IS LOGGED IN
        {
            intent = getIntent();
            getFacebookData();
        /* DECLARE ALL VARIABLES */
            navImageView = (ImageView) findViewById(R.id.imageViewDroid);
            profileImgView = (ImageView) findViewById(R.id.profileImgView);
            profileTxtView = (TextView) findViewById(R.id.profileTxtView);
            events_list = (ListView) findViewById(R.id.peopleBored);
            final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            final Button btn = (Button) findViewById(R.id.buttonBored);
            final TextView txt = (TextView) findViewById(R.id.textPress);
            context = this;
            adapter = new PeopleAdapter(context, people);
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
                    LoginManager.getInstance().logOut();
                }
            });
        }
    }

    public void getFacebookData()
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken(); // get current access token

        GraphRequest request = GraphRequest.newMeRequest(accessToken,
                new GraphRequest.GraphJSONObjectCallback()
                {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response)
                    {
                        try
                        {
                            String URL = object.getJSONObject("picture").getJSONObject("data").getString("url");
                            //new DownloadImage().execute(URL);
                            //Picasso.with(context).load(URL).into(profileImgView);
                            Picasso.with(context).load(URL).transform(new CircleTransform()).into(profileImgView);
                            profileTxtView.setText(object.getString("name"));
                        }
                        catch (JSONException e)
                        {
                            Log.e(TAG, "Error on completed Graph request: " + e.toString());
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "picture.type(large),name,cover,friends");
        request.setParameters(parameters);
        request.executeAsync();
    }

    //METHOD TO CHECK IF USER IS LOGGED IN
    public boolean isLoggedIn()
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken(); //GET THE CURRENT ACCESS TOKEN OF USER
        return accessToken != null; //IF THE ACCESS TOKEN IS NULL, RETURN FALSE. OTHERWISE RETURN TRUE.
    }

    // CREATE OPTIONS MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
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

            Collection<RosterEntry> entries = roster.getEntries();

            for (RosterEntry entry : entries) {
                Presence presence = roster.getPresence(entry.getUser());
                if(presence != null)
                {
                    if(presence.getStatus() != null)
                    {
                        if (presence.getStatus().equals("Bored")) {
                            if(isUnique(people, presence.getFrom()))
                            {
                                people.add(new People(presence.getFrom(), "roster entry"));
                            }
                        }
                    }
                }

            }

            adapter.notifyDataSetChanged();

            roster.addRosterListener(new RosterListener() {
                @Override
                public void entriesDeleted(Collection<String> addresses) {
                    Log.v(TAG, "entriesDeleted");
                }

                @Override
                public void entriesUpdated(Collection<String> addresses) {
                    Log.v(TAG, "entriesUpdated");
                }

                @Override
                public void entriesAdded(Collection<String> addresses) {
                    Log.v(TAG, "entriesAdded");
                    for (String address : addresses) {
                        MyConnectionManager.getInstance().addFriend(address, address + "roster");
                        Log.v(TAG, "entrisAdded: " + address);
                    }

                }

                @Override
                public void presenceChanged(Presence presence) {
                    Log.v(TAG, "The following presence has changed: " + presence.getFrom() + " :" + presence.getStatus());

                    if (presence.getStatus() == null)
                    {
                        for (People d : people)
                        {
                            if (d.getName().equals(presence.getFrom()))
                            {
                                people.remove(d);
                            }
                        }
                    } else if (presence.getStatus().equals("Bored")) {
                        if(isUnique(people, presence.getFrom()))
                        {
                            Log.v(TAG, "presence changed getFrom " + presence.getFrom());
                            people.add(new People(presence.getFrom(), "presence changed"));
                        }
                    } else {
                        for (People d : people) {
                            if (d.getName().equals(presence.getFrom()))
                            {
                                people.remove(d);
                            }
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

            /*
                ChatManager chatmanager = ChatManager.getInstanceFor(MyConnectionManager.getInstance().getConnection());
                chatmanager.addChatListener(new ChatManagerListener() {
                @Override
                public void chatCreated(org.jivesoftware.smack.chat.Chat chat, boolean createdLocally) {
                    chat.addMessageListener(new ChatMessageListener() {
                        @Override
                        public void processMessage(org.jivesoftware.smack.chat.Chat chat, Message message) {
                            Log.v(TAG, chat.getParticipant() + ". m: " + message.getBody());
                            if (message.getBody() != null) {
                                Log.v(TAG, "eba: " + message.getFrom());
                                Toast.makeText(context, "You got a message from " + message.getFrom(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });
*/
            ChatManager chatmanager = ChatManager.getInstanceFor(MyConnectionManager.getInstance().getConnection());
           /* chatmanager.addChatListener(new ChatManagerListener() {
                @Override
                public void chatCreated(org.jivesoftware.smack.chat.Chat chat, boolean createdLocally) {
                    chat.addMessageListener(new ChatMessageListener() {
                        @Override
                        public void processMessage(org.jivesoftware.smack.chat.Chat chat, Message message) {
                            Log.v(TAG, chat.getParticipant() + ". m: " + message.getBody());
                            if (message.getBody() != null) {
                                Log.v(TAG, "eba: " + message.getFrom());
                                Toast.makeText(context, "You got a message from " + message.getFrom(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });*/

            chatmanager.addChatListener(
                    new ChatManagerListener() {
                        @Override
                        public void chatCreated(Chat chat, boolean createdLocally)
                        {
                            if (!createdLocally)
                                chat.addMessageListener(new ChatMessageListener() {
                                    @Override
                                    public void processMessage(org.jivesoftware.smack.chat.Chat chat, Message message) {
                                        Log.v(TAG, chat.getParticipant() + ". m: " + message.getBody());
                                        if (message.getBody() != null) {
                                            Log.d(TAG, "HEY NEW FCKING MEEEESSAGE: " + message.getFrom());
                                            for (People d : people) {
                                                if (d.getName().equals(message.getFrom()))
                                                {
                                                    d.setStatus(message.getBody());
                                                }
                                            }
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    adapter.notifyDataSetChanged();
                                                }
                                            });
                                        }
                                    }
                                });
                        }
                    });

            dialog.dismiss();
        }
    }
    public Boolean isUnique(ArrayList<People> people, String user)
    {
        for (People d : people) {
            if (d.getName().equals(user)) return false;
        }

        return true;
    }
}
