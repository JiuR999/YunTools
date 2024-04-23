package cn.swust.jiur.factory;

import android.os.Message;

public class MessageFactory {
    public static Message newMessage(int code,Object obj){
        Message message = new Message();
        message.what = code;
        message.obj = obj;
        return message;
    }
}
