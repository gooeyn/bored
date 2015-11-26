package gooeyn.bored;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

public class MenuFragment extends Fragment {
    private ImageView androidView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        Button btn = (Button) view.findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                Toast.makeText(getActivity(), "Hello World", Toast.LENGTH_LONG).show();

        }
        });


        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.nav_header_abc, null);
        androidView = (ImageView) headerView.findViewById(R.id.androidView);

        // You can set a temporary background here
        //image.setImageResource(null);
        final TextView androidText = (TextView) headerView.findViewById(R.id.androidText);
        AccessToken accessToken = AccessToken.getCurrentAccessToken(); // get current access token
        GraphRequest request = GraphRequest.newMeRequest( //make graph request for facebook data
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() { //callback from graph request
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) { //on completed request
                        try {
                            androidText.setText(object.getString("name"));
                            JSONObject pic = object.getJSONObject("picture");
                            JSONObject data = pic.getJSONObject("data");

                            JSONObject cover = object.getJSONObject("cover");
                            //JSONObject source = cover.getJSONObject("source");
                            new DownloadImage().execute(data.getString("url"));
                            //new DownloadImageSource().execute(cover.getString("source"));
                        } catch (JSONException e) {
                            Log.d("loginapp", e.toString());
                            Log.d("loginapp", object.toString());
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "picture.type(large),name,cover");
        request.setParameters(parameters);
        request.executeAsync();

        return view;
    }

    private void setImage(Drawable drawable)
    {
        //mImageView.setBackgroundDrawable(drawable);
        androidView.setBackgroundDrawable(drawable);
    }

    public class DownloadImage extends AsyncTask<String, Integer, Drawable> {

        @Override
        protected Drawable doInBackground(String... arg0) {
            // This is done in a background thread
            return downloadImage(arg0[0]);
        }
        /**
         * Called after the image has been downloaded
         * -> this calls a function on the main thread again
         */
        protected void onPostExecute(Drawable image)
        {
            setImage(image);
        }
        private Drawable downloadImage(String _url)
        {
            URL url;
            InputStream in;
            BufferedInputStream buf;

            try {
                url = new URL(_url);
                in = url.openStream();
                buf = new BufferedInputStream(in);
                Bitmap bMap = BitmapFactory.decodeStream(buf);
                in.close();
                buf.close();

                return new BitmapDrawable(bMap);

            } catch (Exception e) {
                Log.e("Error reading file", e.toString());
            }

            return null;
        }

    }
}