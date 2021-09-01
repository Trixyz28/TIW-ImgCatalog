package it.polimi.tiw.controllers;

import it.polimi.tiw.dao.CategoryDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import org.apache.commons.text.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;


@WebServlet("/MoveCategory")
public class MoveCategory extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;
    private Connection connection = null;

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());

        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }



    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        String cidParam = StringEscapeUtils.escapeJava(request.getParameter("categoryid"));
        String destParam = StringEscapeUtils.escapeJava(request.getParameter("destid"));

        int cid = -1;
        int destid = -1;

        if(cidParam == null || destParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameters cannot be null");
            return;
        }

        try {
            cid = Integer.parseInt(cidParam);
            destid = Integer.parseInt(destParam);

            if(cid <= 0 || destid < 0) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid id!");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter id with format number is required");
            return;
        }


        CategoryDAO categoryDAO = new CategoryDAO(connection);

        try {
            categoryDAO.moveCategory(cid,destid);

        } catch(Exception e) {
            // e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error in moving the category");
            return;
        }

        String contextPath = getServletContext().getContextPath();
        String path = contextPath + "/GoToHomePage";
        response.sendRedirect(path);
    }
}
