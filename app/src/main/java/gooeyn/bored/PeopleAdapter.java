package gooeyn.bored;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class PeopleAdapter extends ArrayAdapter<People> {
    private ArrayList<People> events_list = new ArrayList<>();
    Context context;
    String TAG = "myshit";

    public PeopleAdapter(Context context, ArrayList<People> users) {
        super(context, 0, users);
        this.context = context;
        this.events_list = users;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        People user = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_user, parent, false);
        }
        final ImageView profileImgView = (ImageView) convertView.findViewById(R.id.profileImgView);
        TextView tvName = (TextView) convertView.findViewById(R.id.name);
        TextView tvStatus = (TextView) convertView.findViewById(R.id.status);

        tvStatus.setText(user.status);
        tvName.setText(user.name);

        String FILENAME = user.getId() + "_picture";
        final File file = new File(context.getFilesDir(), FILENAME);


        if(file.exists())
        {
            try
            {
                Log.e(TAG, "READ THE IMAGE SUCESSFULLY");
                Picasso.with(context).load(file).transform(new CircleTransform()).into(profileImgView);
            }
            catch (Exception e)
            {
                Log.e(TAG, "Error getting the image: " + e.toString());
            }
        }
        else
        {
            if(user.profile != null)
                if(!user.profile.equals(""))
                {
                    Target target = new Target()
                    {
                        @Override
                        public void onPrepareLoad(Drawable arg0) {
                        }
                        @Override
                        public void onBitmapFailed(Drawable arg0) {
                        }
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
                            profileImgView.setImageBitmap(bitmap);
                            try
                            {
                                FileOutputStream fos = getContext().openFileOutput(file.getName(), Context.MODE_PRIVATE);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                fos.flush();
                                fos.close();

                                Log.e(TAG, "Image stored");

                            } catch (Exception e)
                            {
                                Log.e(TAG, "Error storing the image: " + e.toString());
                            }
                        }
                    };

                    Picasso.with(context).load(user.profile).transform(new CircleTransform()).into(target);
                }
        }



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
