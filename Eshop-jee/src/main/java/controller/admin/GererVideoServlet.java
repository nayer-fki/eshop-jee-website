package controller.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.PromotionalVideoDB;
import model.PromotionalVideo_model;

@WebServlet("/gererVideo")
public class GererVideoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(GererVideoServlet.class.getName());
    
    private PromotionalVideoDB videoDB = new PromotionalVideoDB();

    @Override
    public void init() throws ServletException {
        System.out.println("GererVideoServlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null || !(boolean) session.getAttribute("admin")) {
            response.sendRedirect(request.getContextPath() + "/LoginAdmin");
            return;
        }

        try {
            PromotionalVideo_model video = videoDB.getPromotionalVideo();
            request.setAttribute("video", video);
            request.setAttribute("page", "/jsp/admin/gererVideo.jsp");
            request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de la vidéo", e);
            request.setAttribute("error", "Erreur lors de la récupération de la vidéo : " + e.getMessage());
            request.setAttribute("page", "/jsp/admin/gererVideo.jsp");
            request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null || !(boolean) session.getAttribute("admin")) {
            response.sendRedirect(request.getContextPath() + "/LoginAdmin");
            return;
        }

        String action = request.getParameter("action");
        if ("update".equals(action)) {
            String idStr = request.getParameter("id");
            String videoUrl = request.getParameter("videoUrl");

            try {
                if (videoUrl == null || videoUrl.trim().isEmpty()) {
                    throw new ServletException("L'URL de la vidéo est requise.");
                }

                // Validate the URL (basic check for YouTube embed format)
                if (!videoUrl.matches("https://www\\.youtube\\.com/embed/[A-Za-z0-9_-]+")) {
                    throw new ServletException("L'URL doit être une URL d'intégration YouTube valide (ex: https://www.youtube.com/embed/...)");
                }

                PromotionalVideo_model video = new PromotionalVideo_model();
                video.setId(idStr != null && !idStr.isEmpty() ? Integer.parseInt(idStr) : 0);
                video.setVideoUrl(videoUrl);

                videoDB.updatePromotionalVideo(video);
                request.setAttribute("success", "Vidéo mise à jour avec succès.");
                LOGGER.log(Level.INFO, "Vidéo mise à jour avec succès : URL={0}", videoUrl);

                // Reload the video and forward to the same page
                video = videoDB.getPromotionalVideo();
                request.setAttribute("video", video);
                request.setAttribute("page", "/jsp/admin/gererVideo.jsp");
                request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour de la vidéo", e);
                request.setAttribute("error", "Erreur lors de la mise à jour de la vidéo : " + e.getMessage());
                request.setAttribute("page", "/jsp/admin/gererVideo.jsp");
                request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
            } catch (ServletException e) {
                LOGGER.log(Level.SEVERE, "Erreur lors de l'opération", e);
                request.setAttribute("error", e.getMessage());
                request.setAttribute("page", "/jsp/admin/gererVideo.jsp");
                request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
            }
        }
    }
}