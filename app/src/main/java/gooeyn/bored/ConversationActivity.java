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

import java.util.ArrayList;

public class ConversationActivity extends AppCompatActivity {
    ArrayList<MyChat> myChat = new ArrayList<>();
    String TAG = "myshit";
    ChatAdapter adapter;
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
        adapter = new ChatAdapter(getApplicationContext(), myChat);
        listview.setAdapter(adapter);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = sendText.getText().toString();
                sendText.setText("");
                Message m = new Message(user, Message.Type.chat);
                m.setFrom(connection.getUser());
                m.setBody(text);
                try {
                    connection.sendStanza(m);
                    myChat.add(new MyChat(text));
                    Activity activity = ConversationActivity.this;
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
                            myChat.add(new MyChat(message.getBody()));
                            Activity activity = ConversationActivity.this;
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
