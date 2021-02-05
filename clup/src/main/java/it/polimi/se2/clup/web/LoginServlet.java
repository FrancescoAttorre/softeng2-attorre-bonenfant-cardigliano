package it.polimi.se2.clup.web;

import it.polimi.se2.clup.auth.AuthManagerInt;
import it.polimi.se2.clup.auth.exceptions.CredentialsException;

import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @EJB
    AuthManagerInt am;


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("login.html").include(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        String pIVA = req.getParameter("pIVA");
        String password = req.getParameter("password");
        String jwt;

        if(pIVA != null && password != null) {
            try {
                jwt = am.authenticateActivity(pIVA, password);

                Cookie cookie = new Cookie("token", jwt);

                resp.addCookie(cookie);

                resp.sendRedirect("home");


            } catch (CredentialsException e) {
                req.setAttribute("error", e.getMessage());
                RequestDispatcher rs = req.getRequestDispatcher("error.jsp");
                rs.include(req, resp);
            }
        }
    }
}
