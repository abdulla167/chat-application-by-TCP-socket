package com.server.chatServer.DAO;

import com.server.chatServer.entites.Message;
import com.server.chatServer.entites.User;

import java.util.List;

public interface UserDAO {
    public List<User> getUserFriends(String phone);

    public boolean registerUser(User user);

    public User authenticateUser(String phone, String password);

    public List<Message> getConversation(String senderPhone, String receiverPhone);

    public boolean saveMessage(Message newMessage);

    public boolean addFriend(String userPhone, String friendPhone);

    public void setUserStatus(boolean active, String phone);

    public void saveLastLogin(String phone);

    public User getUser(String phone);




}
