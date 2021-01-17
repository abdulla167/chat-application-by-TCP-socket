package com.server.chatServer.DAO;

import com.server.chatServer.entites.Message;
import com.server.chatServer.entites.User;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Repository
public class UserDAOImp implements UserDAO{
    @Autowired
    private EntityManager entityManager;

    @Override
    public List<User> getUserFriends(String phone) {
       User theUser = this.getUser(phone);
       return theUser.getFriends();
    }

    @Override
    public boolean registerUser(User user) {
        try {
            User theUser = this.getUser(user.getPhone());
            return false;
        }catch (NoResultException nre){
            Session newSession = entityManager.unwrap(Session.class);
            newSession.save(user);
            return true;
        }
    }

    @Override
    public User authenticateUser(String phone, String password) {
        try {
            User theUser = this.getUser(phone);
            return theUser;
        }catch (NoResultException nre){
            return null;
        }
    }

    @Override
    public List<Message> getConversation(String senderPhone, String receiverPhone) {
        User sender = this.getUser(senderPhone);
        User receiver = this.getUser(receiverPhone);
        Session newSession = entityManager.unwrap(Session.class);
        try{
            Query theQuery =
                    newSession.createQuery("from message where message.theSender=:senderNumber  and message.theReceiver=:receiverNumber");
            theQuery.setParameter("senderNumber", sender.getPhone());
            theQuery.setParameter("receiverNumber", receiver.getPhone());
            List<Message> messages = (List<Message>) theQuery.getResultList();
            return messages;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public boolean saveMessage(Message newMessage) {
        Session newSession = entityManager.unwrap(Session.class);
        newSession.save(newMessage);
        newSession.clear();
        return true;
    }

    @Override
    public boolean addFriend(String userPhone, String friendPhone) {
        try{
            User theFriend = this.getUser(friendPhone);
            User theUser = this.getUser(userPhone);
            theUser.addFriend(theFriend);
            Session newSession = entityManager.unwrap(Session.class);
            newSession.save(theUser);
            return true;
        }
        catch (NoResultException nre){
            return false;
        }
    }

    @Override
    public void setUserStatus(boolean active, String phone) {
        try {
            User theUser = this.getUser(phone);
            theUser.setActive(active);
        }catch (NoResultException nre){

        }

    }

    @Override
    public void saveLastLogin(String phone) {
        Session newSession = entityManager.unwrap(Session.class);
        User theUser = this.getUser(phone);
        theUser.setLastLogin(new Timestamp(new Date().getTime()));
        newSession.saveOrUpdate(theUser);
    }

    @Override
    public List<Message> getNewMessages(String receiverPhone) {
        Session newSession = entityManager.unwrap(Session.class);

        return null;
    }

    @Override
    public User getUser(String phone) throws NoResultException{
        Session newSession = entityManager.unwrap(Session.class);
        Query theQuery =
                newSession.createQuery("from user where phone=:number").setParameter("number", phone);
        User theUser = (User) theQuery.getSingleResult();
        return theUser;
    }

}
