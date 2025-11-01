const API_BASE = "/api"; // si tu servidor monta en /api
const params = new URLSearchParams(window.location.search);
const ordenId = params.get("id");

document.addEventListener("DOMContentLoaded", () => {
    if (!ordenId) {
        document.getElementById("timelineContainer").textContent = "No se indicó id de orden (usa ?id=123).";
        return;
    }
    document.getElementById("ordenIdTitle").textContent = ordenId;
    cargarHistorial();
});

async function cargarHistorial() {
    try {
        const res = await fetch(`${API_BASE}/ordenes/${ordenId}/historial`);
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const datos = await res.json();

        renderTimeline(datos);
        renderTable(datos);
    } catch (err) {
        console.error(err);
        document.getElementById("timelineContainer").textContent = "Error al cargar historial.";
    }
}

function renderTimeline(items) {
    const container = document.getElementById("timelineContainer");
    container.innerHTML = "";
    if (!items || items.length === 0) {
        container.textContent = "No hay eventos registrados para esta orden.";
        return;
    }

    items.forEach(ev => {
        const div = document.createElement("div");
        div.className = "timeline-item";

        // detecta eventos críticos
        const isTimeout = (ev.evento || "").toUpperCase().includes("TIMEOUT") || (ev.estadoGlobal && ev.estadoGlobal === "VENCIDA");
        const isCritical = (ev.evento || "").toUpperCase().includes("CRIT") || isTimeout;

        if (isTimeout) div.classList.add("event-timeout");
        else if (isCritical) div.classList.add("event-critical");

        div.innerHTML = `
      <div class="timeline-timestamp">${formatDate(ev.timestamp)}</div>
      <div class="timeline-badge">${escapeHtml(ev.actor || "sistema")}</div>
      <div class="timeline-detail">
        <strong>${escapeHtml(ev.evento)}</strong>
        <div>${escapeHtml(ev.detalle || "")}</div>
        <small>Estado global: ${escapeHtml(ev.estadoGlobal || "")}</small>
      </div>
    `;
        container.appendChild(div);
    });
}

function renderTable(items) {
    const body = document.getElementById("historialBody");
    body.innerHTML = "";
    items.forEach(ev => {
        const tr = document.createElement("tr");
        const isTimeout = (ev.evento || "").toUpperCase().includes("TIMEOUT") || (ev.estadoGlobal && ev.estadoGlobal === "VENCIDA");
        if (isTimeout) tr.classList.add("event-timeout");
        tr.innerHTML = `
      <td>${formatDate(ev.timestamp)}</td>
      <td>${escapeHtml(ev.actor || "sistema")}</td>
      <td>${escapeHtml(ev.evento)}</td>
      <td>${escapeHtml(ev.detalle || "")}</td>
      <td>${escapeHtml(ev.estadoGlobal || "")}</td>
    `;
        body.appendChild(tr);
    });
}

// utilitarios
function formatDate(iso) {
    try {
        const d = new Date(iso);
        return d.toLocaleString();
    } catch (e) { return iso; }
}
function escapeHtml(s) {
    if (!s) return "";
    return s.replaceAll("&","&amp;").replaceAll("<","&lt;").replaceAll(">","&gt;")
        .replaceAll('"',"&quot;").replaceAll("'", "&#39;");
}
