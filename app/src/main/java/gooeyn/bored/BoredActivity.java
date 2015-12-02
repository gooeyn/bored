package gooeyn.bored;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.*;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

public class BoredActivity extends AppCompatActivity {

    ImageView profileImgView;
    ListView events_list;


    FragmentActivity my;
    AbstractXMPPConnection connection;
    String serviceName = "54.84.237.97";
    String host = "54.84.237.97";
    int port = 5225;
    String resource = "Android";
    ArrayList<People> people = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_bored);
        my = this;
        if (!isLoggedIn()) { //IF USER IS NOT LOGGED IN
            Intent i = new Intent(this, LoginActivity.class); //LOGIN ACTIVITY INTENT
            startActivity(i); //START LOGIN ACTIVITY
            finish(); //FINISHES MAIN ACTIVITY
        }
        MyConnectionManager.getInstance().connect(getApplicationContext());
        profileImgView = (ImageView) findViewById(R.id.profileImgView);
        final TextView profileTxtView = (TextView) findViewById(R.id.profileTxtView);

        AccessToken accessToken = AccessToken.getCurrentAccessToken(); // get current access token
        GraphRequest request = GraphRequest.newMeRequest( //make graph request for facebook data
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() { //callback from graph request
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) { //on completed request
                        try {
                            profileTxtView.setText(object.getString("name"));
                            JSONObject pic = object.getJSONObject("picture");
                            JSONObject data = pic.getJSONObject("data");

                            //JSONObject cover = object.getJSONObject("cover");
                            //JSONObject source = cover.getJSONObject("source");
                            new DownloadImage().execute(data.getString("url"));
                            //new DownloadImageSource().execute(cover.getString("source"));
                        } catch (JSONException e) {
                            Log.d("loginapp", e.toString());
                            Log.d("loginapp", object.toString());
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "picture.type(large),name,cover");
        request.setParameters(parameters);
        request.executeAsync();
/*        ArrayList<People> people = new ArrayList<>(); //EVENT ARRAYLIST

        AbstractXMPPConnection connection = null;
        while(connection == null)
        {
            connection = MyConnectionManager.getInstance().getConnection();
        }
        Roster roster = Roster.getInstanceFor(connection);
        try {
            if (!roster.isLoaded())
                roster.reloadAndWait();
        } catch(Exception e)
        {
            Log.e("conectacaralho", "reload");
        }

        Collection<RosterEntry> entries = roster.getEntries();
        Log.e("conectacaralho", "vazio: " + entries.isEmpty());
        for (RosterEntry entry : entries) {
            people.add(new People(entry.getUser()));
            Log.e("conectacaralho", "" + entry.getUser());
        }


        roster.addRosterListener(new RosterListener() {
            // Ignored events public void entriesAdded(Collection<String> addresses) {}
            public void entriesDeleted(Collection<String> addresses) {
            }

            public void entriesUpdated(Collection<String> addresses) {
            }
            public void entriesAdded(Collection<String> addresses) {
            }

            public void presenceChanged(Presence presence) {
                Log.e("conectacaralho", "Presence changed: " + presence.getFrom() + " " + presence);
            }
        });
        people.add(new People("Wagner"));
        people.add(new People("Luci"));
        people.add(new People("Giulia"));
        people.add(new People("Giulia"));
        people.add(new People("Renato"));
        people.add(new People("Andre"));
        people.add(new People("Guigo"));
        people.add(new People("Phillipe"));
        people.add(new People("Mary"));
        people.add(new People("Bruno"));
*/
        events_list = (ListView) findViewById(R.id.peopleBored);
        events_list.setAdapter(new PeopleAdapter(this, people));

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final Button btn = (Button) findViewById(R.id.buttonBored);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                events_list.setVisibility(View.VISIBLE);
                btn.setVisibility(View.INVISIBLE);
                fab.setVisibility(View.VISIBLE);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                events_list.setVisibility(View.INVISIBLE);
                btn.setVisibility(View.VISIBLE);
                fab.setVisibility(View.INVISIBLE);
            }
        });
        new ConnectAndLoad().execute();
    }

    //METHOD TO CHECK IF USER IS LOGGED IN
    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken(); //GET THE CURRENT ACCESS TOKEN OF USER
        return accessToken != null; //IF THE ACCESS TOKEN IS NULL, RETURN FALSE. OTHERWISE RETURN TRUE.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bored_menu, menu);
        return true;
    }

    public class ConnectAndLoad extends AsyncTask<String, Integer, ArrayList<People>> {

        @Override
        protected ArrayList<People> doInBackground(String... arg0) {
            // This is done in a background thread

            InputStream ins = getApplicationContext().getResources().openRawResource(R.raw.keystore_bored);
            KeyStore ks = null;
            try {
                ks = KeyStore.getInstance("BKS");
                ks.load(ins, "123".toCharArray());
                Log.e("XMPPChatDemoActivity", "try ks" + ks.toString());
            } catch (Exception e) {
                Log.e("XMPPChatDemoActivity", e.toString());
            }

            //CREATING TRUST MANAGER USING KEYSTORE CREATED BEFORE
            TrustManagerFactory tmf = null;
            try {
                tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(ks);
                Log.e("XMPPChatDemoActivity", "try tmf" + tmf.toString());
            } catch (Exception e) {
                Log.e("XMPPChatDemoActivity", e.toString());
            }

            //CREATING SSLCONTEXT USING TRUST MANAGER CREATED BEFORE
            SSLContext sslctx = null;
            try {
                sslctx = SSLContext.getInstance("TLS");
                sslctx.init(null, tmf.getTrustManagers(), new SecureRandom());
                Log.e("XMPPChatDemoActivity", "try ssl" + sslctx.toString());
            } catch (Exception e) {
                Log.e("XMPPChatDemoActivity", e.toString());
            }

            //CREATE A CONNECTION
            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setUsernameAndPassword("a", "a")
                    .setServiceName(serviceName)
                    .setHost(host)
                    .setResource(resource)
                    .setPort(port)
                    .setCustomSSLContext(sslctx)
                    .setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .build();
            connection = new XMPPTCPConnection(config);
            SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
            SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
            //I need to see what setHostnameVerifier and SASLAuthentication do

            //TRY TO CONNECT
            try{
                connection.setPacketReplyTimeout(10000);
                connection.connect();
                Log.e("conectacaralho", "conectado: " + connection.isConnected());
            } catch(Exception e)
            {
                Log.e("conectacaralho", e.toString());
            }

            //TRY TO LOGIN
            try{
                connection.login();
                Log.e("conectacaralho", "conectado to: " + connection.getUser());
            } catch(Exception e)
            {
                Log.e("conectacaralho", e.toString());
            }

            Roster roster = Roster.getInstanceFor(connection);
            try {
                if (!roster.isLoaded())
                    roster.reloadAndWait();
            } catch(Exception e)
            {
                Log.e("conectacaralho", "reload");
            }

            Collection<RosterEntry> entries = roster.getEntries();
            Log.e("conectacaralho", "vazio: " + entries.isEmpty());
            for (RosterEntry entry : entries) {
                people.add(new People(entry.getUser()));
                Log.e("conectacaralho", "" + entry.getUser());
            }

            MyConnectionManager.getInstance().setConnection(connection);
            ChatManager chatmanager = ChatManager.getInstanceFor(connection);
            chatmanager.addChatListener(new ChatManagerListener() {
                @Override
                public void chatCreated(org.jivesoftware.smack.chat.Chat chat, boolean createdLocally) {
                    chat.addMessageListener(new ChatMessageListener() {
                        @Override
                        public void processMessage(org.jivesoftware.smack.chat.Chat chat, Message message) {
                            Log.e("recebenu", chat.getParticipant() + ". m: " + message.getBody());
                            if (message.getBody() != null) {
                                Log.e("recebenu", "eba");
                                addItem(chat.getParticipant());
                            }
                        }
                    });
                }
            });
            return people;
        }

        public void addItem(String message){
            people.add(new People(message + ": new message."));
            ((BaseAdapter) events_list.getAdapter()).notifyDataSetChanged();
        }


        public void addItem(){
            Log.e("conectacaralho", "no additem");
            Log.e("conectacaralho", "n: " + people.size());
            people.add(new People("oi"));
            Log.e("conectacaralho", "n: " + people.size());
            ((BaseAdapter) events_list.getAdapter()).notifyDataSetChanged();
        }
        /**
         * Called after the image has been downloaded
         * -> this calls a function on the main thread again
         */
        protected void onPostExecute(ArrayList<People> p)
        {
            events_list.setAdapter(new PeopleAdapter(my, people));
            Roster roster = Roster.getInstanceFor(connection);
            roster.addRosterListener(new RosterListener() {
                // Ignored events public void entriesAdded(Collection<String> addresses) {}
                public void entriesDeleted(Collection<String> addresses) {
                }

                public void entriesUpdated(Collection<String> addresses) {
                }

                public void entriesAdded(Collection<String> addresses) {
                }

                public void presenceChanged(Presence presence) {
                    Log.e("conectacaralho", "Presence changed: " + presence.getFrom() + " " + presence);
                    addItem();
                }
            });
        }
    }





    private void setImage(Drawable drawable)
    {
        if(Build.VERSION.SDK_INT >= 16) {
            profileImgView.setBackground(drawable);
        } else {
            profileImgView.setBackgroundDrawable(drawable);
        }
    }

    public class DownloadImage extends AsyncTask<String, Integer, Drawable> {

        @Override
        protected Drawable doInBackground(String... arg0) {
            // This is done in a background thread
            return downloadImage(arg0[0]);
        }

        /**
         * Called after the image has been downloaded
         * -> this calls a function on the main thread again
         */
        protected void onPostExecute(Drawable image)
        {
            setImage(image);
        }

        private Drawable downloadImage(String _url)
        {
            //Prepare to download image
            URL url;
            InputStream in;
            BufferedInputStream buf;

            //BufferedInputStream buf;
            try {
                url = new URL(_url);
                in = url.openStream();
                buf = new BufferedInputStream(in);
                Bitmap bMap = BitmapFactory.decodeStream(buf);
                in.close();
                buf.close();

                Bitmap bMap2 = getCroppedBitmap(bMap, 300);

                return new BitmapDrawable(getApplicationContext().getResources(), bMap2);

            } catch (Exception e) {
                Log.e("Error reading file", e.toString());
            }

            return null;
        }
        public Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
            Bitmap sbmp;
            if(bmp.getWidth() != radius || bmp.getHeight() != radius)
                sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
            else
                sbmp = bmp;
            Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
                    sbmp.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xffa19774;
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
