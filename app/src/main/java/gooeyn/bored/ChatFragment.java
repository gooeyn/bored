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
        ArrayList<Chat> events = new ArrayList<>(); //EVENT ARRAYLIST

        //ADD VALUES TO EVENT ARRAY LIST
        events.add(new Chat("Guigo"));
        events.add(new Chat("Wagnao"));
        events.add(new Chat("Caique"));
        events.add(new Chat("Lucas"));
        events.add(new Chat("Luci"));
        events.add(new Chat("Giulia"));
        events.add(new Chat("Guigo"));
        events.add(new Chat("Wagnao"));
        events.add(new Chat("Caique"));
        events.add(new Chat("Lucas"));
        events.add(new Chat("Luci"));
        events.add(new Chat("Giulia"));
        events.add(new Chat("Guigo"));
        events.add(new Chat("Wagnao"));
        events.add(new Chat("Caique"));
        events.add(new Chat("Lucas"));
        events.add(new Chat("Luci"));
        events.add(new Chat("Giulia"));
        events.add(new Chat("Guigo"));
        events.add(new Chat("Wagnao"));
        events.add(new Chat("Caique"));
        events.add(new Chat("Lucas"));
        events.add(new Chat("Luci"));
        events.add(new Chat("Giulia"));
        events_list = (ListView) view.findViewById(R.id.listView2);
        events_list.setAdapter(new ChatAdapter(getActivity(), events)); //LOAD EVENTS ARRAYLIST TO LIST VIEW

        return view;
    }
}
