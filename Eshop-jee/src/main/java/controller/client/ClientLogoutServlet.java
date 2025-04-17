package controller.client;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/clientLogout")
public class ClientLogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get the session, if it exists
        HttpSession session = request.getSession(false);
        String redirectUrl = request.getContextPath() + "/login"; // Redirect to client login page

        if (session != null) {
            // Invalidate the session
            session.invalidate();
        }

        // Redirect to the client login page
        response.sendRedirect(redirectUrl);
    }
}