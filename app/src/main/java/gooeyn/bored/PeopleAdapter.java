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
    String TAG = "myshit/PeopleAdapter";
    ImageView pictureImgView;

    //Files
    String messagesFile = "messages_";
    String pictureFile = "picture_";
    String nameFile = "name_";
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

        //DECLARING AND ASSIGNINGVARIABLES
        pictureImgView = (ImageView) convertView.findViewById(R.id.pictureImgView);
        TextView tvName = (TextView) convertView.findViewById(R.id.name);
        TextView tvStatus = (TextView) convertView.findViewById(R.id.status);

        //SETING VALUES TO VARIABLES
        tvStatus.setText(user.getStatus());
        tvName.setText(user.getName());
        setUserPicture(user);

        //ON CLICK LISTENER FOR THE USER, OPENS CONVERSATION ACTIVITY
        convertView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You Clicked " + events_list.get(position).getName(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(context, ConversationActivity.class);
                i.putExtra("user", events_list.get(position).getName());
                i.putExtra("id", events_list.get(position).getId());
                context.startActivity(i);
            }
        });

        return convertView;
    }

    //SET USER PICTURE TO USER IMAGE VIEW
    public void setUserPicture(People user)
    {
        final File file = new File(context.getFilesDir(), pictureFile + user.getId()); //CREATES/GETS FILE USING FILENAME
        if(file.exists()) //IF THE FILE EXISTS LOAD IT TO IMAGE VIEW USING PICASSO
        {
            try
            {
                Log.v(TAG, "Image loaded successfully: " + file.getName());
                Picasso.with(context).load(file).transform(new CircleTransform()).into(pictureImgView);
            }
            catch (Exception e)
            {
                Log.e(TAG, "Error loading the image: " + e.toString());
            }
        }
        else //IF THE FILE DOES NOT EXIST, LOAD PICTURE URL
        {
            loadPicture(user, file);
        }
    }

    //LOAD PICTURE FROM URL
    public void loadPicture(final People user, final File file)
    {
        if (user.getPicture() != null) //IF LINK IS NOT NULL
        {
            if (!user.getPicture().equals("")) //IF LINK IS NOT EMPTY
            {
                Target target = new Target() //CREATES A NEW TARGET OBJECT TO BE USED BY PICASSO
                {
                    @Override
                    public void onPrepareLoad(Drawable arg0) {}
                    @Override
                    public void onBitmapFailed(Drawable arg0) {}
                    @Override //SET IMAGE BITMAP TO IMAGE VIEW AND STORE IMAGE ON INTERNAL MEMORY
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
                        pictureImgView.setImageBitmap(bitmap); //SET IMAGE TO IMAGE VIEW
                        try { //STORE IMAGE TO FILE
                            FileOutputStream fos = context.openFileOutput(file.getName(), Context.MODE_PRIVATE);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.flush();
                            fos.close();
                            Log.v(TAG, "Image stored successfully: " + file.getName());
                        } catch (Exception e) {
                            Log.e(TAG, "Error storing the image: " + e.toString());
                        }
                    }
                };
                //LOAD IMAGE URL USING PICASSO
                Picasso.with(context).load(user.getPicture()).transform(new CircleTransform()).into(target);
            }
        }
    }
}
