<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="seccion" value="conversion"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Convertir unidades - Conversión de Unidades Monster</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<%@ include file="/ec/edu/monster/vista/fragmentos/cabecera.jspf" %>
<main class="contenido">
    <h1>Convertir unidades</h1>
    <c:if test="${not empty error}">
        <p class="mensaje-error" role="alert"><c:out value="${error}"/></p>
    </c:if>
    <form id="formulario-conversion" class="tarjeta tarjeta-conversion" method="post"
          action="${pageContext.request.contextPath}/conversion" novalidate>
        <div class="fila-campos">
            <div>
                <label for="magnitud">Categoría</label>
                <select id="magnitud" name="magnitud">
                    <c:if test="${empty magnitud}">
                        <option value="" selected>Seleccione una magnitud</option>
                    </c:if>
                    <c:forEach var="m" items="${magnitudes}">
                        <option value="<c:out value='${m.valor}'/>" ${m.valor == magnitud ? 'selected' : ''}><c:out value="${m.texto}"/></option>
                    </c:forEach>
                </select>
            </div>
            <div>
                <label for="desde">Desde</label>
                <select id="desde" name="desde">
                    <c:if test="${empty unidades}">
                        <option value="">Sin unidades</option>
                    </c:if>
                    <c:forEach var="u" items="${unidades}">
                        <option value="<c:out value='${u.valor}'/>" ${u.valor == desde ? 'selected' : ''}><c:out value="${u.texto}"/> (<c:out value="${u.valor}"/>)</option>
                    </c:forEach>
                </select>
            </div>
            <div>
                <label for="hacia">Hacia</label>
                <select id="hacia" name="hacia">
                    <c:if test="${empty unidades}">
                        <option value="">Sin unidades</option>
                    </c:if>
                    <c:forEach var="u" items="${unidades}">
                        <option value="<c:out value='${u.valor}'/>" ${u.valor == hacia ? 'selected' : ''}><c:out value="${u.texto}"/> (<c:out value="${u.valor}"/>)</option>
                    </c:forEach>
                </select>
            </div>
        </div>
        <div class="campo-valor">
            <label for="valor">Valor</label>
            <input type="text" id="valor" name="valor" value="<c:out value='${valor}'/>"
                   inputmode="decimal" autocomplete="off">
        </div>
        <button type="submit" name="accion" value="convertir" class="boton">Convertir</button>
        <noscript>
            <button type="submit" name="accion" value="cambiar" class="boton boton-secundario">Actualizar unidades</button>
        </noscript>
    </form>
    <c:if test="${not empty resultado}">
        <section class="resultado" aria-live="polite">
            <p class="resultado-etiqueta">Resultado</p>
            <p class="resultado-valor"><c:out value="${resultado}"/></p>
        </section>
    </c:if>
</main>
<script>
    // Changing the magnitude reloads its units without converting.
    (function () {
        var formulario = document.getElementById('formulario-conversion');
        document.getElementById('magnitud').addEventListener('change', function () {
            var accion = document.createElement('input');
            accion.type = 'hidden';
            accion.name = 'accion';
            accion.value = 'cambiar';
            formulario.appendChild(accion);
            formulario.submit();
        });
    })();
</script>
</body>
</html>
