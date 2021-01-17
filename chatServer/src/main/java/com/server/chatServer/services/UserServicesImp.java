package com.server.chatServer.services;

import com.server.chatServer.DAO.UserDAO;
import com.server.chatServer.Sockets.ClientSocket;
import com.server.chatServer.Sockets.ServerSocket;
import com.server.chatServer.entites.Message;
import com.server.chatServer.entites.User;
import org.json.JSONArray;
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

    /* METHOD TO SET USER ONLINE OR OFFLINE*/
    @Override
    @Transactional
    public void setUserStatus(boolean active, String phone) {
        userDAO.setUserStatus(active, phone);
    }

    /* METHOD TO REGISTER NEW USER*/
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

    /* METHOD TO AUTHENTICATE THE USER BY ITS PHONE AND PASSWORD*/
    @Override
    @Transactional
    public User authenticateUser(String phone, String password) {
        return this.userDAO.authenticateUser(phone, password);
    }

    /* METHOD TO GET THE USER LIST OF FRIENDS*/
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

    /* METHOD TO SEND MESSAGE TO THE RECEIVER IF ONLINE AND SAVE IT IN THE DATABASE*/
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

    /* METHOD TO GET CONVERSATION BETWEEN TWO USERS*/
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

    /* METHOD TO SET USER LAST SEEN DATE*/
    @Override
    @Transactional
    public void saveLastSeen(String phone) {
        this.userDAO.saveLastLogin(phone);
    }


    /* METHOD TO GET USER BY HIS PHONE*/
    @Override
    @Transactional
    public User getUser(String phone) {
        return userDAO.getUser(phone);
    }

    /* METHOD TO ADD FIREND TO USER*/
    @Override
    @Transactional
    public boolean addFriend(String userPhone, String friendPhone) {
        return this.userDAO.addFriend(userPhone, friendPhone);
    }

    /* METHOD TO NOTIFY USER THAT SOMEONE ADDED HIM AS
    *A FRIEND AND ADD HIM TO HIS LIST OF FRIENDS
    */
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

    public JSONObject JsonResponse(String endPoint, boolean response, String description, JSONObject payload){
        JSONObject jsonObject = new JSONObject();

        return jsonObject;
    }
}
