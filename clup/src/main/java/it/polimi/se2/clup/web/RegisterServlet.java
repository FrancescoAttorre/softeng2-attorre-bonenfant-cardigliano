package it.polimi.se2.clup.web;

import it.polimi.se2.clup.auth.AuthManagerInt;

import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @EJB
    AuthManagerInt am;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean registered = false;

       // String response = "<html><head><title>Registration</title/></head>";

        String name = req.getParameter("name");
        String pIVA = req.getParameter("pIVA");
        String password = req.getParameter("password");

        if(name != null && pIVA != null && password != null) {
            registered = am.registerActivity(name, pIVA, password);
            System.out.println("Activity registration: " + registered);

            req.setAttribute("registered", registered);
            req.getRequestDispatcher("/registrationResult.jsp").forward(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }


    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher rs = req.getRequestDispatcher("/register.html");
        rs.include(req, resp);
    }
}
