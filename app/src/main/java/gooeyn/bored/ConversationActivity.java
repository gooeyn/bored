package gooeyn.bored;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat.*;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;

import java.io.FileInputStream;
import java.util.ArrayList;

public class ConversationActivity extends AppCompatActivity {
    ArrayList<MyMessage> myChat = new ArrayList<>();
    String TAG = "myshit";
    MessagesAdapter adapter;
    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        final AbstractXMPPConnection connection = MyConnectionManager.getInstance().getConnection();
        Button sendButton = (Button) findViewById(R.id.buttonSend);
        ListView listview = (ListView) this.findViewById(R.id.listMessages);
        final EditText sendText = (EditText) findViewById(R.id.editTextSend);
        Intent intent = getIntent();

        final String user = intent.getStringExtra("user");
        setTitle(user);

        final String id = intent.getStringExtra("id");

        String chatUser = id + "_messages";
        String currentMessages = "";

        try {
            FileInputStream fis2 = getApplicationContext().openFileInput(chatUser);
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

        myChat.add(new MyMessage(currentMessages));


        activity = ConversationActivity.this;
        adapter = new MessagesAdapter(getApplicationContext(), myChat);
        listview.setAdapter(adapter);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = sendText.getText().toString();
                Log.e(TAG, "string text: " + text);
                sendText.setText("");
                Log.e(TAG, "MESSAGE TO: " + id + "@54.84.237.97");
                Message m = new Message(id + "@54.84.237.97", Message.Type.chat);
                m.setFrom(connection.getUser());
                m.setBody(text);
                Log.e(TAG, "message body: " + m.getBody());
                try {
                    connection.sendStanza(m);
                    myChat.add(new MyMessage(text));

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        });

        ChatManager chatmanager = ChatManager.getInstanceFor(connection);
        chatmanager.addChatListener(new ChatManagerListener()
        {
            @Override
            public void chatCreated(Chat chat, boolean createdLocally)
            {
                chat.addMessageListener(new ChatMessageListener()
                {
                    @Override
                    public void processMessage(Chat chat, Message message)
                    {
                        if (message.getBody() != null)
                        {
                            Log.e(TAG, "message body: " + message.getBody());
                            myChat.add(new MyMessage(message.getBody()));

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run()
                                {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}
