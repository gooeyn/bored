package gooeyn.bored;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;

public class ConversationActivity extends AppCompatActivity {
    private ListView listview;
    ArrayList<People> people = new ArrayList<>();
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
        Log.e("chatgooeyn", user);
        listview.setAdapter(new ConversationAdapter(getApplicationContext(), people));
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
                people.add(new People(text));
                listview.invalidateViews();
            }
        });
    }
}
