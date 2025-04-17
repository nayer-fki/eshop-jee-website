package controller.client;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import model.UtilisateurDB;
import model.Utilisateur_model;

@WebServlet("/profile")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10,      // 10MB
                 maxRequestSize = 1024 * 1024 * 50)   // 50MB
public class ProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UtilisateurDB utilisateurDB;
    private static final String UPLOAD_DIR = "uploads";

    @Override
    public void init() throws ServletException {
        utilisateurDB = new UtilisateurDB();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Utilisateur_model utilisateur = (Utilisateur_model) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        // Generate CSRF token if not present
        if (session.getAttribute("csrfToken") == null) {
            session.setAttribute("csrfToken", java.util.UUID.randomUUID().toString());
        }
        request.getRequestDispatcher("/jsp/client/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Utilisateur_model utilisateur = (Utilisateur_model) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Verify CSRF token
        String submittedToken = request.getParameter("csrfToken");
        String sessionToken = (String) session.getAttribute("csrfToken");
        if (submittedToken == null || !submittedToken.equals(sessionToken)) {
            request.setAttribute("errorMessage", "Erreur de sécurité : jeton CSRF invalide.");
            request.getRequestDispatcher("/jsp/client/profile.jsp").forward(request, response);
            return;
        }

        // Get current password from form
        String currentPassword = request.getParameter("currentPassword");
        if (!utilisateurDB.verifierMotDePasse(utilisateur.getEmail(), currentPassword)) {
            request.setAttribute("errorMessage", "Mot de passe actuel incorrect.");
            request.getRequestDispatcher("/jsp/client/profile.jsp").forward(request, response);
            return;
        }

        // Get form data
        String nom = request.getParameter("nom");
        String email = request.getParameter("email");
        String motDePasse = request.getParameter("motDePasse");
        Part filePart = request.getPart("image");

        // Preserve the existing estAdmin value
        boolean existingEstAdmin = utilisateur.isEstAdmin();

        // Update user details
        utilisateur.setNom(nom);
        utilisateur.setEmail(email);
        if (motDePasse != null && !motDePasse.isEmpty()) {
            utilisateur.setMotDePasse(motDePasse); // Assuming this hashes the password internally
        }
        utilisateur.setEstAdmin(existingEstAdmin); // Ensure estAdmin is not overwritten

        // Handle image upload
        if (filePart != null && filePart.getSize() > 0) {
            String fileName = extractFileName(filePart);
            String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String filePath = uploadPath + File.separator + fileName;
            filePart.write(filePath);
            utilisateur.setImage(UPLOAD_DIR + "/" + fileName);
        }

        try {
            utilisateurDB.modifierUtilisateur(utilisateur);
            session.setAttribute("utilisateur", utilisateur);
            request.setAttribute("successMessage", "Profil mis à jour avec succès.");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Erreur lors de la mise à jour du profil : " + e.getMessage());
        }

        request.getRequestDispatcher("/jsp/client/profile.jsp").forward(request, response);
    }

    private String extractFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        String[] items = contentDisposition.split(";");
        for (String item : items) {
            if (item.trim().startsWith("filename")) {
                return item.substring(item.indexOf("=") + 2, item.length() - 1);
            }
        }
        return "";
    }
}