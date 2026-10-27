// Keeps the header's server status up to date: asks /estado right away and
// then every few seconds, so a server that goes down turns the dot red.
(function () {
    var INTERVALO_MS = 5000;
    var estado = document.getElementById('estado-servicio');
    if (!estado) {
        return;
    }
    var punto = estado.querySelector('.estado-punto');
    var texto = estado.querySelector('.estado-texto');
    var url = estado.getAttribute('data-url');

    function mostrar(disponible) {
        punto.classList.toggle('inactivo', !disponible);
        texto.textContent = disponible ? 'Servicio disponible' : 'Servicio no disponible';
    }

    function revisar() {
        fetch(url, {cache: 'no-store'})
            .then(function (respuesta) {
                return respuesta.ok ? respuesta.json() : {disponible: false};
            })
            .then(function (datos) {
                mostrar(datos.disponible === true);
            })
            .catch(function () {
                mostrar(false);
            });
    }

    revisar();
    setInterval(revisar, INTERVALO_MS);
})();
