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
    AuthManagerInt am;

    @EJB
    BuildingManagerInterface bm;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cookie[] cookies = req.getCookies();
        Integer activityID;

        Cookie jwt = null;

        //if there are no cookies just visualize the index page
        if(cookies == null) {
            req.getRequestDispatcher("/index.html").forward(req,resp);
            return;
        }



        for(Cookie cookie : cookies) {
            if(cookie.getName().equals("token")) {
                jwt = cookie;
                System.out.println("Found Cookie: " + jwt.getValue());
                break;
            }
        }

        if(jwt != null) {
            try {
                activityID = am.verifyToken(jwt.getValue(), AuthFlag.ACTIVITY);
            } catch (TokenException e) {
                req.setAttribute("error", e.getMessage());
                jwt.setMaxAge(0); //delete cookie
                resp.addCookie(jwt);
                req.getRequestDispatcher("/error.jsp").forward(req, resp);
                return;
            }

            //TODO: retrieve buildings
            req.getRequestDispatcher("/welcome.jsp").forward(req, resp);

        } else {
            req.getRequestDispatcher("/login").forward(req, resp);
        }

    }
}
