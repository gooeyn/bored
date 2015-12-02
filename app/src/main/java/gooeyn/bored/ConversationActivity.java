package gooeyn.bored;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat.*;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;

public class ConversationActivity extends AppCompatActivity {
    private ListView listview;
    ArrayList<MyChat> myChat = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        final AbstractXMPPConnection connection = MyConnectionManager.getInstance().getConnection();
        Button sendButton = (Button) findViewById(R.id.buttonSend);
        listview = (ListView) this.findViewById(R.id.listMessages);
        final EditText sendText = (EditText) findViewById(R.id.editTextSend);
        Intent intent = getIntent();
        final String user = intent.getStringExtra("user");
        setTitle(user);
        listview.setAdapter(new ChatAdapter(getApplicationContext(), myChat));
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = sendText.getText().toString();
                sendText.setText("");
                Message message = new Message(user, Message.Type.chat);
                message.setFrom(connection.getUser());
                message.setBody(text);
                try {
                    connection.sendStanza(message);
                } catch(Exception e)
                {
                    Log.e("conectacaralho", e.toString());
                }




                ChatManager chatmanager = ChatManager.getInstanceFor(connection);
                chatmanager.addChatListener(new ChatManagerListener() {
                    @Override
                    public void chatCreated(Chat chat, boolean createdLocally) {
                        chat.addMessageListener(new ChatMessageListener() {
                            @Override
                            public void processMessage(Chat chat, Message message) {
                                Log.e("recebenu", chat.getParticipant() + ". m: " + message.getBody());
                                if (message.getBody() != null) {
                                    Log.e("recebenu", "eba");
                                    addItem(message.getBody());
                                }
                            }
                        });
                    }
                });


                myChat.add(new MyChat(text));
                listview.invalidateViews();
            }
        });
    }

    public void addItem(String message){
        Log.e("conectacaralho", "no additem");
        Log.e("conectacaralho", "n: " + myChat.size());
        myChat.add(new MyChat(message));
        Log.e("conectacaralho", "n: " + myChat.size());
        ((BaseAdapter) listview.getAdapter()).notifyDataSetChanged();
    }
}
