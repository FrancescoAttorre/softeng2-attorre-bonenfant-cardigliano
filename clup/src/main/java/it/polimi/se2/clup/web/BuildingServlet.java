package it.polimi.se2.clup.web;

import it.polimi.se2.clup.auth.AuthFlag;
import it.polimi.se2.clup.auth.AuthManagerInt;
import it.polimi.se2.clup.building.BuildingManagerInterface;

import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/buildingManager")
public class BuildingServlet extends HttpServlet {
    @EJB
    BuildingManagerInterface bm;

    @EJB
    AuthManagerInt am;

    @EJB
    AuthFilter filter;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Integer activityID = filter.checkCookie(req, resp, AuthFlag.ACTIVITY);

        if(activityID == null)
            return;

        String name = req.getParameter("name");
        int capacity = Integer.parseInt(req.getParameter("capacity"));
        String openingTime = req.getParameter("openingTime");
        String closingTime = req.getParameter("closingTime");
        String address = req.getParameter("address");
        int nDepartments = Integer.parseInt(req.getParameter("counter"));

        Map<String, Integer> departments = new HashMap<>();

        for(int i = 1; i <= nDepartments; i++) {
            String depName = req.getParameter("dep" + i);
            Integer surplus = Integer.parseInt(req.getParameter("surplus" + i));

            if(depName == null || surplus == null) {
                req.setAttribute("error", "Bad Request");
                req.getRequestDispatcher("error.jsp").forward(req, resp);
                return;
            }

            departments.put(depName, surplus);
        }

        if(name == null || openingTime == null || closingTime == null || address == null) {
            req.setAttribute("error", "Bad Request");
            req.getRequestDispatcher("error.jsp").forward(req, resp);
            return;
        }

        String[] codes = am.generateAccessCode();
        System.out.println("Building AccessCode: " + codes[0]);

        if(bm.insertBuilding(activityID, name, LocalTime.parse(openingTime), LocalTime.parse(closingTime), address, capacity, departments, codes[1])) {
            req.setAttribute("result", codes[0]);
            RequestDispatcher rs = req.getRequestDispatcher("insertionResult.jsp");
            rs.include(req, resp);
        } else {
            req.setAttribute("error", "Could not insert building");
            req.getRequestDispatcher("error.jsp").forward(req, resp);
        }
    }
}
