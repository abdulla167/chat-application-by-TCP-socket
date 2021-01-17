package com.server.chatServer.Sockets;

import com.server.chatServer.entites.Message;
import com.server.chatServer.entites.User;
import com.server.chatServer.services.UserServices;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.*;
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
                    // SET HIM AS ACTIVE USER
                    newUser.setActive(true);
                    // GET RESULT OF USER REGISTERTION IF SUCCESS OR FAIL
                    boolean result = this.userServices.registerNewUser(newUser);
                    // ADD ENDPOINT AND RESPONSE RESULT TO JSON OBJECT WHICH WILL BE SENT TO THE USER
                    response.put("endpoint", request);
                    response.put("response", result);
                    // CHECK IF USER SAVED SUCCESSFULLY IN THE DATABASE OR NOT
                    if (result) {
                        // CONNECT USER PRIMARY KEY WITH HIS SOCKET IN THE SERVER
                        this.phone = payload.getString("phone");
                        // PUT THE RESPONSE FOR THE SUCCESS REQUEST IN THE JSON OBJECT
                        response.put("description", "User added successfully");
                        response.put("user", new JSONObject(newUser.jsonString()));
                    } else {
                        // PUT THE RESPONSE FOR THE FAILING REQUEST IN THE JSON OBJECT
                        response.put("description", "failed to add user");
                    }
                    // SEND THE JSON OBJECT AS STRING
                    this.sender.println(response.toString());
                }
                // CHECK IF REQUEST IS FOR LOGIN
                else if (request.equals("login")) {
                    // GET USER DATA FROM PAYLOAD SECTION IN THE RECEIVED JSON OBJECT
                    String phoneNumber = payload.getString("phone");
                    String password = payload.getString("password");
                    // AUTHENTICATE THE USER
                    User result = this.userServices.authenticateUser(phoneNumber, password);
                    // ADD THE END POINT WHICH THE RESPONSE WILL BE SENT TO IT
                    response.put("endpoint", request);
                    // CHECK IF THE USER FOUND IN THE DATABASE SO SEND SUCCESS RESPONSE ELSE SEND FAIL RESPONSE
                    if (result != null) {
                        this.phone = phoneNumber;
                        this.userServices.setUserStatus(true, this.phone);
                        // GET USER FRIENDS FROM THE DATABASE TO SEND THEM IN RESPONSE PAYLOAD SECTION
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
                    Message newMessage = null;
                    // CHECK IF THE MESSAGE WHICH WAS SENT IS TEXT MESSAGE OR FILE
                    if ( payload.getString("type").equals("message")){
                        // CREATE TEXT MESSAGE
                        newMessage = new Message(payload.getString("messageText"), payload.getString("type"), null, new Timestamp(new Date().getTime()));
                    }else {
                        // CREATE FILE MESSAGE
                        newMessage = new Message(payload.getString("messageText"), payload.getString("type"),  payload.getString("file"), new Timestamp(new Date().getTime()));
                    }
                    // SET SENDER AND RECEIVER IN THE MESSAGE OBJECT
                    newMessage.setTheSender(senderUser);
                    newMessage.setTheReceiver(receiverUser);
                    // GET RESULT OF SAVING THE MESSAGE
                    boolean result = this.userServices.sendMessage(newMessage, payload);
                    // FILLING THE JSON ENDPOINT AND RESPONSE SECTION
                    response.put("endpoint", request);
                    response.put("response", result);
                    // CHECK IF MESSAGE SENT AND SAVED IN THE DATABASE WELL AND THEN DESCRIPTION TO USER
                    if (result) {
                        response.put("description", "Message was sent successfully");
                    } else {
                        response.put("description", "Message failed to be sent");
                    }
                    // SEND THE JSON OBJECT AS STRING
                    this.sender.println(response.toString());
                }
                // CHECK IF REQUEST IS FOR GETTING CONVERSATION
                else if (request.equals("getConversation")) {
                    String senderPhone = this.phone;
                    String receiverPhone =  payload.getString("friendPhone");
                    // GET THE CONVERSATION BETWEEN THE SENDER AND RECEIVER
                    JSONArray conversation= this.userServices.getConversation(senderPhone, receiverPhone);
                    response.put("endpoint", request);
                    // CHECK IF THERE IS CONVERSATION OR THIS IS THE FIRST TIME TO CHAT
                    if (conversation != null){
                        response.put("response", true);
                        response.put("payload", conversation);
                    }else {
                        response.put("response", false);
                        response.put("payload", new ArrayList<>());
                    }
                    // SEND THE JSON OBJECT AS STRING
                    this.sender.println(response.toString());
                }
                // CHECK IF REQUEST IS FOR ADDING FRIEND
                else if (request.equals("addFriend")) {
                    String phone = payload.getString("phone");
                    // RESPONSE OF ADDING FRIEND REQUEST
                    boolean result = this.userServices.addFriend(this.phone, phone);
                    // ADD ENDPOINT TO SEND TO
                    response.put("endpoint", request);
                    // ADD THE RESPONSE IN THE JSON OBJECT TO BE SENT
                    response.put("response", result);
                    // CHECK IF THE USER ADDED SUCCESSFULLY OR FAILED
                    if (result) {
                        // SEND THE USER DATA TO SENDER
                        response.put("description", "Friend is addded successfully");
                        User theUser = this.userServices.getUser(phone);
                        theUser.setPassword(null);
                        response.put("payload", new JSONObject(theUser.jsonString()));
                        this.userServices.notifyNewFriend(this.phone, phone);
                    } else {
                        // SEND RESPONSE THAT PHONE NOT FOUND
                        response.put("description", "This phone is not found");
                    }
                    this.sender.println(response.toString());
                }
                // CHECK IF REQUEST IS FOR LOGGING OUT
                else if (request.equals("logout")) {
                    /* IF USER LOGGED OUT CHANGE USER TO OFFLINE AND SAVE THE
                     * TIME OF HIS LAST SEEN & BREAK TO END THIS SOCKET THREAD
                     */
                    this.userServices.setUserStatus(false, this.phone);
                    this.userServices.saveLastSeen(this.phone);
                    break;
                }
            }
            /* IF USER CLOSED THE APP SERVER WILL GET IOEXCEPTION SO HANDLE IT BY
             *CHANGE USER TO OFFLINE AND SAVE THE TIME OF HIS LAST SEEN
             * & BREAK TO END THIS SOCKET THREAD
             */
            catch (IOException e) {
                this.userServices.setUserStatus(false, this.phone);
                this.userServices.saveLastSeen(this.phone);
                break;
            }
        }
    }
}

