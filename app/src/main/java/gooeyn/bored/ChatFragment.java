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
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        //ASSIGN VARIABLES
        final ListView chatLV = (ListView) view.findViewById(R.id.chatLV);

        activity = getActivity();
        adapter = new ChatAdapter(getContext(), people);
        chatLV.setAdapter(adapter);

        //GET CHAT MANAGER FROM CONNECTION AND ADD LISTENER
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
                        if (message.getBody() != null) //IF MESSAGE BODY IS NOT NULL
                        {
                            //ASSIGN VARIABLES
                            String from = message.getFrom();
                            String id = from.substring(0, from.indexOf("@"));
                            String newMessage = message.getBody();
                            String fileMessages = id + "_messages";
                            String fileUser = id + "_username";
                            boolean isNewChat = true;

                            // RUNS TROUGH ALL THE CHATS
                            for (MyChat person : people)
                            {
                                if(person.getId().equals(id)) //IF FIND A CHAT WITH THE SAME ID AS THE NEW MESSAGE
                                {
                                    isNewChat = false; //IT'S NOT A NEW CHAT
                                    try
                                    {
                                        person.setMessage(newMessage); //SET MESSAGE TO PERSON'S
                                        updateMessages(newMessage, fileMessages); //UPDATE MESSAGES
                                    }
                                    catch (Exception e)
                                    {
                                        Log.e(TAG, "EXCEPTION: " + e.toString());
                                    }
                                }
                            }

                            if(isNewChat) //IF IS A NEW CHAT
                            {
                                createNewChat(newMessage, fileMessages, fileUser, id); //CREATE A NEW CHAT
                            }

                            //UPDATE DATA SET ON UI THREAD
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

    public void updateMessages(String newMessage, String fileMessages)
    {
        //GET CURRENT MESSAGES
        String oldMessages = "";
        try
        {
            FileInputStream fis2 = getContext().openFileInput(fileMessages);
            StringBuilder builder = new StringBuilder();
            int ch;
            while((ch = fis2.read()) != -1){
                builder.append((char)ch);
            }
            oldMessages = builder.toString();
            fis2.close();

            oldMessages += newMessage;
        }
        catch (Exception e)
        {
            Log.e(TAG, "EXCEPTION GETTING OLD MESSAGES: " + e.toString());
        }

        //ADD NEW MESSAGE TO CURRENT MESSAGES
        try
        {
            FileOutputStream fos2 = getContext().openFileOutput(fileMessages, Context.MODE_PRIVATE);
            fos2.write(oldMessages.getBytes());
            fos2.close();
        }
        catch (Exception e)
        {
            Log.e(TAG, "EXCEPTION ADDING NEW MESSAGE TO OLD MESSAGES (NEW CHAT): " + e.toString());
        }
    }

    public void createNewChat(String newMessage, String fileMessages, String fileUser, String id)
    {
        StringBuilder builder = new StringBuilder();
        MyChat newChat = new MyChat("", "", id, newMessage);
        try
        {
            //GET USER NAME
            FileInputStream fis2 = getContext().openFileInput(fileUser);
            int ch;
            while((ch = fis2.read()) != -1){
                builder.append((char)ch);
            }
            fis2.close();

            //CREATE NEW FILE MESSAGES
            FileOutputStream fos2 = getContext().openFileOutput(fileMessages, Context.MODE_PRIVATE);
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
}