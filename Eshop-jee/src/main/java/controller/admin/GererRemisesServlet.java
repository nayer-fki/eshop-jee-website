package controller.admin;

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
import model.ProduitDB;
import model.Produit_model;

@WebServlet("/gererRemises")
public class GererRemisesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(GererRemisesServlet.class.getName());
    
    private ProduitDB produitDB = new ProduitDB();

    @Override
    public void init() throws ServletException {
        System.out.println("GererRemisesServlet initialized.");
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
            // Get filter and search parameters
            String discountFilter = request.getParameter("discountFilter");
            String query = request.getParameter("query");

            // Fetch products
            List<Produit_model> produits;
            if ("withDiscount".equals(discountFilter)) {
                produits = produitDB.getDiscountedProducts();
            } else if ("withoutDiscount".equals(discountFilter)) {
                produits = produitDB.getAllProducts().stream()
                    .filter(p -> p.getRemise() <= 0)
                    .collect(Collectors.toList());
            } else {
                produits = produitDB.getAllProducts();
            }

            // Apply search filter if query is present
            if (query != null && !query.trim().isEmpty()) {
                query = java.net.URLDecoder.decode(query, "UTF-8").trim();
                final String searchQuery = query.toLowerCase();
                produits = produits.stream()
                    .filter(p -> p.getNom() != null && p.getNom().toLowerCase().contains(searchQuery))
                    .collect(Collectors.toList());
                request.setAttribute("searchQuery", query);
            }

            LOGGER.log(Level.INFO, "Nombre de produits récupérés : {0}", produits != null ? produits.size() : 0);
            request.setAttribute("produits", produits);
            request.setAttribute("page", "/jsp/admin/gererRemises.jsp");
            request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des produits", e);
            request.setAttribute("error", "Erreur lors de la récupération des produits : " + e.getMessage());
            request.setAttribute("page", "/jsp/admin/gererRemises.jsp");
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
        String remiseStr = request.getParameter("remise");

        try {
            if ("modifier".equals(action)) {
                if (id != null && remiseStr != null && !remiseStr.trim().isEmpty()) {
                    double remise = Double.parseDouble(remiseStr);
                    if (remise < 0 || remise > 100) {
                        throw new ServletException("La remise doit être comprise entre 0 et 100%.");
                    }
                    Produit_model produit = produitDB.getProductById(id);
                    if (produit != null) {
                        produit.setRemise(remise);
                        produitDB.modifierProduit(produit);
                        request.setAttribute("success", "Remise mise à jour avec succès.");
                        LOGGER.log(Level.INFO, "Remise mise à jour avec succès : ID={0}, Remise={1}%", new Object[]{id, remise});
                    } else {
                        throw new ServletException("Produit non trouvé.");
                    }
                } else {
                    throw new ServletException("L'ID du produit et la remise sont requis.");
                }
            } else if ("supprimer".equals(action)) {
                if (id != null) {
                    Produit_model produit = produitDB.getProductById(id);
                    if (produit != null) {
                        produit.setRemise(0.0);
                        produitDB.modifierProduit(produit);
                        request.setAttribute("success", "Remise supprimée avec succès.");
                        LOGGER.log(Level.INFO, "Remise supprimée avec succès : ID={0}", id);
                    } else {
                        throw new ServletException("Produit non trouvé.");
                    }
                } else {
                    throw new ServletException("L'ID du produit est requis.");
                }
            }

            // Reload products and forward to the same page to show updated list
            String discountFilter = request.getParameter("discountFilter");
            String query = request.getParameter("query");
            List<Produit_model> produits;
            if ("withDiscount".equals(discountFilter)) {
                produits = produitDB.getDiscountedProducts();
            } else if ("withoutDiscount".equals(discountFilter)) {
                produits = produitDB.getAllProducts().stream()
                    .filter(p -> p.getRemise() <= 0)
                    .collect(Collectors.toList());
            } else {
                produits = produitDB.getAllProducts();
            }

            if (query != null && !query.trim().isEmpty()) {
                query = java.net.URLDecoder.decode(query, "UTF-8").trim();
                final String searchQuery = query.toLowerCase();
                produits = produits.stream()
                    .filter(p -> p.getNom() != null && p.getNom().toLowerCase().contains(searchQuery))
                    .collect(Collectors.toList());
                request.setAttribute("searchQuery", query);
            }

            request.setAttribute("produits", produits);
            request.setAttribute("page", "/jsp/admin/gererRemises.jsp");
            request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'opération sur la remise", e);
            request.setAttribute("error", "Erreur lors de l'opération : " + e.getMessage());
            request.setAttribute("page", "/jsp/admin/gererRemises.jsp");
            request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
        } catch (ServletException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'opération", e);
            request.setAttribute("error", e.getMessage());
            request.setAttribute("page", "/jsp/admin/gererRemises.jsp");
            request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Erreur de format de la remise", e);
            request.setAttribute("error", "La remise doit être un nombre valide.");
            request.setAttribute("page", "/jsp/admin/gererRemises.jsp");
            request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
        }
    }
}