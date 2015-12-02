package gooeyn.bored;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.chat.*;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;

/**
 * Created by guilh on 12/2/2015.
 */
public class MyListener implements ChatManagerListener{
    ChatMessageListener messageListener = new MyMessageListener();
    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        chat.addMessageListener(messageListener);
    }
}
