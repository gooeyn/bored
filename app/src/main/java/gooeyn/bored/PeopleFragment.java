package gooeyn.bored;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class PeopleFragment extends Fragment {
    ListView events_list;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people, container, false);
        ArrayList<People> events = new ArrayList<>(); //EVENT ARRAYLIST

        //ADD VALUES TO EVENT ARRAY LIST
        events.add(new People("Guigo"));
        events.add(new People("Wagnao"));
        events.add(new People("Caique"));
        events.add(new People("Lucas"));
        events.add(new People("Luci"));
        events.add(new People("Giulia"));
        events.add(new People("Guigo"));
        events.add(new People("Wagnao"));
        events.add(new People("Caique"));
        events.add(new People("Lucas"));
        events.add(new People("Luci"));
        events.add(new People("Giulia"));
        events.add(new People("Guigo"));
        events.add(new People("Wagnao"));
        events.add(new People("Caique"));
        events.add(new People("Lucas"));
        events.add(new People("Luci"));
        events.add(new People("Giulia"));
        events.add(new People("Guigo"));
        events.add(new People("Wagnao"));
        events.add(new People("Caique"));
        events.add(new People("Lucas"));
        events.add(new People("Luci"));
        events.add(new People("Giulia"));
        events_list = (ListView) view.findViewById(R.id.listView);
        events_list.setAdapter(new PeopleAdapter(getActivity(), events)); //LOAD EVENTS ARRAYLIST TO LIST VIEW

        return view;
    }
}
