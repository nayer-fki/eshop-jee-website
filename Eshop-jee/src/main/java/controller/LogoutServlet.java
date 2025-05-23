package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get the session, if it exists
        HttpSession session = request.getSession(false);
        String redirectUrl = request.getContextPath() + "/LoginAdmin"; // Default redirect for admins

        if (session != null) {
            // Check if the user is an admin
            Boolean isAdmin = (Boolean) session.getAttribute("admin");
            if (isAdmin != null && isAdmin) {
                redirectUrl = request.getContextPath() + "/LoginAdmin"; // Redirect admins to admin login
            } else {
                // For clients, redirect to the client logout servlet
                response.sendRedirect(request.getContextPath() + "/clientLogout");
                return;
            }

            // Invalidate the session
            session.invalidate();
        }

        // Redirect to the appropriate page
        response.sendRedirect(redirectUrl);
    }
}