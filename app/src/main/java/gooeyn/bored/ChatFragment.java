package gooeyn.bored;

import android.app.Activity;
import android.content.Context;
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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class ChatFragment extends Fragment {

    ArrayList<MyChat> people = new ArrayList<>();
    ChatAdapter adapter;
    String TAG = "myshit";
    Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("myshit", "ONCREATE CHAT FRAGMENT");
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        final ListView chatLV = (ListView) view.findViewById(R.id.chatLV);
        activity = getActivity();
        adapter = new ChatAdapter(getContext(), people);
        chatLV.setAdapter(adapter);

        ChatManager chatManager = ChatManager.getInstanceFor(MyConnectionManager.getInstance().getConnection());

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
                            String from = message.getFrom();
                            Log.e(TAG, "MESSAGE FROM: " + from);
                            String id = from.substring(0, from.indexOf("@"));

                            String newMessage = message.getBody();
                            String chatUser = id + "_messages";

                            boolean isNewChat = true;

                            for (MyChat person : people) {
                                if(person.getId().equals(id))
                                {
                                    try
                                    {
                                        person.setStatus(message.getBody());
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                adapter.notifyDataSetChanged();
                                            }
                                        });

                                        FileInputStream fis2 = getContext().openFileInput(chatUser);
                                        StringBuilder builder = new StringBuilder();
                                        int ch;
                                        while((ch = fis2.read()) != -1){
                                            builder.append((char)ch);
                                        }
                                        Log.e(TAG, builder.toString());
                                        String currentMessages = builder.toString();
                                        fis2.close();

                                        currentMessages += newMessage;

                                        try
                                        {
                                            FileOutputStream fos2 = getContext().openFileOutput(chatUser, Context.MODE_PRIVATE);
                                            fos2.write(currentMessages.getBytes());
                                            fos2.close();
                                        }
                                        catch (Exception e)
                                        {
                                            Log.e(TAG, "EXCEPTIO WRITING (NEW CHAT): " + e.toString());
                                        }
                                    }
                                    catch (Exception e)
                                    {
                                        Log.e(TAG, "EXCEPTION: " + e.toString());
                                    }
                                    isNewChat = false;
                                }
                            }

                            if(isNewChat)
                            {
                                String filenameUser = id + "_username";
                                StringBuilder builder = new StringBuilder();
                                MyChat newChat = new MyChat("", "", id, message.getBody());
                                try
                                {
                                    FileInputStream fis2 = getContext().openFileInput(filenameUser);
                                    int ch;
                                    while((ch = fis2.read()) != -1){
                                        builder.append((char)ch);
                                    }
                                    Log.e(TAG, "THE NAME: " + builder.toString());
                                    fis2.close();

                                    FileOutputStream fos2 = getContext().openFileOutput(chatUser, Context.MODE_PRIVATE);
                                    fos2.write(newMessage.getBytes());
                                    fos2.close();
                                }
                                catch (Exception e)
                                {
                                    Log.e(TAG, "EXCEPTIO WRITING (NEW CHAT): " + e.toString());
                                }
                                newChat.setName(builder.toString());
                                people.add(newChat);
                            }

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