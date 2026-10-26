<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="seccion" value="menu"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Menú principal - Conversión de Unidades Monster</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<%@ include file="/ec/edu/monster/vista/fragmentos/cabecera.jspf" %>
<main class="contenido">
    <h1>Menú principal</h1>
    <div class="opciones-menu">
        <a class="opcion-menu" href="${pageContext.request.contextPath}/catalogo">
            <span class="opcion-titulo">Consultar catálogo</span>
            <span class="opcion-detalle">Ver categorías y unidades disponibles</span>
        </a>
        <a class="opcion-menu opcion-principal" href="${pageContext.request.contextPath}/conversion">
            <span class="opcion-titulo">Convertir unidades</span>
            <span class="opcion-detalle">Longitud, masa, volumen, tiempo, temperatura y área</span>
        </a>
    </div>
</main>
</body>
</html>
