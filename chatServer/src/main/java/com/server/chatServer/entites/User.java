package com.server.chatServer.entites;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "user")
@Table(name= "user")
public class User {
    // FIELDS
    @Id
    @Column(name = "phone")
    private String phone;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "active")
    private boolean active;

    @Column(name = "last_login")
    private Timestamp lastLogin;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="tbl_friends",
            joinColumns=@JoinColumn(name="personId"),
            inverseJoinColumns=@JoinColumn(name="friendId")
    )
    private List<User> friends;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="tbl_friends",
            joinColumns=@JoinColumn(name="friendId"),
            inverseJoinColumns=@JoinColumn(name="personId")
    )
    private List<User> friendOf;

    // METHODS
    public void addFriend(User friend) {
        this.initializeIfNull(this.friends);
        this.initializeIfNull(this.friendOf);
        this.friends.add(friend);
        this.friendOf.add(friend);
    }

    public void initializeIfNull(List<User> l){
        if (l == null) {
            l = new ArrayList<User>();
        }
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public List<User> getFriendOf() {
        return friendOf;
    }

    public void setFriendOf(List<User> friendOf) {
        this.friendOf = friendOf;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User(){}

    public User(String phone, String username, String password) {
        this.phone = phone;
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "phone='" + phone + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String jsonString(){
        return "{\"username\":"+ this.username +",\"phone\":" + this.phone + "\"password\":" +this.password +"}";
    }
}
