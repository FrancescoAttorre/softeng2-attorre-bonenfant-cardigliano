package it.polimi.se2.clup.web;

import it.polimi.se2.clup.auth.AuthFlag;
import it.polimi.se2.clup.auth.AuthManagerInt;
import it.polimi.se2.clup.auth.exceptions.TokenException;
import it.polimi.se2.clup.building.BuildingManagerInterface;

import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    @EJB
    BuildingManagerInterface bm;

    @EJB
    AuthFilter filter;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Integer activityID = filter.checkCookie(req, resp, AuthFlag.ACTIVITY);

        if(activityID == null)
            return;

        req.getRequestDispatcher("welcome.jsp").forward(req, resp);

    }
}
