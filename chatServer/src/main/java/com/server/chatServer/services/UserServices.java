package com.server.chatServer.services;

import com.server.chatServer.DAO.UserDAOImp;
import com.server.chatServer.entites.Message;
import com.server.chatServer.entites.User;

import java.util.List;

public interface UserServices {
    public void setUserStatus(boolean active, String phone);

    public boolean registerNewUser(User newUser);

    public boolean authenticateUser(String phone, String password);

    public List<User> getUserFriends(String phone);

    public boolean sendMessage(Message theMessage);

    public List<Message> getConversation(String senderPhone, String receiverPhone);

    public void saveLastLogin(String phone);

    public List<Message> getNewMessages(String receiverPhone);

    public User getUser(String phone);

    public boolean addFriend(String userPhone, String friendPhone);
}
