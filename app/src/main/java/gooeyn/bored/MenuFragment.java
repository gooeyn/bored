package gooeyn.bored;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

public class MenuFragment extends Fragment {
    TextView profileTxtView;
    TextView statusTxtView;
    ImageView profileImgView;
    Context context;
    String TAG = "myshit";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("myshit", "ONCREATE MENU FRAGMENT");
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        context = getContext();
        profileTxtView = (TextView) view.findViewById(R.id.profileTxtView);
        profileImgView = (ImageView) view.findViewById(R.id.profileImgView);
        statusTxtView = (TextView) view.findViewById(R.id.statusTextView);
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
                alertDialog.setTitle("What do you want to do?");
                alertDialog.setMessage("Hey, tell your friends what you want to do: ");
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

                alertDialog.show();
            }
        });
        getFacebookData();
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
                            String URL = object.getJSONObject("picture").getJSONObject("data").getString("url");
                            Picasso.with(context).load(URL).transform(new CircleTransform()).into(profileImgView);
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