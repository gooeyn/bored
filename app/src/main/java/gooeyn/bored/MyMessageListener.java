package gooeyn.bored;

import android.util.Log;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.chat.*;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;

/**
 * Created by guilh on 12/2/2015.
 */
public class MyMessageListener implements ChatMessageListener {

    @Override
    public void processMessage(Chat chat, Message message) {
        Log.e("recebenu", chat.getParticipant() + ". m: " + message.getBody());
        if(message.getBody() != null)
        {

        }
    }
}
