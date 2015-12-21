package gooeyn.bored;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    ArrayList<People> people = new ArrayList<>();
    PeopleAdapter adapter;
    String TAG = "myshit";
    Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("myshit", "ONCREATE CHAT FRAGMENT");
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        final ListView chatLV = (ListView) view.findViewById(R.id.chatLV);
        activity = getActivity();
        adapter = new PeopleAdapter(getContext(), people);
        chatLV.setAdapter(adapter);

        ChatManager chatManager = ChatManager.getInstanceFor(MyConnectionManager.getInstance().getConnection());

        chatManager.addChatListener(
                new ChatManagerListener() {
                    @Override
                    public void chatCreated(Chat chat, boolean createdLocally)
                    {
                        if (!createdLocally)
                            chat.addMessageListener(new ChatMessageListener() {
                                @Override
                                public void processMessage(org.jivesoftware.smack.chat.Chat chat, Message message) {
                                    Log.v(TAG, chat.getParticipant() + ". m: " + message.getBody());
                                    if (message.getBody() != null) {
                                        Log.d(TAG, "HEY NEW FCKING MEEEESSAGE: " + message.getFrom());
                                        for (People d : people) {
                                            if (d.getName().equals(message.getFrom()))
                                            {
                                                d.setProfile(message.getBody());
                                            }
                                        }
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                adapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }
                            });
                    }
                });

        chatManager.addChatListener(new ChatManagerListener()
        {
            @Override
            public void chatCreated(Chat chat, boolean createdLocally)
            {
                chat.addMessageListener(new ChatMessageListener()
                {
                    @Override
                    public void processMessage(Chat chat, Message message)
                    {
                        if (message.getBody() != null)
                        {
                            Log.e(TAG, "message body: " + message.getBody());
                            people.add(new People(message.getBody(), "oi", "oi"));

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run()
                                {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
            }
        });
        return view;
    }
}