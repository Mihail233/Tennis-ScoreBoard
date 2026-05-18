package org.example.tennisscoreboard.dao.player;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.example.tennisscoreboard.entity.Player;
import org.example.tennisscoreboard.common.BaseDAO;
import org.example.tennisscoreboard.exception.TestException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class H2PlayerDAO extends BaseDAO<Player, Player> {
    //Supplier, Consumer, Function

    public H2PlayerDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public void run(Player player, Session session) {
        try {
            session.beginTransaction();
            session.merge(player);
            session.getTransaction().commit();
        } catch (Exception e) {
            //ловить index, но при этом не ловим остальные
            //проброс в baseDao
            //ИДЕЯ КАСТОМИЗАЦИИ ОШИБОК для SERVLETS
            int i = 0;
            //throw new TestException("test3");
        }
    }

    @Override
    public Player run(String playerName, Session session) {
        try {
            session.beginTransaction();

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Player> criteriaQuery = criteriaBuilder.createQuery(Player.class);
            Root<Player> playerRoot = criteriaQuery.from(Player.class);

            criteriaQuery.select(playerRoot).where(
                    criteriaBuilder.equal(playerRoot.get("name"), playerName));

            Player player = session.createQuery(criteriaQuery).getSingleResult();

            session.getTransaction().commit();
            return player;

        } catch (Exception e) {
            //ИДЕЯ КАСТОМИЗАЦИИ ОШИБОК для SERVLETS
            //словили ошибку необходимо ее здесь словить и перебросить нужную
            //проброс в baseDao
            throw new TestException("test2");
        }
    }
}
