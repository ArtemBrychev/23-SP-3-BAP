package org.example.servlets;

import org.example.repositories.Repository;
import org.example.services.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class NewServlet extends HttpServlet {

    private final Service service;
    private static final Logger log = LoggerFactory.getLogger(NewServlet.class);

    public NewServlet(Service service){
        this.service = service;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/plain;charset=UTF-8");

        try (PrintWriter writer = response.getWriter()) {

            String oldUrl = request.getParameter("url");

            if (oldUrl != null && !oldUrl.isBlank()) {
                String newUrl = service.add(oldUrl);
                response.setStatus(HttpServletResponse.SC_OK);
                writer.write(newUrl);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writer.write("No url found, please enter your url");
            }
        }
    }
}
