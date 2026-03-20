package org.example.servlets;

import org.example.services.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RootServlet extends HttpServlet {
    private Service service;
    private static final Logger log = LoggerFactory.getLogger(RootServlet.class);

    public RootServlet(Service service){
        this.service = service;
    }



    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.debug("root doGet");
        String path = request.getRequestURI()
                .substring(request.getContextPath().length());

        if (path.equals("/") || path.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String key = path.substring(1);
        log.debug("Key to old url is: " + key);
        if(service == null) System.out.println("Service is null");
        String oldUrl = service.get(key);

        if (oldUrl == null) {
            log.info("Url not found");
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (!oldUrl.startsWith("http://") && !oldUrl.startsWith("https://")) {
            oldUrl = "https://" + oldUrl;
        }

        response.sendRedirect(oldUrl);
    }
}
