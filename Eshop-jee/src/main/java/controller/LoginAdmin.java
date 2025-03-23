package controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Utilisateur_model;
import model.UtilisateurDB;

@WebServlet("/LoginAdmin")
public class LoginAdmin extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LoginAdmin.class.getName());
    private UtilisateurDB utilisateurDB = new UtilisateurDB();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String logout = request.getParameter("logout");
        if (logout != null && logout.equals("true")) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/LoginAdmin");
            return;
        }
        request.getRequestDispatcher("/jsp/admin/loginAdmin.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.log(Level.INFO, "doPost request URI: {0}", request.getRequestURI());
        String email = request.getParameter("email");
        String motDePasse = request.getParameter("motDePasse");

        try {
            Utilisateur_model admin = utilisateurDB.authentifierAdmin(email, motDePasse);
            if (admin != null) {
                HttpSession session = request.getSession();
                session.setAttribute("admin", true);
                session.setAttribute("adminId", admin.getId());
                session.setAttribute("adminUser", admin); // Store admin details in session
                response.sendRedirect(request.getContextPath() + "/adminDashboard");
            } else {
                request.setAttribute("error", "Email ou mot de passe incorrect.");
                request.getRequestDispatcher("/jsp/admin/loginAdmin.jsp").forward(request, response);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'authentification", e);
            request.setAttribute("error", "Erreur lors de l'authentification : " + e.getMessage());
            request.getRequestDispatcher("/jsp/admin/loginAdmin.jsp").forward(request, response);
        }
    }
}