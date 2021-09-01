package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.Category;
import it.polimi.tiw.dao.CategoryDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import org.apache.commons.text.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

@WebServlet("/GoToMove")
public class GoToMove extends HttpServlet {

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


        String idParam = StringEscapeUtils.escapeJava(request.getParameter("categoryid"));
        int id = -1;

        if(idParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter id cannot be null");
            return;
        }

        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter id with format number is required");
            return;
        }


        List<Category> allCategories = null;
        List<Category> topCategories = null;

        CategoryDAO categoryDAO = new CategoryDAO(connection);

        try {
            allCategories = categoryDAO.findAllCategories();
            topCategories = categoryDAO.findTopsAndSubtrees();

        } catch(Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error in retrieving categories from the database");
            return;
        }

        // Redirect to the Home page and add categories to the parameters
        String path = "/WEB-INF/HomePage.html";
        ServletContext servletContext = getServletContext();

        final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
        webContext.setVariable("allcategories",allCategories);
        webContext.setVariable("topcategories",topCategories);
        webContext.setVariable("cid",id);
        webContext.setVariable("page","move");
        templateEngine.process(path, webContext, response.getWriter());

    }


}
