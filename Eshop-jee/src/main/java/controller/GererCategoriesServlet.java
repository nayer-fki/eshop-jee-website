package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Categorie_model;
import model.CategorieDB;

@WebServlet("/gererCategories")
public class GererCategoriesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(GererCategoriesServlet.class.getName());
    
    private CategorieDB categorieDB = new CategorieDB();

    @Override
    public void init() throws ServletException {
        System.out.println("GererCategoriesServlet initialized.");
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
            List<Categorie_model> categories = categorieDB.listerCategories();
            String query = request.getParameter("query");
            if (query != null && !query.trim().isEmpty()) {
                query = java.net.URLDecoder.decode(query, "UTF-8").trim();
                final String searchQuery = query.toLowerCase();
                categories = categories.stream()
                    .filter(c -> c.getNom() != null && c.getNom().toLowerCase().contains(searchQuery))
                    .collect(Collectors.toList());
                request.setAttribute("searchQuery", query);
            }
            LOGGER.log(Level.INFO, "Nombre de catégories récupérées : {0}", categories != null ? categories.size() : 0);
            request.setAttribute("categories", categories);
            request.setAttribute("page", "/jsp/admin/gererCategories.jsp");
            request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des catégories", e);
            request.setAttribute("error", "Erreur lors de la récupération des catégories : " + e.getMessage());
            request.setAttribute("page", "/jsp/admin/gererCategories.jsp");
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
        String id = request.getParameter("id");
        String nomCategorie = request.getParameter("nomCategorie");

        try {
            if ("ajouter".equals(action)) {
                if (nomCategorie != null && !nomCategorie.trim().isEmpty()) {
                    Categorie_model categorie = new Categorie_model();
                    categorie.setId("CAT" + System.currentTimeMillis());
                    categorie.setNom(nomCategorie);
                    categorieDB.ajouterCategorie(categorie);
                    request.setAttribute("success", "Catégorie ajoutée avec succès.");
                    LOGGER.log(Level.INFO, "Catégorie ajoutée avec succès : ID={0}", categorie.getId());
                } else {
                    throw new ServletException("Le nom de la catégorie est requis.");
                }
            } else if ("modifier".equals(action)) {
                if (id != null && nomCategorie != null && !nomCategorie.trim().isEmpty()) {
                    Categorie_model categorie = categorieDB.trouverCategorie(id);
                    if (categorie != null) {
                        categorie.setNom(nomCategorie);
                        categorieDB.modifierCategorie(categorie);
                        request.setAttribute("success", "Catégorie modifiée avec succès.");
                        LOGGER.log(Level.INFO, "Catégorie modifiée avec succès : ID={0}", id);
                    } else {
                        throw new ServletException("Catégorie non trouvée.");
                    }
                } else {
                    throw new ServletException("Le nom de la catégorie et l'ID sont requis.");
                }
            } else if ("supprimer".equals(action)) {
                if (id != null) {
                    categorieDB.supprimerCategorie(id);
                    request.setAttribute("success", "Catégorie supprimée avec succès.");
                    LOGGER.log(Level.INFO, "Catégorie supprimée avec succès : ID={0}", id);
                } else {
                    throw new ServletException("L'ID de la catégorie est requis.");
                }
            }

            // Reload categories and forward to the same page to show updated list
            List<Categorie_model> categories = categorieDB.listerCategories();
            String query = request.getParameter("query");
            if (query != null && !query.trim().isEmpty()) {
                query = java.net.URLDecoder.decode(query, "UTF-8").trim();
                final String searchQuery = query.toLowerCase();
                categories = categories.stream()
                    .filter(c -> c.getNom() != null && c.getNom().toLowerCase().contains(searchQuery))
                    .collect(Collectors.toList());
                request.setAttribute("searchQuery", query);
            }
            request.setAttribute("categories", categories);
            request.setAttribute("page", "/jsp/admin/gererCategories.jsp");
            request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'opération sur la catégorie", e);
            request.setAttribute("error", "Erreur lors de l'opération : " + e.getMessage());
            request.setAttribute("page", "/jsp/admin/gererCategories.jsp");
            request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
        } catch (ServletException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'opération", e);
            request.setAttribute("error", e.getMessage());
            request.setAttribute("page", "/jsp/admin/gererCategories.jsp");
            request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
        }
    }
}