package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.UtilisateurDB;
import model.Utilisateur_model;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UtilisateurDB utilisateurDB;

    @Override
    public void init() throws ServletException {
        utilisateurDB = new UtilisateurDB();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Display the login page
        request.getRequestDispatcher("/jsp/client/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            Utilisateur_model utilisateur = utilisateurDB.authentifierClient(email, password);
            if (utilisateur != null) {
                // Authentication successful, store user in session
                HttpSession session = request.getSession();
                session.setAttribute("utilisateur", utilisateur);
                // Redirect to the homepage or cart
                response.sendRedirect(request.getContextPath() + "/index");
            } else {
                // Authentication failed, redirect back to login with error
                response.sendRedirect(request.getContextPath() + "/login?error=true");
            }
        } catch (Exception e) {
            throw new ServletException("Erreur lors de la connexion", e);
        }
    }
}