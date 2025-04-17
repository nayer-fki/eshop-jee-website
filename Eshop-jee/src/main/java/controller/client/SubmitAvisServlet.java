package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.util.UUID;
import model.AvisDB;
import model.Evaluation;
import model.Commentaire;
import model.Utilisateur_model;

@WebServlet("/submitAvis")
public class SubmitAvisServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AvisDB avisDB;

    @Override
    public void init() throws ServletException {
        avisDB = new AvisDB();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            Utilisateur_model utilisateur = (Utilisateur_model) session.getAttribute("utilisateur");
            if (utilisateur == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            String productId = request.getParameter("productId");
            String redirectUrl = request.getContextPath() + "/produitDetails?id=" + productId;

            // Handle evaluation submission
            String rating = request.getParameter("rating");
            if (rating != null && !rating.isEmpty()) {
                int note = Integer.parseInt(rating);
                if (note >= 1 && note <= 5) { // Ensure rating is between 1 and 5
                    Evaluation evaluation = new Evaluation();
                    evaluation.setId(UUID.randomUUID().toString());
                    evaluation.setIdUtilisateur(utilisateur.getId());
                    evaluation.setProduitId(productId);
                    evaluation.setNote(note);
                    avisDB.ajouterEvaluation(evaluation);
                    System.out.println("SubmitAvisServlet: Added evaluation for product " + productId + " by user " + utilisateur.getId());
                }
            }

            // Handle comment submission
            String comment = request.getParameter("comment");
            if (comment != null && !comment.trim().isEmpty()) {
                Commentaire commentaire = new Commentaire();
                commentaire.setId(UUID.randomUUID().toString());
                commentaire.setIdUtilisateur(utilisateur.getId());
                commentaire.setProduitId(productId);
                commentaire.setCommentaire(comment.trim());
                commentaire.setDateCreation(new Date(System.currentTimeMillis()));
                avisDB.ajouterCommentaire(commentaire);
                System.out.println("SubmitAvisServlet: Added comment for product " + productId + " by user " + utilisateur.getId());
            }

            // Redirect back to the product details page
            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            System.err.println("SubmitAvisServlet: Error during submission for user: " + 
                    (request.getSession().getAttribute("utilisateur") != null ?
                    ((Utilisateur_model) request.getSession().getAttribute("utilisateur")).getId() : "unknown"));
            throw new ServletException("Erreur lors de la soumission de l'avis", e);
        }
    }
}