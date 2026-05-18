package org.example.tennisscoreboard.common;

import org.example.tennisscoreboard.entity.Player;
import org.example.tennisscoreboard.exception.TestException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public abstract class BaseDAO<K, E> {
    private final SessionFactory sessionFactory;

    public BaseDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected abstract void run(K entity, Session session);

    protected abstract E run(String name, Session session);

    public void insert(K entity) {

        try (Session session = sessionFactory.openSession()) {

            try {
                run(entity, session);
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

    public E findByName(String name) {
        try (Session session = sessionFactory.openSession()) {

            try {
                return run(name, session);

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
