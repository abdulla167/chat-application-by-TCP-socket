package com.server.chatServer.Sockets;

import com.server.chatServer.entites.Message;
import com.server.chatServer.entites.User;
import com.server.chatServer.services.UserServices;
import org.json.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Scope("prototype")
public class ClientSocket implements Runnable {
    // FIELDS
    private Socket clientConnection;
    private BufferedReader receiver;
    private PrintWriter sender;
    private String phone = null;
    @Autowired
    private UserServices userServices;

    public void setClientConnection(Socket clientConnection) throws IOException {
        this.clientConnection = clientConnection;
        this.receiver = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
        this.sender = new PrintWriter(clientConnection.getOutputStream(), true);
    }

    // METHODS
    public ClientSocket() {
    }

    public UserServices getUserServices() {
        return userServices;
    }

    public void setUserServices(UserServices userServices) {
        this.userServices = userServices;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public PrintWriter getSender() {
        return sender;
    }

    public void setSender(PrintWriter sender) {
        this.sender = sender;
    }

    @Override
    public void run() {
        while (true) {
            // GET REQUEST FROM THE USER
            try {
                String message = null;
                message = receiver.readLine();
                if (message == null)
                    continue;
                System.out.println(message);
                // CONVERT STRING TO JSON OBJECT
                JSONObject json = new JSONObject(message);
                // GET endpoint FIELD FROM THE JSON OBJECT
                String request = json.getString("endpoint");
                System.out.println(request);
                // GET PAYLOAD SECTION FROM THE JSON OJECT
                JSONObject payload = json.getJSONObject("payload");
                // MAKE NEW JSON OBJECT TO PUT THE RESPONSE FOR THE CLIENT IN IT
                JSONObject response = new JSONObject();
                // CHECK IF REQUEST IS FOR REGISTERATION
                if (request.equals("register")) {
                    System.out.println("register");
                    // CREATE NEW USER AND PUT THE DATA OF THE NEW USER IN IT FROM PAYLOAD SECTION
                    User newUser = new User(payload.getString("phone"), payload.getString("username"), payload.getString("password"));
                    newUser.setActive(true);
                    boolean result = this.userServices.registerNewUser(newUser);
                    response.put("endpoint", request);
                    response.put("response", result);
                    // CHECK IF USER SAVED SUCCESSFULLY IN THE DATABASE OR NOT
                    if (result) {
                        this.phone = payload.getString("phone");
                        // PUT THE RESPONSE FOR THE SUCCESS REQUEST IN THE JSON OBJECT
                        response.put("description", "User added successfully");
                        response.put("user", new JSONObject(newUser.jsonString()));
                    } else {
                        // PUT THE RESPONSE FOR THE FAILING REQUEST IN THE JSON OBJECT
                        response.put("description", "failed to add user");
                    }
                    // SEND THE JSON OBJECT AS STRING
                    System.out.println(response.toString());
                    this.sender.println(response.toString());
                } else if (request.equals("login")) {
                    System.out.println("login");
                    String phoneNumber = payload.getString("phone");
                    String password = payload.getString("password");
                    User result = this.userServices.authenticateUser(phoneNumber, password);
                    response.put("endpoint", request);
                    if (result != null) {
                        this.phone = phoneNumber;
                        this.userServices.setUserStatus(true, this.phone);
                        List<JSONObject> friendsList = this.userServices.getUserFriends(phone);
                        response.put("response", true);
                        response.put("description", "got friend list successfully");
                        response.put("friends", friendsList);
                        response.put("user", new JSONObject(result.jsonString()));
                    } else {
                        response.put("response", false);
                        response.put("description", "invalid user or password");
                        response.put("payload", new ArrayList<>());
                    }
                    // SEND THE JSON OBJECT AS STRING
                    System.out.println(response.toString());
                    this.sender.println(response.toString());
                }
                // CHECK IF REQUEST IS FOR SENDING MESSAGE
                else if (request.equals("sendMessage")) {
                    // GET SENDER USER
                    User senderUser = userServices.getUser(this.phone);
                    // GET RECEIVER USER
                    User receiverUser = userServices.getUser(payload.getString("sendTo"));
                    // CREATE NEW USER AND PUT THE DATA OF THE NEW MESSAGE IN IT FROM PAYLOAD SECTION
                    Message newMessage = new Message(payload.getString("messageText"), new Timestamp(new Date().getTime()));
                    newMessage.setTheSender(senderUser);
                    newMessage.setTheReceiver(receiverUser);
                    boolean result = this.userServices.sendMessage(newMessage, payload);
                    response.put("endpoint", request);
                    response.put("response", result);
                    if (result) {
                        response.put("description", "Message was sent successfully");
                    } else {
                        response.put("description", "Message failed to be sent");
                    }
                    // SEND THE JSON OBJECT AS STRING
                    this.sender.println(response.toString());
                } else if (request.equals("getConversation")) {
                    String senderPhone = this.phone;
                    String receiverPhone = payload.getString("friendPhone");
                    response.put("endpoint", request);
                    List<JSONObject> conversation = this.userServices.getConversation(senderPhone, receiverPhone);
                    if (conversation != null) {
                        response.put("response", true);
                        response.put("payload", conversation);
                    } else {
                        response.put("response", true);
                        response.put("payload", new ArrayList<>());
                    }
                    // SEND THE JSON OBJECT AS STRING
                    this.sender.println(response.toString());
                } else if (request.equals("addFriend")) {
                    String phone = payload.getString("phone");
                    boolean result = this.userServices.addFriend(this.phone, phone);
                    response.put("endpoint", request);
                    response.put("response", result);
                    if (result) {
                        response.put("description", "Friend is addded successfully");
                        User theUser = this.userServices.getUser(phone);
                        theUser.setPassword(null);
                        response.put("payload", new JSONObject(theUser.jsonString()));
                        this.userServices.notifyNewFriend(this.phone, phone);
                    } else {
                        response.put("description", "This phone is not found");
                    }
                    this.sender.println(response.toString());
                } else if (request.equals("logout")) {
                    this.userServices.setUserStatus(false, this.phone);
                    this.userServices.saveLastLogin(this.phone);
                    break;
                }
            } catch (IOException e) {
                this.userServices.setUserStatus(false, this.phone);
                this.userServices.saveLastLogin(this.phone);
                break;
            }
        }
    }
}

