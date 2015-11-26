package gooeyn.bored;

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
        final AbstractXMPPConnection connection = SingletonConnection.getInstance().getConnection();
        Button sendButton = (Button) findViewById(R.id.buttonSend);
        listview = (ListView) this.findViewById(R.id.listMessages);
        final EditText sendText = (EditText) findViewById(R.id.editTextSend);
        listview.setAdapter(new ConversationAdapter(getApplicationContext(), people));
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = sendText.getText().toString();
                sendText.setText("");
                Message message = new Message("b@ec2-54-84-237-97.compute-1.amazonaws.com", Message.Type.chat);
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
