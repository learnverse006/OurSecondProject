package repository;


import models.entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.mindrot.jbcrypt.BCrypt;
import util.HibernateUtil;

import java.util.Optional;


public class UserRepository {

    public boolean insertUser(User user){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.save(user);
            tx.commit();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public User findByUsername(String username){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("from User where username = :username", User.class);
            query.setParameter("username", username);
            return query.uniqueResult();
        }
    }

    public User findByEmail(String email){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("from User where email = :email" , User.class);
            query.setParameter("email", email);
            return query.uniqueResult();
        }
    }

    public String getUsernameById(int userId) {
        User user = findById(userId);
        return user != null ? user.getUsername() : "Unknown";
    }

    public boolean existsByUsername(String username) {
        return Optional.ofNullable(findByUsername(username)).isPresent();
    }



    public boolean existsByEmail(String email) {
        return Optional.ofNullable(findByEmail(email)).isPresent();
    }


    public boolean insertUserByDefault(User user) {
        return insertUser(user);
    }
    public boolean checkPassword(String username, String plainPassword){
        User user = findByUsername(username);
        if (user == null) return false;
        return BCrypt.checkpw(plainPassword, user.getPasswordHash());
    }

    public User findById(int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, userId);
        }
    }

    public String getPictureByUserID(int userId) {
        User user = findById(userId);
        return user != null ? user.getProfilePicture() : "Default Picture";
    }

}
