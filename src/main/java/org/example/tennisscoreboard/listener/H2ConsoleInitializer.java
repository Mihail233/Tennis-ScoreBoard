package org.example.tennisscoreboard.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.h2.tools.Server;

import java.sql.SQLException;

@WebListener
public class H2ConsoleInitializer implements ServletContextListener {

    private Server webServer;
    private Server server;

    public void contextInitialized(ServletContextEvent sce) {
        try {
            server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092").start();
            webServer = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();

        } catch (SQLException e) {
            System.err.println("Failed to start H2 Console: " + e.getMessage());
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        if (webServer != null) {
            webServer.stop();
        }
        if (server != null) {
            server.stop();
        }
        System.out.println("H2 Console stopped");
    }
}
