package gooeyn.bored;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jivesoftware.smack.chat.*;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class ConversationActivity extends AppCompatActivity {
    ArrayList<MyMessage> myChat = new ArrayList<>();
    String TAG = "myshit";
    MessagesAdapter adapter;
    Activity activity;

    //Files
    String messagesFile = "messages_";
    String pictureFile = "picture_";
    String nameFile = "name_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        final String user = intent.getStringExtra("user");
        final String id = intent.getStringExtra("id");
        setTitle(user);
        setImage(id);

        Button sendButton = (Button) findViewById(R.id.buttonSend);
        ListView listview = (ListView) findViewById(R.id.listMessages);
        final EditText sendText = (EditText) findViewById(R.id.editTextSend);


/*
        String currentMessages = "";
        try {
            FileInputStream fis2 = getApplicationContext().openFileInput(messagesFile + id);
            StringBuilder builder = new StringBuilder();
            int ch;
            while ((ch = fis2.read()) != -1) {
                builder.append((char) ch);
            }
            currentMessages = builder.toString();
            fis2.close();
            Log.e(TAG, "messages: " + builder.toString());
        } catch (Exception e) {
            Log.e(TAG, "EXCEPTION ON GETTING MESSAGES: " + e.toString());
        }

        myChat.add(new MyMessage(currentMessages, false));*/
        activity = ConversationActivity.this;
        adapter = new MessagesAdapter(getApplicationContext(), myChat);
        listview.setAdapter(adapter);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = sendText.getText().toString();

                ChatManager cm = ChatManager.getInstanceFor(MyConnectionManager.getInstance().getConnection());
                Log.e(TAG, "Chat will be created with user: " + id + "@54.84.237.97");
                Log.e(TAG, "Chat will have the message: " + text);

                Chat newChat = cm.createChat(id + "@54.84.237.97");

                try {
                    newChat.sendMessage(text);
                    myChat.add(new MyMessage(text, true));
                    notifyDataSetChanged();
                    sendText.setText("");
                    Log.e(TAG, "The message was sent");
                }
                catch (Exception e)
                {
                    Log.e(TAG, "Error sending message: " + e.toString());
                }
            }
        });

        ChatManager chatmanager = ChatManager.getInstanceFor(MyConnectionManager.getInstance().getConnection());
        chatmanager.addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean createdLocally) {
                chat.addMessageListener(new ChatMessageListener() {
                    @Override
                    public void processMessage(Chat chat, Message message) {
                        if (message.getBody() != null) {
                            Log.e(TAG, "message body: " + message.getBody());
                            myChat.add(new MyMessage(message.getBody(), false));
                            notifyDataSetChanged();
                        }
                    }
                });
            }
        });
    }

    public void notifyDataSetChanged()
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void setImage(String id)
    {
        pictureFile += id; //FILE PICTURE NAME. EX: 12423487398_profile
        final File file = new File(getApplicationContext().getFilesDir(), pictureFile); //CREATES/GETS FILE USING FILENAME

        if(file.exists())
        {
            Target target = new Target() //CREATES A NEW TARGET OBJECT TO BE USED BY PICASSO
            {
                @Override
                public void onPrepareLoad(Drawable arg0) {}
                @Override
                public void onBitmapFailed(Drawable arg0) {}
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
                    if(getSupportActionBar() != null)
                        getSupportActionBar().setIcon(new BitmapDrawable(getResources(), bitmap));
                }
            };
            Picasso.with(getApplicationContext()).load(file).transform(new CircleTransform()).into(target);
        }

    }
}
