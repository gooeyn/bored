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
        ArrayList<People> people = new ArrayList<>(); //EVENT ARRAYLIST
        events_list = (ListView) view.findViewById(R.id.listView);
        events_list.setAdapter(new PeopleAdapter(getActivity(), people)); //LOAD EVENTS ARRAYLIST TO LIST VIEW

        return view;
    }
}
