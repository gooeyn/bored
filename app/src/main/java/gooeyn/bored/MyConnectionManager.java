package gooeyn.bored;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

public class MyConnectionManager {
    AbstractXMPPConnection connection;
    String serviceName = "54.84.237.97";
    String host = "54.84.237.97";
    int port = 5225;
    String resource = "Android";

    private static MyConnectionManager instance = null;

    private MyConnectionManager(){}

    public static MyConnectionManager getInstance(){
        if(instance == null)
        {
            instance = new MyConnectionManager();
        }
        return instance;
    }
    public void connect(final Context context) {
        //final ProgressDialog dialog = ProgressDialog.show(context, "Connecting...", "Please wait...", false);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //LOADING KEY STORE
                InputStream ins = context.getResources().openRawResource(R.raw.keystore_bored2);
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
                    Log.e("conectacaralho", "TRYING TO CONNECT. IS CONNECTED: " + connection.isConnected());
                } catch(Exception e)
                {
                    Log.e("conectacaralho", "TRYING TO CONNECT. " + e.toString());
                }

                //TRY TO LOGIN
                try{
                    connection.login();
                    Log.e("conectacaralho", "TRYING TO LOGIN. CONNECTED TO: " + connection.getUser());
                } catch(Exception e)
                {
                    Log.e("conectacaralho", "TRYING TO LOGIN. " + e.toString());
                }

                /* TRY TO SEND A MESSAGE
                Message message = new Message("b@ec2-54-84-237-97.compute-1.amazonaws.com", Message.Type.chat);
                message.setFrom(connection.getUser());
                message.setBody("sou o celular do gui");
                try {
                    connection.sendStanza(message);
                } catch(Exception e)
                {
                    Log.e("conectacaralho", e.toString());
                }*/

                /* TRY TO SET PRESENCE
                Presence presence = new Presence(Presence.Type.available);
                presence.setStatus("Hike anyone?");
// Send the packet (assume we have an XMPPConnection instance called "con").
                try {
                    connection.sendStanza(presence);
                } catch(Exception e)
                {
                    Log.e("conectacaralho", "presence");
                }
                */

                /*TRY TO GET ROSTER (FRIENDS)
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
                    Log.e("conectacaralho", entry.getUser());
                }*/
                //dialog.dismiss();
            }
        });
        t.start();
        //dialog.show();
    }
    public AbstractXMPPConnection getConnection() {
        return connection;
    }

    public boolean isConnected()
    {
        return connection.isConnected();
    }
    public void setConnection(AbstractXMPPConnection connection)
    {
        this.connection = connection;
    }
}
