package gooeyn.bored;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class ChatFragment extends Fragment {
    ListView events_list;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ArrayList<MyChat> events = new ArrayList<>(); //EVENT ARRAYLIST

        //ADD VALUES TO EVENT ARRAY LIST
        events.add(new MyChat("Guigo"));
        events.add(new MyChat("Wagnao"));
        events.add(new MyChat("Caique"));
        events.add(new MyChat("Lucas"));
        events.add(new MyChat("Luci"));
        events.add(new MyChat("Giulia"));
        events.add(new MyChat("Guigo"));
        events.add(new MyChat("Wagnao"));
        events.add(new MyChat("Caique"));
        events.add(new MyChat("Lucas"));
        events.add(new MyChat("Luci"));
        events.add(new MyChat("Giulia"));
        events.add(new MyChat("Guigo"));
        events.add(new MyChat("Wagnao"));
        events.add(new MyChat("Caique"));
        events.add(new MyChat("Lucas"));
        events.add(new MyChat("Luci"));
        events.add(new MyChat("Giulia"));
        events.add(new MyChat("Guigo"));
        events.add(new MyChat("Wagnao"));
        events.add(new MyChat("Caique"));
        events.add(new MyChat("Lucas"));
        events.add(new MyChat("Luci"));
        events.add(new MyChat("Giulia"));
        events_list = (ListView) view.findViewById(R.id.listView2);
        events_list.setAdapter(new ChatAdapter(getActivity(), events)); //LOAD EVENTS ARRAYLIST TO LIST VIEW

        return view;
    }
}
