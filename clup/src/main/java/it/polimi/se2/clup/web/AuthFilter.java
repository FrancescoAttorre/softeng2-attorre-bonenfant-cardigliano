package it.polimi.se2.clup.web;

import it.polimi.se2.clup.auth.AuthFlag;
import it.polimi.se2.clup.auth.AuthManagerInt;
import it.polimi.se2.clup.auth.exceptions.TokenException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Stateless
public class AuthFilter {

    @EJB
    AuthManagerInt am;

    public Integer checkCookie(HttpServletRequest req, HttpServletResponse resp, AuthFlag auth) throws ServletException, IOException {
        Cookie[] cookies = req.getCookies();
        Integer activityID;


        Cookie jwt = null;

        //if there are no cookies just visualize the index page
        if (cookies == null) {
            req.getRequestDispatcher("/index.html").forward(req, resp);
            activityID = null;
        } else {

            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    jwt = cookie;
                    //System.out.println("Found Cookie: " + jwt.getValue());
                    break;
                }
            }

            if (jwt != null) {
                try {
                    if(am == null)
                        System.err.println("AuthFilter: AuthManagerInt not injected!");
                    activityID = am.verifyToken(jwt.getValue(), auth);
                } catch (TokenException e) {
                    req.setAttribute("error", e.getMessage());
                    jwt.setMaxAge(0); //delete cookie
                    resp.addCookie(jwt);
                    req.getRequestDispatcher("/error.jsp").forward(req, resp);
                    activityID = null;

                }


            } else {
                req.getRequestDispatcher("/login").forward(req, resp);
                activityID = null;
            }

        }

        return activityID;
    }
}
