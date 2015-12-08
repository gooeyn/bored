package gooeyn.bored;

import android.content.Context;
import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.packet.Presence;
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
    String TAG = "myshit";

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

    public KeyStore getKeyStore(Context context)
    {
        InputStream ins = context.getResources().openRawResource(R.raw.keystore_bored);
        KeyStore ks = null;

        try
        {
            ks = KeyStore.getInstance("BKS");
            ks.load(ins, "abc".toCharArray());
            Log.e(TAG, "try ks" + ks.toString());
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        return ks;
    }

    public TrustManagerFactory getTrustManager(KeyStore ks)
    {
        TrustManagerFactory tmf = null;

        try
        {
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        return tmf;
    }

    public SSLContext getSSLContext(TrustManagerFactory tmf)
    {
        SSLContext ssl = null;

        try
        {
            ssl = SSLContext.getInstance("TLS");
            ssl.init(null, tmf.getTrustManagers(), new SecureRandom());
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        return ssl;
    }

    public void connect()
    {
        if(connection.isConnected()) return;

        try
        {
            connection.setPacketReplyTimeout(10000);
            connection.connect();
        }
        catch(Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    public void login(String user, String password)
    {
        if(connection.isAuthenticated()) return;

        try
        {
            connection.login(user, password);
        }
        catch(Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }
    public void bored()
    {
        try
        {
            Presence p = new Presence(Presence.Type.available, "Bored", 1, Presence.Mode.available);
            connection.sendStanza(p);
        }
        catch(Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }
    public void notBored()
    {
        try
        {
            Presence p = new Presence(Presence.Type.available, "Not bored", 1, Presence.Mode.available);
            connection.sendStanza(p);
        }
        catch(Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }
    public void setConnectionConfiguration(Context context)
    {
        if(connection != null) return;

        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setUsernameAndPassword("a", "a")
                .setServiceName(serviceName)
                .setHost(host)
                .setResource(resource)
                .setPort(port)
                .setCustomSSLContext(getSSLContext(getTrustManager(getKeyStore(context))))
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

    public void createAccount(String user, String pass)
    {
        AccountManager manager = AccountManager.getInstance(connection);
        try
        {
            manager.createAccount(user, pass);
        }
        catch (Exception e)
        {

        }


    }
}
