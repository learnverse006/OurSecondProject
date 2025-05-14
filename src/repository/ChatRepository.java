package repository;

import models.entity.Chat;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.List;

public class ChatRepository {

    public boolean saveChat(Chat chat) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.save(chat);
            tx.commit();
            return true;
        } catch (Exception e) {
            System.err.println("Error saving chat: " + e.getMessage());
            return false;
        }
    }

    public boolean updateChat(Chat chat){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.update(chat);
            tx.commit();
            return true;
        } catch (Exception e) {
            System.err.println("Error updating chat: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int chatId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Chat chat = session.get(Chat.class, chatId);
            if (chat != null) {
                session.delete(chat);
            }
            tx.commit();
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa Chat: " + e.getMessage());
            return false;
        }
    }

    public Chat findById(int chatId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Chat.class, chatId);
        }
    }

    //    // Read all - Lấy danh sách tất cả các phòng chat
    @SuppressWarnings("unchecked")
    public List<Chat> getAllChats() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Chat").list();
        }
    }

}
