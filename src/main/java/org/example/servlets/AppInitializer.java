package org.example.servlets;

import org.example.repositories.CachedRepository;
import org.example.repositories.Repository;
import org.example.repositories.postgres.DbRepository;
import org.example.repositories.redis.RedisRepository;
import org.example.services.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

@WebListener
public class AppInitializer implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(AppInitializer.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("Initializing tomcat context");
        Repository repository = new CachedRepository(new DbRepository(), new RedisRepository());
        Service service = new Service(repository);

        ServletContext ctx = sce.getServletContext();
        ctx.addServlet("newServlet", new NewServlet(service))
                .addMapping("/addurl");

        ctx.addServlet("rootServlet", new RootServlet(service))
                .addMapping("/*");

        ctx.addServlet("pingServlet", new PingServlet())
                .addMapping("/ping");
    }
}
