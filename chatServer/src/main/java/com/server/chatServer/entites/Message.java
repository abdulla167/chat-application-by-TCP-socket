package com.server.chatServer.entites;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Entity(name = "message")
@Table(name = "message")
public class Message implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "senderPhone", referencedColumnName = "phone")
    private User theSender;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "receiverPhone", referencedColumnName = "phone")
    private User theReceiver;

    @Column(name = "message")
    private String message;

    @Column(name = "description")
    private String description;

    @Lob
    @Column(name = "file")
    private String file;

    @Column(name = "date")
    private Timestamp date;

    public Message(){}

    public Message(String message, String description,String file, Timestamp date) {
        this.message = message;
        this.description = description;
        this.file = file;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getTheSender() {
        return theSender;
    }

    public void setTheSender(User theSender) {
        this.theSender = theSender;
    }

    public User getTheReceiver() {
        return theReceiver;
    }

    public void setTheReceiver(User theReceiver) {
        this.theReceiver = theReceiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "Message{" +
                "senderPhone='" + theSender + '\'' +
                ", receiverPhone='" + theReceiver + '\'' +
                ", message='" + message + '\'' +
                ", date=" + date +
                '}';
    }

    public String jsonString() {
        return "{\"sendTo\":" + "\""+ this.theReceiver.getPhone()  +"\""+  ",\"sendFrom\":" +  "\""+ this.theSender.getPhone()  +"\""+ ",\"messageText\":" +  "\""+this.message + "\""+",\"file\":"+ "\""+ this.getFile()  +"\""+ ",\"type\":"+  "\""+this.description +"\"" + "}";
    }
}
