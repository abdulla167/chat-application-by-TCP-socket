package com.server.chatServer.services;

import com.server.chatServer.DAO.UserDAO;
import com.server.chatServer.entites.Message;
import com.server.chatServer.entites.User;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServicesImp implements UserServices {

    @Autowired
    private UserDAO userDAO;

    @Override
    @Transactional
    public void setUserStatus(boolean active, String phone) {
        userDAO.setUserStatus(active, phone);
    }

    @Override
    @Transactional
    public boolean registerNewUser(User newUser) {
        try {
            boolean check = userDAO.registerUser(newUser);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    @Transactional
    public User authenticateUser(String phone, String password) {
        return this.userDAO.authenticateUser(phone, password);
    }

    @Override
    @Transactional
    public List<JSONObject> getUserFriends(String phone) {
        List<User> friends = this.userDAO.getUserFriends(phone);
        List<JSONObject> friendsJsonList = new ArrayList<>(friends.size());
        for (User user:friends){
            friendsJsonList.add(new JSONObject(user.jsonString()));
        }

        return friendsJsonList;
    }

    @Override
    @Transactional
    public boolean sendMessage(Message theMessage) {
        return userDAO.saveMessage(theMessage);
    }

    @Override
    @Transactional
    public List<Message> getConversation(String senderPhone, String receiverPhone) {
        return null;
    }

    @Override
    @Transactional

    public void saveLastLogin(String phone) {
        this.userDAO.saveLastLogin(phone);
    }

    @Override
    @Transactional
    public List<Message> getNewMessages(String receiverPhone) {
        this.userDAO.getNewMessages(receiverPhone);
        return null;
    }

    @Override
    @Transactional
    public User getUser(String phone) {
        return userDAO.getUser(phone);
    }

    @Override
    @Transactional
    public boolean addFriend(String userPhone, String friendPhone) {
        return this.userDAO.addFriend(userPhone, friendPhone);
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
}
