<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Iniciar sesión - Conversión de Unidades Monster</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<%@ include file="/ec/edu/monster/vista/fragmentos/cabecera.jspf" %>
<main class="contenido-centrado">
    <section class="tarjeta tarjeta-login">
        <h1>Iniciar sesión</h1>
        <form method="post" action="${pageContext.request.contextPath}/login" novalidate>
            <label for="usuario">Usuario</label>
            <input type="text" id="usuario" name="usuario" value="<c:out value='${usuario}'/>"
                   autocomplete="username" autofocus>
            <label for="clave">Contraseña</label>
            <input type="password" id="clave" name="clave" autocomplete="current-password">
            <button type="submit" class="boton">Ingresar</button>
        </form>
    </section>
</main>
<c:if test="${not empty error}">
    <dialog id="dialogo-error" class="dialogo" open>
        <h2>Error de autenticación</h2>
        <p><c:out value="${error}"/></p>
        <form method="dialog">
            <button type="submit" class="boton">Aceptar</button>
        </form>
    </dialog>
    <script>
        // Reopen as a modal dialog so the page behind is dimmed, as in the mockup.
        (function () {
            var dialogo = document.getElementById('dialogo-error');
            if (dialogo && dialogo.showModal) {
                dialogo.close();
                dialogo.showModal();
            }
        })();
    </script>
</c:if>
</body>
</html>
