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

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class ChatAdapter extends ArrayAdapter<MyChat> {
    private ArrayList<MyChat> users = new ArrayList<>();
    Context context;
    String TAG = "myshit";
    ImageView pictureImgView;

    public ChatAdapter(Context context, ArrayList<MyChat> users) {
        super(context, 0, users);
        this.context = context;
        this.users = users;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyChat chat = getItem(position); //GET CHAT

        //IF THE CONVERT VIEW IS NULL, CREATE A NEW ONE
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_chat, parent, false);
        }

        // ASSIGNING VARIABLES
        pictureImgView = (ImageView) convertView.findViewById(R.id.pictureImgView);
        TextView nameTxtView = (TextView) convertView.findViewById(R.id.nameTxtView);
        TextView messageTxtView = (TextView) convertView.findViewById(R.id.messageTxtView);

        //SETTINGS THE USER INFORMATION
        nameTxtView.setText(chat.getMessage());
        messageTxtView.setText(chat.getName());
        setUserPicture(chat);

        //ON CLICK LISTENER FOR THE USER, OPENS CONVERSATION ACTIVITY
        convertView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(context, ConversationActivity.class); //CREATE NEW INTENT
                i.putExtra("user", users.get(position).getName()); //EXTRA USER NAME. EX: GUILHERME NOBRE
                i.putExtra("id", users.get(position).getId()); //EXTRA USER ID. EX: 1239210832293
                context.startActivity(i); //START CONVERSATION ACTIVITY
            }
        });

        return convertView; //RETURN THE VIEW
    }


    public void setUserPicture(MyChat chat)
    {
        String filePicture = chat.getId() + "_picture";
        final File file = new File(context.getFilesDir(), filePicture);

        if(file.exists())
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
        else
        {
            loadPicture(chat, file);
        }
    }

    public void loadPicture(final MyChat chat, final File file)
    {
        if (chat.getPicture() != null)
        {
            if (!chat.getPicture().equals(""))
            {
                Target target = new Target()
                {
                    @Override
                    public void onPrepareLoad(Drawable arg0) {
                    }

                    @Override
                    public void onBitmapFailed(Drawable arg0) {
                    }

                    @Override //SET IMAGE BITMAP TO IMAGEVIEW AND STORE IMAGE ON INTERNAL MEMORY
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
                        pictureImgView.setImageBitmap(bitmap);
                        try {
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
                Picasso.with(context).load(chat.getPicture()).transform(new CircleTransform()).into(target);
            }
        }
    }
}
