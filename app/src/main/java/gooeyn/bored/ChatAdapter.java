package gooeyn.bored;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ChatAdapter extends BaseAdapter {
    private static LayoutInflater inflater=null;
    private ArrayList<MyChat> events_list = new ArrayList<>();
    Context context;

    public ChatAdapter(Context context, ArrayList<MyChat> events_list)
    {
        this.events_list = events_list;
        this.context = context;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return events_list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView tv;
        ImageView img;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView;
        if(convertView == null)
        {
            rowView = inflater.inflate(R.layout.chat_list, parent, false);
            holder.tv = (TextView) rowView.findViewById(R.id.textView1);
            holder.img = (ImageView) rowView.findViewById(R.id.imageView1);
            holder.tv.setText(events_list.get(position).name);

            rowView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "You Clicked " + events_list.get(position).name, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(context, ConversationActivity.class);
                    i.putExtra("user", events_list.get(position).name);
                    context.startActivity(i);
                }
            });
        }
        else
        {
            return convertView;
        }
        return rowView;
    }
}
