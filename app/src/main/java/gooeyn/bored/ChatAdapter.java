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

public class ChatAdapter extends ArrayAdapter<MyChat> {
    private ArrayList<MyChat> events_list = new ArrayList<>();
    Context context;

    public ChatAdapter(Context context, ArrayList<MyChat> users) {
        super(context, 0, users);
        this.context = context;
        this.events_list = users;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyChat user = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_list, parent, false);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.textMessage);
        tvName.setText(user.name);

        convertView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You Clicked " + events_list.get(position).name, Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
