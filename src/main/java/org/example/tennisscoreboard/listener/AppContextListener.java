package org.example.tennisscoreboard.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.example.tennisscoreboard.common.BaseDAO;
import org.example.tennisscoreboard.dao.player.H2PlayerDAO;
import org.example.tennisscoreboard.entity.Player;
import org.example.tennisscoreboard.service.match.MatchScoreCalculationService;
import org.example.tennisscoreboard.service.match.OngoingMatchesService;
import org.example.tennisscoreboard.service.player.PlayerService;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@WebListener
public class AppContextListener implements ServletContextListener {


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Configuration configuration = new Configuration().configure();
        SessionFactory sessionFactory = configuration.buildSessionFactory();

        //переделал с H2PlayerDAO -> BaseDAO<Player> - не тестировал
        BaseDAO<Player> h2PlayerDAO = new H2PlayerDAO(sessionFactory);

        PlayerService playerService = new PlayerService(h2PlayerDAO);
        OngoingMatchesService ongoingMatchesService = new OngoingMatchesService();
        MatchScoreCalculationService matchScoreCalculationService = new MatchScoreCalculationService();

        ServletContext context = sce.getServletContext();
        context.setAttribute("sessionFactory", sessionFactory);
        context.setAttribute("playerService", playerService);
        context.setAttribute("ongoingMatchesService", ongoingMatchesService);
        context.setAttribute("matchScoreCalculationService", matchScoreCalculationService);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
       SessionFactory sessionFactory = (SessionFactory) sce.getServletContext().getAttribute("sessionFactory");

        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}