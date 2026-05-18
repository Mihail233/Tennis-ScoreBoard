package org.example.tennisscoreboard.dao.match;

import org.example.tennisscoreboard.common.BaseDAO;
import org.example.tennisscoreboard.entity.Match;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class H2FinishedMatchDAO extends BaseDAO<Match, List<Match>> {

    public H2FinishedMatchDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public void run(Match match, Session session) {
        try {
            session.beginTransaction();
            session.merge(match);
            session.getTransaction().commit();
        } catch (Exception e) {
            //ИДЕЯ КАСТОМИЗАЦИИ ОШИБОК для SERVLETS
            //throw new TestException("test3");
        }
    }

    @Override
    public List<Match> run(String name, Session session) {
        return List.of();
    }
}
