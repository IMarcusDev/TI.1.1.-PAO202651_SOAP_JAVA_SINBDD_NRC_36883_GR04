<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="seccion" value="catalogo"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Consultar catálogo - Conversión de Unidades Monster</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<%@ include file="/ec/edu/monster/vista/fragmentos/cabecera.jspf" %>
<main class="contenido">
    <h1>Consultar catálogo</h1>
    <c:if test="${not empty error}">
        <p class="mensaje-error" role="alert"><c:out value="${error}"/></p>
    </c:if>
    <div class="catalogo">
        <nav aria-label="Magnitudes">
            <ul class="lista-magnitudes">
                <c:forEach var="m" items="${magnitudes}">
                    <c:url var="enlace" value="/catalogo">
                        <c:param name="magnitud" value="${m.valor}"/>
                    </c:url>
                    <li>
                        <a href="<c:out value='${enlace}'/>" class="${m.valor == magnitud ? 'activo' : ''}"
                           ${m.valor == magnitud ? 'aria-current="page"' : ''}><c:out value="${m.texto}"/></a>
                    </li>
                </c:forEach>
            </ul>
        </nav>
        <table class="tabla-unidades">
            <thead>
            <tr>
                <th scope="col">Nombre</th>
                <th scope="col">Símbolo</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="u" items="${unidades}">
                <tr>
                    <td><c:out value="${u.texto}"/></td>
                    <td><c:out value="${u.valor}"/></td>
                </tr>
            </c:forEach>
            <c:if test="${empty unidades}">
                <tr>
                    <td colspan="2">No hay unidades para mostrar</td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>
</main>
</body>
</html>
