package gooeyn.bored;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MenuFragment extends Fragment {
    TextView profileTxtView;
    TextView statusTxtView;
    ImageView profileImgView;
    Context context;
    String TAG = "myshit";
    String FILENAME = "profilepic9";
    String filenameUser = "usernameprofile";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("myshit", "ONCREATE MENU FRAGMENT");
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        context = getContext();
        profileTxtView = (TextView) view.findViewById(R.id.profileTxtView);
        profileImgView = (ImageView) view.findViewById(R.id.profileImgView);
        statusTxtView = (TextView) view.findViewById(R.id.statusTextView);

        statusTxtView.setText(MyConnectionManager.getInstance().status);
        Button logoutButton = (Button) view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyConnectionManager.getInstance().notBored();
                LoginManager.getInstance().logOut();
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });

        statusTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Click here to set your status message");
                alertDialog.setMessage("By setting a message you automatically get BORED");
                final EditText input = new EditText(getActivity());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);

                alertDialog.setPositiveButton("Done!",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MyConnectionManager.getInstance().setStatus(input.getText().toString());
                                statusTxtView.setText(input.getText().toString());
                            }
                        });
                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                alertDialog.show();
            }
        });

        File file = new File(context.getFilesDir(), FILENAME);
        try
        {
            FileInputStream fis = getContext().openFileInput(file.getName());
            fis.close();

            Log.e(TAG, "READ THE IMAGE SUCESSFULLY");
            Log.e(TAG, file.getName());
            Log.e(TAG, file.getPath());
            Picasso.with(context).load(file).transform(new CircleTransform()).into(profileImgView);

            FileInputStream fis2 = getContext().openFileInput(filenameUser);
            StringBuilder builder = new StringBuilder();
            int ch;
            while((ch = fis2.read()) != -1){
                builder.append((char)ch);
            }
            Log.e(TAG, builder.toString());
            profileTxtView.setText(builder.toString());
            fis2.close();
        } catch (Exception e)
        {
            getFacebookData();
            Log.e(TAG, "Error getting the image: " + e.toString());
        }
        return view;
    }


    public void getFacebookData()
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken(); // get current access token

        GraphRequest request = GraphRequest.newMeRequest(accessToken,
                new GraphRequest.GraphJSONObjectCallback()
                {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response)
                    {
                        try
                        {
                            Log.e(TAG, "COMPLETED FACEBOOK");
                            String URL = object.getJSONObject("picture").getJSONObject("data").getString("url");

                            final String string = object.getString("name");

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
                                    File file = new File(context.getFilesDir(), FILENAME);
                                    try
                                    {
                                        FileOutputStream fos = getContext().openFileOutput(file.getName(), Context.MODE_PRIVATE);
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                        fos.flush();
                                        fos.close();

                                        FileOutputStream fos2 = getContext().openFileOutput(filenameUser, Context.MODE_PRIVATE);
                                        fos2.write(string.getBytes());
                                        fos2.close();
                                        Log.e(TAG, "Profile image stored");
                                    } catch (Exception e)
                                    {
                                        Log.e(TAG, "Error storing the image: " + e.toString());
                                    }
                                }
                            };
                            Picasso.with(context).load(URL).transform(new CircleTransform()).into(target);
                            profileTxtView.setText(object.getString("name"));
                        }
                        catch (JSONException e)
                        {
                            Log.e(TAG, "Error on completed Graph request: " + e.toString());
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "picture.type(large),name");
        request.setParameters(parameters);
        request.executeAsync();
    }
}