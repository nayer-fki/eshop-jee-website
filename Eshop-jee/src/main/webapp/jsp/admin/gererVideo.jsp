<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.PromotionalVideo_model" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gérer la Vidéo Promotionnelle - E-Shop Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/videoAdmin.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" integrity="sha512-Fo3rlrZj/k7ujTnHg4CGR2D7kSs0v4LLanw2qksYuRlEzO+tcaEPQogQ0KaoGN26/zrn20ImR1DfuLWnOo7aBA==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>
<div class="gererVideo">
    <div class="video-management">
        <h2><i class="fas fa-video"></i> Gérer la Vidéo Promotionnelle</h2>
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

        <!-- Form to Update Video URL -->
        <div class="video-form-container">
            <form action="${pageContext.request.contextPath}/gererVideo" method="post" class="video-form">
                <input type="hidden" name="action" value="update">
                <%
                    PromotionalVideo_model video = (PromotionalVideo_model) request.getAttribute("video");
                    String videoUrl = (video != null && video.getVideoUrl() != null) ? video.getVideoUrl() : "";
                    int videoId = (video != null) ? video.getId() : 0;
                %>
                <input type="hidden" name="id" value="<%= videoId %>">
                <div class="form-group">
                    <label for="videoUrl"><i class="fas fa-link"></i> URL de la vidéo (YouTube Embed) :</label>
                    <input type="url" name="videoUrl" id="videoUrl" value="<%= videoUrl %>" placeholder="Ex: https://www.youtube.com/embed/..." required>
                </div>
                <div class="form-group preview-group">
                    <label><i class="fas fa-eye"></i> Aperçu de la vidéo :</label>
                    <div class="video-preview">
                        <% if (!videoUrl.isEmpty()) { %>
                            <iframe width="100%" height="200" src="<%= videoUrl %>" title="Aperçu de la vidéo" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
                        <% } else { %>
                            <p class="no-preview">Aucune vidéo à prévisualiser. Entrez une URL pour voir l'aperçu.</p>
                        <% } %>
                    </div>
                </div>
                <div class="form-actions">
                    <button type="submit" class="btn-update"><i class="fas fa-save"></i> Mettre à jour</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    // Update the preview dynamically when the URL changes
    document.getElementById('videoUrl').addEventListener('input', function() {
        const url = this.value;
        const previewDiv = document.querySelector('.video-preview');
        if (url) {
            previewDiv.innerHTML = `<iframe width="100%" height="200" src="${url}" title="Aperçu de la vidéo" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>`;
        } else {
            previewDiv.innerHTML = '<p class="no-preview">Aucune vidéo à prévisualiser. Entrez une URL pour voir l\'aperçu.</p>';
        }
    });
</script>
</body>
</html>