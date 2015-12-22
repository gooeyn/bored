package gooeyn.bored;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MessagesAdapter extends ArrayAdapter<MyMessage> {
    private ArrayList<MyMessage> events_list = new ArrayList<>();
    Context context;

    public MessagesAdapter(Context context, ArrayList<MyMessage> users) {
        super(context, 0, users);
        this.context = context;
        this.events_list = users;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyMessage message = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.messages_list, parent, false);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.textMessage);
        tvName.setText(message.name);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You Clicked " + events_list.get(position).name, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(context, ConversationActivity.class);
                i.putExtra("user", events_list.get(position).name);
                context.startActivity(i);
            }
        });

        return convertView;
    }
}
