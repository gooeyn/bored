package gooeyn.bored;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Collection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView androidView;
    AbstractXMPPConnection conn2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_people));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_chat));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        final SectionsPagerAdapter adapter = new SectionsPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.setCurrentItem(0);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ButtonActivity.class);
                startActivity(i);
                finish();
            }
        });

        View headerView = LayoutInflater.from(this).inflate(R.layout.nav_header_abc, null);
        navigationView.addHeaderView(headerView);
        androidView = (ImageView) headerView.findViewById(R.id.androidView);
        androidView = (ImageView) headerView.findViewById(R.id.androidView);
        final TextView androidText = (TextView) headerView.findViewById(R.id.androidText);
        AccessToken accessToken = AccessToken.getCurrentAccessToken(); // get current access token
        GraphRequest request = GraphRequest.newMeRequest( //make graph request for facebook data
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() { //callback from graph request
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) { //on completed request
                        try {
                            androidText.setText(object.getString("name"));
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

        connectToServer();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.abc, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            LoginManager.getInstance().logOut();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        int mNumOfTabs;
        public SectionsPagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    Bundle args = new Bundle();
                    //args.putParcelable("a", conn2);
                    PeopleFragment people = new PeopleFragment();
                    people.setArguments(args);
                    return people;
                case 1:
                    return new ChatFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }



















    private void setImage(Drawable drawable)
    {
        //mImageView.setBackgroundDrawable(drawable);
        androidView.setBackgroundDrawable(drawable);
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

                return new BitmapDrawable(bMap);

            } catch (Exception e) {
                Log.e("Error reading file", e.toString());
            }

            return null;
        }

    }

    public void connectToServer() {

        final ProgressDialog dialog = ProgressDialog.show(this, "Connecting...", "Please wait...", false);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream ins = getApplicationContext().getResources().openRawResource(R.raw.keystore2);
                KeyStore ks = null;
                try {
                    ks = KeyStore.getInstance("BKS");
                    ks.load(ins, "123".toCharArray());
                    Log.e("XMPPChatDemoActivity", "try ks" + ks.toString());
                } catch (Exception e) {
                    Log.e("XMPPChatDemoActivity", e.toString());
                }

                TrustManagerFactory tmf = null;
                try {
                    tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    tmf.init(ks);
                    Log.e("XMPPChatDemoActivity", "try tmf" + tmf.toString());
                } catch (Exception e) {
                    Log.e("XMPPChatDemoActivity", e.toString());
                }

                SSLContext sslctx = null;
                try {
                    sslctx = SSLContext.getInstance("TLS");
                    sslctx.init(null, tmf.getTrustManagers(), new SecureRandom());
                    Log.e("XMPPChatDemoActivity", "try ssl" + sslctx.toString());
                } catch (Exception e) {
                    Log.e("XMPPChatDemoActivity", e.toString());
                }
                // Create a connection to the jabber.org server on a specific port.
                XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                        .setUsernameAndPassword("a", "a")
                        .setServiceName("54.84.237.97")
                        .setHost("54.84.237.97")
                        .setResource("Android")
                        .setPort(5225)
                        .setCustomSSLContext(sslctx)
                        .setHostnameVerifier(new HostnameVerifier() {
                            @Override
                            public boolean verify(String hostname, SSLSession session) {
                                return true;
                            }
                        })
                        .build();
                conn2 = new XMPPTCPConnection(config);
                SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
                SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
                try{
                    conn2.connect();
                    Log.e("conectacaralho", "conectado: " + conn2.isConnected());
                } catch(Exception e)
                {
                    Log.e("conectacaralho", e.toString());
                }

                try{
                    conn2.login();
                    Log.e("conectacaralho", "conectado to: " + conn2.getUser());
                } catch(Exception e)
                {
                    Log.e("conectacaralho", e.toString());
                }
                Message message = new Message("b@ec2-54-84-237-97.compute-1.amazonaws.com", Message.Type.chat);
                message.setFrom(conn2.getUser());
                message.setBody("sou o celular do gui");
                try {
                    conn2.sendStanza(message);
                } catch(Exception e)
                {
                    Log.e("conectacaralho", e.toString());
                }

                Roster roster = Roster.getInstanceFor(conn2);


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
                    Log.e("conectacaralho", entry.getUser());
                }
                    dialog.dismiss();
            }
        });
        t.start();
        dialog.show();
    }
}
