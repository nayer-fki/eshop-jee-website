<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>E-Shop - Inscription</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/clientStyle.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <style>
        /* Register Page Specific Styles */
        .register-container {
            max-width: 500px;
            margin: 50px auto;
            padding: 30px;
            background: linear-gradient(135deg, #ffffff, #f1f5f9);
            border-radius: 12px;
            box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
        }

        .register-container h2 {
            font-size: 2.2rem;
            color: #2d3748;
            text-align: center;
            margin-bottom: 30px;
            font-weight: 700;
            text-transform: uppercase;
        }

        .register-form {
            display: flex;
            flex-direction: column;
            gap: 20px;
        }

        .form-group {
            display: flex;
            flex-direction: column;
            gap: 8px;
        }

        .form-group label {
            font-size: 1.1rem;
            color: #4a5568;
            font-weight: 500;
        }

        .form-group input {
            padding: 12px;
            font-size: 1rem;
            border: 1px solid #e2e8f0;
            border-radius: 6px;
            outline: none;
            transition: border-color 0.3s ease;
        }

        .form-group input:focus {
            border-color: #4CAF50;
        }

        .error-message {
            color: #e53e3e;
            font-size: 0.9rem;
            text-align: center;
            margin-bottom: 15px;
        }

        .success-message {
            color: #2ecc71;
            font-size: 0.9rem;
            text-align: center;
            margin-bottom: 15px;
        }

        .register-btn {
            padding: 14px;
            background: linear-gradient(90deg, #4CAF50, #2ecc71);
            color: #fff;
            border: none;
            border-radius: 6px;
            font-size: 1.1rem;
            font-weight: 600;
            cursor: pointer;
            transition: background-color 0.3s ease, transform 0.2s ease;
        }

        .register-btn:hover {
            background: linear-gradient(90deg, #45a049, #27ae60);
            transform: translateY(-2px);
        }

        .login-link {
            text-align: center;
            margin-top: 20px;
            font-size: 0.9rem;
            color: #4a5568;
        }

        .login-link a {
            color: #4CAF50;
            text-decoration: none;
            font-weight: 500;
        }

        .login-link a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <header class="header">
        <div class="container">
            <div class="logo">
                <h1><a href="${pageContext.request.contextPath}/index">E-Shop</a></h1>
            </div>
            <nav class="nav">
                <ul>
                    <li><a href="${pageContext.request.contextPath}/index">Accueil</a></li>
                    <li><a href="${pageContext.request.contextPath}/cart">Panier <i class="fas fa-shopping-cart"></i></a></li>
                    <li><a href="${pageContext.request.contextPath}/login">Connexion</a></li>
                </ul>
            </nav>
        </div>
    </header>

    <main class="main-content">
        <div class="register-container">
            <h2>Inscription</h2>
            <% String error = request.getParameter("error"); %>
            <% String success = request.getParameter("success"); %>
            <% if (error != null) { %>
                <p class="error-message">Erreur lors de l'inscription. Veuillez réessayer.</p>
            <% } %>
            <% if (success != null) { %>
                <p class="success-message">Inscription réussie ! Vous pouvez maintenant vous connecter.</p>
            <% } %>
            <form action="${pageContext.request.contextPath}/register" method="post" class="register-form">
                <div class="form-group">
                    <label for="nom">Nom</label>
                    <input type="text" id="nom" name="nom" required>
                </div>
                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" required>
                </div>
                <div class="form-group">
                    <label for="password">Mot de passe</label>
                    <input type="password" id="password" name="password" required>
                </div>
                <button type="submit" class="register-btn">S'inscrire</button>
            </form>
            <div class="login-link">
                <p>Déjà un compte ? <a href="${pageContext.request.contextPath}/login">Se connecter</a></p>
            </div>
        </div>
    </main>

    <footer class="footer">
        <div class="container">
            <p>© 2025 E-Shop. Tous droits réservés.</p>
        </div>
    </footer>
</body>
</html>