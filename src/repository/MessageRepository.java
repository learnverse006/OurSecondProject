package repository;

import models.entity.Message;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;

import java.util.List;

public class MessageRepository {

    public void saveMessage(Message message) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.save(message);
            tx.commit();
            System.out.println("[DB] Đang lưu tin nhắn: " + message.getContent());
        } catch (Exception e) {
            System.err.println("Lỗi khi lưu tin nhắn: " + e.getMessage());
        }
    }

    public void deleteMessage(Message message){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.delete(message);
            tx.commit();
            System.out.println("[DB] Đang xóa tin nhắn: " + message.getContent());
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa tin nhắn: " + e.getMessage());
        }
    }

    public List<Message> getMessageByChatID(int chatID){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Message> query = session.createQuery(
                    "FROM Message WHERE chatID = :chatID ORDER BY createAt ASC", Message.class);
            query.setParameter("chatID", chatID);
            return query.getResultList();
        }
    }

    public List<Message> pairChatHIs(int userID1, int userID2){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Message> query = session.createQuery(
                    "FROM Message WHERE (senderID = :user1 AND receiverID = :user2) " +
                            "OR (senderID = :user2 AND receiverID = :user1) ORDER BY createAt ASC", Message.class);
            query.setParameter("user1", userID1);
            query.setParameter("user2", userID2);
            return query.getResultList();
        }
    }




}
