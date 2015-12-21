package gooeyn.bored;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PeopleAdapter extends ArrayAdapter<People> {
    private ArrayList<People> events_list = new ArrayList<>();
    Context context;

    public PeopleAdapter(Context context, ArrayList<People> users) {
        super(context, 0, users);
        this.context = context;
        this.events_list = users;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        People user = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.people_list, parent, false);
        }
        ImageView profileImgView = (ImageView) convertView.findViewById(R.id.profileImgView);
        TextView tvName = (TextView) convertView.findViewById(R.id.name);
        tvName.setText(user.name);

        if(user.profile != null)
            if(!user.profile.equals(""))
                Picasso.with(context).load(user.profile).transform(new CircleTransform()).into(profileImgView);

        convertView.setOnClickListener(new View.OnClickListener()
        {
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
