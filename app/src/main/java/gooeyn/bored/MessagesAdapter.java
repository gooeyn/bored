package gooeyn.bored;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MessagesAdapter extends ArrayAdapter<MyMessage> {
    private ArrayList<MyMessage> messages = new ArrayList<>();
    Context context;

    public MessagesAdapter(Context context, ArrayList<MyMessage> messages) {
        super(context, 0, messages);
        this.context = context;
        this.messages = messages;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MyMessage message = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_message, parent, false);
        }
        TextView tvName = (TextView) convertView.findViewById(R.id.textMessage);
        tvName.setText(message.name);
        if(message.isFromMe)
        {
            convertView.setBackgroundColor(Color.RED);
            tvName.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        }
        else
        {
            convertView.setBackgroundColor(Color.BLUE);
            tvName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You Clicked " + messages.get(position).name, Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
