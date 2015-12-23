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

    ArrayList<MyChat> chats = new ArrayList<>();
    ChatAdapter adapter;
    String TAG = "myshit";
    Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        //DECLARING AND ASSIGNING VARIABLES
        final ListView chatListView = (ListView) view.findViewById(R.id.chatLV);
        activity = getActivity();
        adapter = new ChatAdapter(getContext(), chats);
        chatListView.setAdapter(adapter);

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
                            //DECLARING AND ASSIGNING VARIABLES
                            String from = message.getFrom();
                            String id = from.substring(0, from.indexOf("@"));
                            String newMessage = message.getBody();
                            String fileMessages = id + "_messages";
                            String fileUser = id + "_username";
                            boolean isNewChat = true;

                            // RUNS TROUGH ALL THE CHATS
                            for (MyChat currentChat : chats)
                            {
                                if(currentChat.getId().equals(id)) //IF FIND A CHAT WITH THE SAME ID AS THE NEW MESSAGE
                                {
                                    isNewChat = false; //IT'S NOT A NEW CHAT
                                    currentChat.setMessage(newMessage); //SET MESSAGE TO CHAT
                                    updateMessages(newMessage, fileMessages); //UPDATE MESSAGES
                                }
                            }

                            if(isNewChat) //IF IS A NEW CHAT CREATE NEW CHAT
                                createNewChat(newMessage, fileMessages, fileUser, id);


                            notifyDataSetChanged(); //UPDATE DATA SET ON UI THREAD
                        }
                    }
                });
            }
        });
        return view;
    }

    //UPDATES THE CHAT MESSAGES
    public void updateMessages(String newMessage, String fileMessages)
    {
        String messages = readFile(fileMessages) + newMessage; //CREATE STRING WITH OLD MESSAGES + NEW MESSAGE
        createFile(fileMessages, messages); //UPDATES FILE WITH OLD + CURRENT MESSAGES
    }

    //CREATES A NEW CHAT, SETTING THE CHAT NAME AND CREATING A STORAGE FILE
    public void createNewChat(String newMessage, String fileMessages, String fileUser, String id)
    {
        MyChat newChat = new MyChat("", "", id, newMessage);
        newChat.setName(readFile(fileUser)); //READ FILE USER AND SET IT TO NEW CHAT
        createFile(fileMessages, newMessage); //CREATE NEW FILE MESSAGES CONTAINING THE NEW MESSAGE
        chats.add(newChat);
    }

    //READ FILE FILENAME AND RETURNS IT AS A STRING
    public String readFile(String filename)
    {
        StringBuilder builder = new StringBuilder();
        try
        {
            FileInputStream fileInputStream = getContext().openFileInput(filename);
            int ch;
            while((ch = fileInputStream.read()) != -1){
                builder.append((char)ch);
            }
            fileInputStream.close();
        }
        catch (Exception e)
        {
            Log.e(TAG, "CAN'T LOAD FILE: " + e.toString());
        }
        return builder.toString();
    }

    //CREATE A NEW FILE FILENAME WITH THE THE GIVE CONTENT
    public void createFile(String filename, String content)
    {
        try
        {
            FileOutputStream fos2 = getContext().openFileOutput(filename, Context.MODE_PRIVATE);
            fos2.write(content.getBytes());
            fos2.close();
        }
        catch (Exception e)
        {
            Log.e(TAG, "CAN'T CREATE FILE: " + e.toString());
        }
    }

    //NOTIFY DATA SET CHANGED ON UI THREAD
    public void notifyDataSetChanged()
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                adapter.notifyDataSetChanged();
            }
        });
    }
}