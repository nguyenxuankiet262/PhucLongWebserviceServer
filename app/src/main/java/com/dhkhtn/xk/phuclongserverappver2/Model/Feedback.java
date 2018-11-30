package com.dhkhtn.xk.phuclongserverappver2.Model;

public class Feedback {
    int id;
    String content;
    String reply;

    public Feedback(){

    }

    public Feedback(int id, String content, String reply) {
        this.id = id;
        this.content = content;
        this.reply = reply;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
