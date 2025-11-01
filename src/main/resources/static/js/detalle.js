const API_BASE = "http://localhost:8080/api"; // Ajusta según tu backend
const urlParams = new URLSearchParams(window.location.search);
const ordenId = urlParams.get("id");

document.addEventListener("DOMContentLoaded", async () => {
    if (!ordenId) return alert("No se especificó una orden.");

    const orden = await fetch(`${API_BASE}/ordenes/${ordenId}`).then(r => r.json());
    mostrarOrden(orden);

    const historial = await fetch(`${API_BASE}/ordenes/${ordenId}/historial`).then(r => r.json());
    mostrarHistorial(historial);
});

function mostrarOrden(orden) {
    document.getElementById("ordenId").textContent = orden.id;
    document.getElementById("ordenTitulo").textContent = orden.titulo;
    document.getElementById("ordenEstado").textContent = orden.estado_global;
    document.getElementById("ordenActualizada").textContent = orden.actualizada_en;

    const tbody = document.getElementById("tablaAreas");
    tbody.innerHTML = "";

    orden.areas.forEach(area => {
        const fila = document.createElement("tr");
        fila.innerHTML = `
            <td>${area.nombre}</td>
            <td>${area.responsable}</td>
            <td>${area.estado_parcial}</td>
            <td>${area.seg_acumulados}</td>
            <td>
                <button class="iniciar" onclick="accionArea(${orden.id}, ${area.id}, 'iniciar')">▶</button>
                <button class="pausar" onclick="accionArea(${orden.id}, ${area.id}, 'pausar')">⏸</button>
                <button class="completar" onclick="accionArea(${orden.id}, ${area.id}, 'completar')">✅</button>
                <button class="sin-solucion" onclick="accionArea(${orden.id}, ${area.id}, 'sin_solucion')">❌</button>
            </td>
        `;
        tbody.appendChild(fila);
    });
}

async function accionArea(ordenId, areaId, accion) {
    const confirmacion = confirm(`¿Deseas ${accion} esta subtarea?`);
    if (!confirmacion) return;

    const respuesta = await fetch(`${API_BASE}/ordenes/${ordenId}/areas/${areaId}`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ accion })
    });

    if (respuesta.ok) {
        alert("Acción realizada correctamente.");
        location.reload(); // refrescar datos
    } else {
        alert("Error al ejecutar acción.");
    }
}

function mostrarHistorial(historial) {
    const lista = document.getElementById("listaHistorial");
    lista.innerHTML = "";
    historial.forEach(h => {
        const item = document.createElement("li");
        item.textContent = `[${h.timestamp}] ${h.actor}: ${h.evento} → ${h.detalle}`;
        lista.appendChild(item);
    });
}
