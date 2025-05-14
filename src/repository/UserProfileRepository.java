package repository;

import models.entity.UserProfile;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

public class UserProfileRepository {

    public boolean updateProfile(UserProfile userProfile){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(userProfile);// ON DUPLICATE KEY UPDATE
            tx.commit();
            return true;
        }catch (Exception e){
            System.err.println("Lỗi khi update UserProfile: " + e.getMessage());
            return false;
        }
    }

    public UserProfile getUserProfileById(int userID){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(UserProfile.class, userID);
        }catch (Exception e){
            System.err.println("Lỗi khi lấy UserProfile: " + e.getMessage());
            return null;
        }
    }
}
