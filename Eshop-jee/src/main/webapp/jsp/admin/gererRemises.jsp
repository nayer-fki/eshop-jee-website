<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Produit_model" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gérer les remises - E-Shop Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/remiseAdmin.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" integrity="sha512-Fo3rlrZj/k7ujTnHg4CGR2D7kSs0v4LLanw2qksYuRlEzO+tcaEPQogQ0KaoGN26/zrn20ImR1DfuLWnOo7aBA==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>
<div class="gererRemises">
    <div class="remise-management">
        <h2><i class="fas fa-tags"></i> Gérer les Remises</h2>
        <div class="filter-search-bar">
            <form action="${pageContext.request.contextPath}/gererRemises" method="get" class="filter-form">
                <div class="filter-group">
                    <label for="discountFilter"><i class="fas fa-filter"></i> Filtrer :</label>
                    <select name="discountFilter" id="discountFilter" onchange="this.form.submit()">
                        <option value="all" <%= "all".equals(request.getParameter("discountFilter")) || request.getParameter("discountFilter") == null ? "selected" : "" %>>Tous les produits</option>
                        <option value="withDiscount" <%= "withDiscount".equals(request.getParameter("discountFilter")) ? "selected" : "" %>>Produits avec remise</option>
                        <option value="withoutDiscount" <%= "withoutDiscount".equals(request.getParameter("discountFilter")) ? "selected" : "" %>>Produits sans remise</option>
                    </select>
                </div>
                <div class="search-group">
                    <input type="text" name="query" value="${searchQuery}" placeholder="Rechercher par nom..." class="search-input">
                    <button type="submit" class="search-btn"><i class="fas fa-search"></i> Rechercher</button>
                </div>
            </form>
        </div>
        <div class="message error">
            <% if (request.getAttribute("error") != null) { %>
                <p><i class="fas fa-exclamation-triangle"></i> <%= request.getAttribute("error") %></p>
            <% } %>
        </div>
        <div class="message success">
            <% if (request.getAttribute("success") != null) { %>
                <p><i class="fas fa-check-circle"></i> <%= request.getAttribute("success") %></p>
            <% } %>
        </div>

        <!-- Form to Update Discount -->
        <div class="remise-form-container">
            <form action="${pageContext.request.contextPath}/gererRemises" method="post" class="remise-form" id="remiseForm">
                <input type="hidden" name="action" id="formAction" value="modifier">
                <input type="hidden" name="id" id="produitId">
                <div class="form-group">
                    <label for="nomProduit"><i class="fas fa-box"></i> Nom du produit :</label>
                    <input type="text" name="nomProduit" id="nomProduit" readonly>
                </div>
                <div class="form-group">
                    <label for="remise"><i class="fas fa-percentage"></i> Remise (%) :</label>
                    <input type="number" name="remise" id="remise" min="0" max="100" step="0.1" placeholder="Entrez la remise (0-100)" required>
                </div>
                <div class="form-actions">
                    <button type="submit" class="btn-update" id="submitBtn"><i class="fas fa-save"></i> Mettre à jour</button>
                    <button type="button" class="btn-cancel" onclick="hideForm()">Annuler</button>
                </div>
            </form>
        </div>

        <!-- Table of Products -->
        <div class="remise-table">
            <table>
                <thead>
                    <tr>
                        
                        <th>Nom</th>
                        <th>Prix (€)</th>
                        <th>Remise (%)</th>
                        <th>Prix après remise (€)</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        List<Produit_model> produits = (List<Produit_model>) request.getAttribute("produits");
                        if (produits != null && !produits.isEmpty()) {
                            for (Produit_model produit : produits) {
                    %>
                    <tr>
                        
                        <td><%= produit.getNom() %></td>
                        <td><%= String.format("%.2f", produit.getPrix()) %></td>
                        <td class="<%= produit.getRemise() > 0 ? "highlight-discount" : "" %>">
                            <%= String.format("%.1f", produit.getRemise()) %>
                        </td>
                        <td><%= produit.getRemise() > 0 ? String.format("%.2f", produit.getDiscountedPrice()) : "N/A" %></td>
                        <td>
                            <button class="btn-edit" onclick="editRemise('<%= produit.getId() %>', '<%= produit.getNom() %>', '<%= produit.getRemise() %>')"><i class="fas fa-edit"></i> Modifier</button>
                            <% if (produit.getRemise() > 0) { %>
                            <form action="${pageContext.request.contextPath}/gererRemises" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="supprimer">
                                <input type="hidden" name="id" value="<%= produit.getId() %>">
                                <button type="submit" class="btn-delete" onclick="return confirm('Êtes-vous sûr de vouloir supprimer la remise de ce produit ?')"><i class="fas fa-trash"></i> Supprimer</button>
                            </form>
                            <% } %>
                        </td>
                    </tr>
                    <%      }
                        } else { %>
                    <tr>
                        <td colspan="6" class="no-data">Aucun produit trouvé.</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script>
    function editRemise(id, nom, remise) {
        document.getElementById('remiseForm').style.display = 'block';
        document.getElementById('formAction').value = 'modifier';
        document.getElementById('produitId').value = id;
        document.getElementById('nomProduit').value = nom;
        document.getElementById('remise').value = remise;
        document.getElementById('submitBtn').innerHTML = '<i class="fas fa-save"></i> Mettre à jour';
        window.scrollTo({ top: document.getElementById('remiseForm').offsetTop - 20, behavior: 'smooth' });
    }

    function hideForm() {
        document.getElementById('remiseForm').style.display = 'none';
    }

    // Show form if there was an error or success during submission
    window.onload = function() {
        if ('${error}' !== '' || '${success}' !== '') {
            document.getElementById('remiseForm').style.display = 'block';
            window.scrollTo({ top: document.getElementById('remiseForm').offsetTop - 20, behavior: 'smooth' });
        }
    };
</script>
</body>
</html>