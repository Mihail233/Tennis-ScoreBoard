package org.example.tennisscoreboard.common;

import org.example.tennisscoreboard.entity.Player;
import org.example.tennisscoreboard.exception.TestException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public abstract class BaseDAO<T> {
    private final SessionFactory sessionFactory;

    public BaseDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public abstract void run(Player player, Session session);

    public abstract T run(String playerName, Session session);

    public void insert(Player player) {

        try (Session session = sessionFactory.openSession()) {

            try {
                run(player, session);
            } catch (Exception e) {
                Transaction transaction = session.getTransaction();
                if (transaction != null) {
                    transaction.rollback();
                }
                throw new TestException("test3");
                //кинуть ошибку здесь, которую словили(она должна быть runtime)
            }
        }
    }

    public T findByName(String playerName) {
        try (Session session = sessionFactory.openSession()) {

            try {
                return run(playerName, session);

            } catch (Exception e) {
                Transaction transaction = session.getTransaction();
                if (transaction != null) {
                    transaction.rollback();
                }
                throw new TestException("test");
                //кинуть ошибку здесь, которую словили(она должна быть runtime)
            }
        }
    }
}
