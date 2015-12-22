package gooeyn.bored;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PeopleFragment extends Fragment {

    ArrayList<People> people = new ArrayList<>();
    PeopleAdapter adapter;
    String TAG = "myshit";
    HashMap<String, String> hashData = new HashMap<>();
    Activity activity;
    /*
    I NEED TO FIX THE FOLLOWING EXCEPTION
        java.util.ConcurrentModificationException
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "ONCREATE PEOPLE FRAGMENT");
        View view = inflater.inflate(R.layout.fragment_people, container, false);

        final Button boredButton = (Button) view.findViewById(R.id.buttonBored);
        final LinearLayout lowerTab = (LinearLayout) view.findViewById(R.id.lowerTab);
        final Button tabButton = (Button) view.findViewById(R.id.button2);
        final ListView peopleLV = (ListView) view.findViewById(R.id.peopleBored);
        final TextView txt = (TextView) view.findViewById(R.id.textPress);

        activity = getActivity();

        adapter = new PeopleAdapter(getContext(), people);
        peopleLV.setAdapter(adapter);
                /* SET ALL ON CLICK LISTENERS */

        if (MyConnectionManager.getInstance().isBored())
        {
            peopleLV.setVisibility(View.VISIBLE);
            boredButton.setVisibility(View.INVISIBLE);
            lowerTab.setVisibility(View.VISIBLE);
            txt.setVisibility(View.INVISIBLE);
        }

        boredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyConnectionManager.getInstance().bored();
                peopleLV.setVisibility(View.VISIBLE);
                boredButton.setVisibility(View.INVISIBLE);
                lowerTab.setVisibility(View.VISIBLE);
                txt.setVisibility(View.INVISIBLE);
                Roster roster = Roster.getInstanceFor(MyConnectionManager.getInstance().getConnection());

                try {
                    if (!roster.isLoaded()) roster.reloadAndWait();
                } catch (Exception e) {
                    Log.e(TAG, "reload");
                }

                Collection<RosterEntry> entries = roster.getEntries();

                for (RosterEntry entry : entries) {
                    Presence presence = roster.getPresence(entry.getUser());
                    Log.e(TAG, "This is the name: " + entry.getName());
                    Log.e(TAG, "This is the user: " + entry.getUser());
                    if (presence != null) {
                        if (presence.getStatus() != null) {
                            if (presence.getPriority() == 1) {
                                String from = presence.getFrom();
                                String id = from.substring(0, from.indexOf("@"));

                                if (isUnique(people, id)) {
                                    people.add(new People("", "", id, presence.getStatus()));
                                    new ConnectAndLoad(presence.getFrom()).execute();
                                } else {
                                    for (People d : people) {
                                        if (d.getId().equals(id)) {
                                            d.setStatus(presence.getStatus());
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    adapter.notifyDataSetChanged();
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        }
                    }

                }

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
                        Log.v(TAG, "The following presence has changed: " + presence.getFrom() + ": " + presence.getStatus());
                        //Log.e(TAG, "This is the name: " + r.getEntry(presence.getStatus()));
                        String from = presence.getFrom();
                        String id = from.substring(0, from.indexOf("@"));
                        Log.v(TAG, "PPPPPPPPPPPRESENCE STATUS: " + presence.getStatus());
                        Log.v(TAG, "PPPPPPPPPPPRESENCE MODE: " + presence.getMode());
                        Log.v(TAG, "PPPPPPPPPPPRESENCE PRIORITY: " + presence.getPriority());
                        Log.v(TAG, "PPPPPPPPPPPRESENCE TYPE: " + presence.getType());
                        if (presence.getStatus() == null) {

                            for (People d : people) {
                                if (d.getId().equals(id)) {
                                    Log.d(TAG, "people id: " + d.getId() + ". presence id: " + id);
                                    people.remove(d);
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                        } else if (presence.getPriority() == 1) {
                            if (isUnique(people, id)) {
                                people.add(new People("", "", id, presence.getStatus()));
                                new ConnectAndLoad(presence.getFrom()).execute();
                            } else {
                                for (People d : people) {
                                    if (d.getId().equals(id)) {
                                        d.setStatus(presence.getStatus());
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                adapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }
                            }
                        } else {
                            for (People d : people) {
                                Log.d(TAG, "people id: " + d.getId() + ". presence id: " + id);
                                if (d.getId().equals(id)) {
                                    people.remove(d);
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                        }
                    }
                });
            }
        });
        tabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyConnectionManager.getInstance().notBored();
                peopleLV.setVisibility(View.INVISIBLE);
                boredButton.setVisibility(View.VISIBLE);
                lowerTab.setVisibility(View.INVISIBLE);
                txt.setVisibility(View.VISIBLE);
            }
        });
        return view;
    }
    public Boolean isUnique(ArrayList<People> people, String user)
    {
        for (People d : people) {
            if (d.getId().equals(user)) return false;
        }
        return true;
    }














    public class ConnectAndLoad extends AsyncTask<String, Integer, Boolean> {
        private String user;
        public ConnectAndLoad(String user)
        {
            this.user = user;
        }

        @Override
        protected Boolean doInBackground(String... arg0) {

            String result = user.substring(0, user.indexOf("@"));
            Log.e(TAG, "RESULT:::::::::::::::::::: " + result);
            hashData.put("facebook_id", result);
            try
            {
                insert();
            }
            catch (Exception e) //catches IO exception
            {
                Log.e(TAG, e.toString());
            }
            return true;
        }

        protected void onPostExecute(Boolean boo)
        {
            adapter.notifyDataSetChanged();
        }
    }

     /*
    INSERT FUNCTION: INSERT USER'S INFORMATION DO MYSQL DATABASE VIA PHP
     */
    private Boolean insert() throws IOException {
        URL url = new URL("http://54.84.237.97/select.php"); //server url
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
            InputStream is2 = new BufferedInputStream(conn.getInputStream());
            Log.e(TAG, is2.toString());


            BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            String abc = total.toString();

            String username = abc.substring(0, abc.indexOf("@"));
            String profile = abc.substring(abc.indexOf("@") + 1, abc.length());
            String id = hashData.get("facebook_id");

            for (People d : people) {
                if (d.getId().equals(id))
                {
                    d.setName(username);
                    d.setProfile(profile);
                }
            }

            Log.e(TAG, total.toString());
            Log.e(TAG, profile);
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
}