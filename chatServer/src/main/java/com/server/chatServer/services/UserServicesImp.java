package com.server.chatServer.services;

import com.server.chatServer.DAO.UserDAO;
import com.server.chatServer.Sockets.ClientSocket;
import com.server.chatServer.Sockets.ServerSocket;
import com.server.chatServer.entites.Message;
import com.server.chatServer.entites.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
            return  check? true: false;
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
    public boolean sendMessage(Message theMessage, JSONObject payload) {
        List<ClientSocket> clientSockets = ServerSocket.getClients();
        for (ClientSocket client : clientSockets)
        {
            if (client.getPhone().equals(theMessage.getTheReceiver().getPhone())){
                JSONObject json = new JSONObject();
                json.put("endpoint", "receivedMessage");
                json.put("payload", payload);
                client.getSender().println(json.toString());
            }
        }
        return userDAO.saveMessage(theMessage);
    }

    @Override
    @Transactional
    public JSONArray getConversation(String senderPhone, String receiverPhone) {
        List<Message> messages = this.userDAO.getConversation(senderPhone, receiverPhone);
        String jsonList = "";
        JSONArray jsonArray = new JSONArray();
        if (messages != null){
            for (Message message : messages){
                jsonArray.put(new JSONObject(message.jsonString()));
            }
            return jsonArray;
        }else {
            return null;
        }
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

    @Override
    public void notifyNewFriend(String phone, String receiverPhone) {
        User theUser = this.userDAO.getUser(phone);
        List<ClientSocket> clientSockets = ServerSocket.getClients();
        for (ClientSocket client : clientSockets)
        {
            if (client.getPhone().equals(receiverPhone)){
                JSONObject json = new JSONObject();
                json.put("endpoint","newFriend");
                json.put("payload", new JSONObject(theUser.jsonString()));
                client.getSender().println(json.toString());
            }
        }
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
}
