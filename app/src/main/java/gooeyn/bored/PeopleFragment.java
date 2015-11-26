package gooeyn.bored;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;
import java.util.Collection;

public class PeopleFragment extends Fragment {
    ListView events_list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people, container, false);
        ArrayList<People> people = new ArrayList<>(); //EVENT ARRAYLIST
        AbstractXMPPConnection connection = SingletonConnection.getInstance().getConnection();
        Roster roster = Roster.getInstanceFor(connection);
        try {
            if (!roster.isLoaded())
                roster.reloadAndWait();
        } catch(Exception e)
        {
            Log.e("conectacaralho", "reload");
        }

        Collection<RosterEntry> entries = roster.getEntries();
        Log.e("conectacaralho", "vazio: " + entries.isEmpty());
        for (RosterEntry entry : entries) {
            people.add(new People(entry.getUser()));
            Log.e("conectacaralho", "fragment" + entry.getUser());
        }
        events_list = (ListView) view.findViewById(R.id.listView);
        events_list.setAdapter(new PeopleAdapter(getActivity(), people)); //LOAD EVENTS ARRAYLIST TO LIST VIEW

        return view;
    }
}
