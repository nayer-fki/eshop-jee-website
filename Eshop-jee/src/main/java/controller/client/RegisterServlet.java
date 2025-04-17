package controller.client;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import model.UtilisateurDB;
import model.Utilisateur_model;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UtilisateurDB utilisateurDB;

    @Override
    public void init() throws ServletException {
        utilisateurDB = new UtilisateurDB();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Afficher la page d'inscription
        request.getRequestDispatcher("/jsp/client/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String nom = request.getParameter("nom");
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            // Vérifier si l'email existe déjà
            Utilisateur_model existingUser = utilisateurDB.trouverUtilisateurParEmail(email);
            if (existingUser != null) {
                response.sendRedirect(request.getContextPath() + "/register?error=true");
                return;
            }

            // Créer un nouvel utilisateur
            Utilisateur_model utilisateur = new Utilisateur_model();
            utilisateur.setId(UUID.randomUUID().toString());
            utilisateur.setNom(nom);
            utilisateur.setEmail(email);
            utilisateur.setMotDePasse(password); // Le mot de passe sera haché par UtilisateurDB
            utilisateur.setEstAdmin(false); // Client, pas admin
            utilisateur.setImage(null); // Pas d'image pour l'instant

            // Ajouter l'utilisateur à la base de données
            utilisateurDB.ajouterUtilisateur(utilisateur);

            // Rediriger vers la page d'inscription avec un message de succès
            response.sendRedirect(request.getContextPath() + "/register?success=true");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/register?error=true");
        }
    }
}