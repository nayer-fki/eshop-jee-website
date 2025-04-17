<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Utilisateur_model" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Paramètres de compte - E-Shop Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/parametreAdmin.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" integrity="sha512-Fo3rlrZj/k7ujTnHg4CGR2D7kSs0v4LLanw2qksYuRlEzO+tcaEPQogQ0KaoGN26/zrn20ImR1DfuLWnOo7aBA==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>
<div class="parametreAdmin">
    <div class="account-settings">
        <h2><i class="fas fa-cog"></i> Paramètres de compte</h2>
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

        <%
            Utilisateur_model admin = (Utilisateur_model) session.getAttribute("adminUser");
            if (admin != null) {
        %>
        <form action="${pageContext.request.contextPath}/parametresCompte" method="post" enctype="multipart/form-data" id="accountForm">
            <input type="hidden" name="action" value="update">
            <div class="form-group">
                <label><i class="fas fa-user"></i> Nom :</label>
                <input type="text" name="nom" id="nom" value="<%= admin.getNom() != null ? admin.getNom() : "" %>" required>
            </div>
            <div class="form-group">
                <label><i class="fas fa-envelope"></i> Email :</label>
                <input type="email" name="email" id="email" value="<%= admin.getEmail() != null ? admin.getEmail() : "" %>" required>
            </div>
            <div class="form-group">
                <label><i class="fas fa-lock"></i> Ancien mot de passe :</label>
                <input type="password" name="oldPassword" id="oldPassword" placeholder="Entrez votre ancien mot de passe">
                <small class="form-text">Requis si vous changez le mot de passe.</small>
            </div>
            <div class="form-group">
                <label><i class="fas fa-lock"></i> Nouveau mot de passe (laisser vide pour ne pas changer) :</label>
                <input type="password" name="motDePasse" id="motDePasse" placeholder="Entrez un nouveau mot de passe">
                <small class="form-text">Minimum 8 caractères, incluant des lettres et des chiffres.</small>
            </div>
            <div class="form-group profile-section">
                <label><i class="fas fa-camera"></i> Photo de profil :</label>
                <div class="profile-image">
                    <img src="${pageContext.request.contextPath}/<%= admin.getImage() != null && !admin.getImage().isEmpty() ? admin.getImage() : "images/user-def.jpg" %>"
                         alt="Profile Image" id="profileImagePreview"
                         onerror="this.src='${pageContext.request.contextPath}/images/user-def.jpg';">
                </div>
                <input type="file" name="image" id="image" accept="image/*" onchange="previewImage(event)">
            </div>
            <button type="submit" class="btn-save"><i class="fas fa-save"></i> Enregistrer les modifications</button>
        </form>
        <% } else { %>
            <p>Erreur : Impossible de charger les informations de l'utilisateur.</p>
        <% } %>
    </div>
</div>

<script>
    function previewImage(event) {
        const reader = new FileReader();
        reader.onload = function() {
            const output = document.getElementById('profileImagePreview');
            output.src = reader.result;
        };
        reader.readAsDataURL(event.target.files[0]);
    }

    document.getElementById('accountForm').addEventListener('submit', function(event) {
        const email = document.getElementById('email').value;
        const oldPassword = document.getElementById('oldPassword').value;
        const motDePasse = document.getElementById('motDePasse').value;
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        
        if (!emailRegex.test(email)) {
            event.preventDefault();
            alert('Veuillez entrer une adresse email valide.');
            return;
        }

        if (motDePasse && motDePasse.length > 0) {
            if (motDePasse.length < 8 || !/[a-zA-Z]/.test(motDePasse) || !/[0-9]/.test(motDePasse)) {
                event.preventDefault();
                alert('Le mot de passe doit contenir au moins 8 caractères, incluant des lettres et des chiffres.');
                return;
            }
            if (!oldPassword || oldPassword.trim() === '') {
                event.preventDefault();
                alert('Veuillez entrer votre ancien mot de passe pour changer le mot de passe.');
                return;
            }
        }
    });
</script>
</body>
</html>