package gooeyn.bored;

import android.content.Context;
import android.util.Log;

import com.facebook.AccessToken;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;

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
    String TAG = "myshit/MyConnectionManager";
    private Boolean isBored = false;
    public String status = "Click here to set your status message";

    private static MyConnectionManager instance = null;

    private MyConnectionManager(){}

    public static MyConnectionManager getInstance()
    {
        if(instance == null)
        {
            instance = new MyConnectionManager();
        }
        return instance;
    }

    public AbstractXMPPConnection getConnection()
    {
        return connection;
    }

    public KeyStore generateKeyStore(Context context)
    {
        Log.v(TAG, "Generating key store..");
        InputStream ins = context.getResources().openRawResource(R.raw.keystore_bored);
        KeyStore ks = null;

        try
        {
            ks = KeyStore.getInstance("BKS");
            ks.load(ins, "abc".toCharArray());
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error generating key store: " + e.toString());
        }

        return ks;
    }

    public TrustManagerFactory generateTrustManagerFactory(KeyStore ks)
    {
        Log.v(TAG, "Generating trust manager factory..");
        TrustManagerFactory tmf = null;

        try
        {
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error generating trust manager factory: " + e.toString());
        }

        return tmf;
    }

    public SSLContext generateSSLContext(TrustManagerFactory tmf)
    {
        Log.v(TAG, "Generating SSL context..");
        SSLContext ssl = null;

        try
        {
            ssl = SSLContext.getInstance("TLS");
            ssl.init(null, tmf.getTrustManagers(), new SecureRandom());
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error generating SSL context: " + e.toString());
        }

        return ssl;
    }

    public void connect()
    {
        Log.v(TAG, "Attempting to connect..");
        if(connection.isConnected()) return;

        try
        {
            connection.setPacketReplyTimeout(10000);
            connection.connect();
        }
        catch(Exception e)
        {
            Log.e(TAG, "Error connecting: " + e.toString());
        }
    }

    public void login()
    {
        Log.v(TAG, "Attempting to login..");
        if(connection.isAuthenticated()) return;

        AccessToken accessToken = AccessToken.getCurrentAccessToken(); // get current access token
        try
        {
            connection.login(accessToken.getUserId(), "smack");
        }
        catch(Exception e)
        {
            Log.e(TAG, "Error loging in: " + e.toString());
        }
    }

    public void bored()
    {
        Log.v(TAG, "Setting status to BORED..");
        isBored = true;
        try
        {
            Presence p = new Presence(Presence.Type.available, status, 1, Presence.Mode.available);
            connection.sendStanza(p);
        }
        catch(Exception e)
        {
            Log.e(TAG, "Error setting status to BORED: " + e.toString());
        }
    }

    public void notBored()
    {
        Log.v(TAG, "Setting status to NOT BORED..");
        isBored = false;
        try
        {
            status = "Click here to set your status message";
            Presence p = new Presence(Presence.Type.available, "", 0, Presence.Mode.available);
            connection.sendStanza(p);
        }
        catch(Exception e)
        {
            Log.e(TAG, "Error setting status to NOT BORED: " + e.toString());
        }
    }

    public void setConnectionConfiguration(Context context)
    {
        Log.v(TAG, "Setting connection configuration..");
        if(connection != null) return;

        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setServiceName(serviceName)
                .setHost(host)
                .setResource(resource)
                .setPort(port)
                .setCustomSSLContext(generateSSLContext(generateTrustManagerFactory(generateKeyStore(context))))
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
    }

    public boolean createAccount(String user, String pass)
    {
        Log.v(TAG, "Creating account..");
        if(connection == null) return false;

        AccountManager accountManager = AccountManager.getInstance(connection);
        try
        {
            accountManager.createAccount(user, pass);
            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error creating account: " + e.toString());
            return false;
        }
    }

    public void addFriend(String user, String nickname)
    {
        Log.v(TAG, "Adding friend..");
        if(connection == null) return;

        Roster roster = Roster.getInstanceFor(connection);
        try
        {
            roster.createEntry(user, nickname, null);
            Log.v(TAG, "Friend added: " + nickname);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error adding friend: " + e.toString());
        }
    }
    public boolean isBored()
    {
        return isBored;
    }

    public void setStatus(String status)
    {
        Log.v(TAG, "Setting status..");
        isBored = true;
        try
        {
            Presence p = new Presence(Presence.Type.available, status, 1, Presence.Mode.available);
            this.status = status;
            connection.sendStanza(p);
        }
        catch(Exception e)
        {
            Log.e(TAG, "Error setting status: " + e.toString());
        }
    }
}
