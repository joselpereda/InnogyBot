package com.yukinohara.innogybot.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by YukiNoHara on 6/14/2017.
 */

public class Message implements Parcelable{
    private int permission;
    private String content;

    public Message(){

    }

    public Message(int permission, String content) {
        this.permission = permission;
        this.content = content;
    }

    protected Message(Parcel in) {
        permission = in.readInt();
        content = in.readString();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public int getPermission() {
        return permission;
    }

    public String getContent() {
        return content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(permission);
        dest.writeString(content);
    }
}
