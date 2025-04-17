package controller.admin;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/search")
public class SearchController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null || !(boolean) session.getAttribute("admin")) {
            response.sendRedirect(request.getContextPath() + "/LoginAdmin");
            return;
        }

        String query = request.getParameter("query");
        if (query != null && !query.trim().isEmpty()) {
            // Trim the query to remove leading/trailing spaces
            query = query.trim();
            // Encode the query properly for URL
            String encodedQuery = java.net.URLEncoder.encode(query, "UTF-8").replace("+", "%20");
            // Redirect to gererUtilisateurs with the properly encoded query
            response.sendRedirect(request.getContextPath() + "/gererUtilisateurs?query=" + encodedQuery);
        } else {
            // If no query, redirect to dashboard
            response.sendRedirect(request.getContextPath() + "/adminDashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}