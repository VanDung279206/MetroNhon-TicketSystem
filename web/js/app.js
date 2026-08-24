const app = document.querySelector("#app");
const toast = document.querySelector("#toast");

const state = {
    session: null,
    stations: [],
    tickets: [],
    admin: null,
    screen: "home",
    ticketType: "VE_LUOT",
    monthlyType: "PHO_THONG",
    from: "G01",
    to: "G08",
    historyFilter: "ALL",
    registerMode: false,
    mobileMenu: false
};

const money = value => new Intl.NumberFormat("vi-VN").format(value || 0) + " ₫";
const escapeHtml = value => String(value ?? "")
    .replaceAll("&", "&amp;").replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;").replaceAll('"', "&quot;");

async function api(path, options = {}) {
    const response = await fetch(path, { credentials: "same-origin", ...options });
    let data;
    try { data = await response.json(); }
    catch { data = { success: false, message: "Phản hồi không hợp lệ" }; }
    if (!response.ok) {
        const error = new Error(data.message || "Không thể thực hiện yêu cầu");
        error.status = response.status;
        throw error;
    }
    return data;
}

function formBody(values) {
    return new URLSearchParams(values).toString();
}

async function boot() {
    try {
        const stations = await api("/api/ga");
        state.stations = stations.stations;
        const session = await api("/api/phien");
        state.session = session;
        state.screen = session.role === "ADMIN" ? "admin" : "home";
        await loadData();
        render();
    } catch (error) {
        if (error.status === 401) renderAuth();
        else renderFatal(error.message);
    }
}

async function loadData() {
    if (!state.session) return;
    if (state.session.role === "ADMIN") {
        state.admin = await api("/api/admin/tong-quan");
    } else {
        const response = await api("/api/ve");
        state.tickets = response.tickets;
    }
}

function render() {
    if (!state.session) return renderAuth();
    const admin = state.session.role === "ADMIN";
    const nav = admin
        ? [{ id: "admin", icon: "▦", label: "Quản trị" }]
        : [
            { id: "home", icon: "⌂", label: "Trang chủ" },
            { id: "buy", icon: "🎫", label: "Mua vé" },
            { id: "stations", icon: "⌖", label: "Nhà ga" },
            { id: "history", icon: "≡", label: "Vé của tôi" }
        ];
    const initials = admin ? "AD" : initialsOf(state.session.displayName);

    app.innerHTML = `
        <div class="app-shell">
            <aside class="sidebar ${state.mobileMenu ? "is-open" : ""}">
                <div class="brand" data-screen="${admin ? "admin" : "home"}">
                    <span class="brand-mark">M</span>
                    <span><strong>Metro Nhổn</strong><small>Smart ticket</small></span>
                </div>
                <nav class="side-nav">
                    ${nav.map(item => `<button class="${state.screen === item.id ? "active" : ""}" data-screen="${item.id}"><span>${item.icon}</span><span>${item.label}</span></button>`).join("")}
                </nav>
                <div class="side-profile">
                    <div class="avatar">${initials}</div>
                    <div><strong>${escapeHtml(state.session.displayName)}</strong><small>${escapeHtml(state.session.username)}</small></div>
                    <button class="icon-btn" data-action="logout" title="Đăng xuất">↪</button>
                </div>
            </aside>
            ${state.mobileMenu ? `<button class="menu-backdrop" data-action="close-menu"></button>` : ""}
            <main class="main-area">
                <header class="topbar">
                    <button class="mobile-menu-btn" data-action="open-menu">☰</button>
                    <div class="mobile-brand"><span class="brand-mark">M</span><strong>Metro Nhổn</strong></div>
                    <div class="top-actions"><span class="status-pill"><i></i> Hệ thống hoạt động</span><button class="profile-pill" data-action="logout"><span>${initials}</span>${escapeHtml(state.session.displayName)}</button></div>
                </header>
                <div class="page-content">${renderScreen()}</div>
                ${admin ? "" : `<nav class="bottom-nav">${nav.map(item => `<button class="${state.screen === item.id ? "active" : ""}" data-screen="${item.id}"><b>${item.icon}</b><span>${item.label}</span></button>`).join("")}</nav>`}
            </main>
        </div>`;
}

function renderScreen() {
    if (state.session.role === "ADMIN") return renderAdmin();
    if (state.screen === "buy") return renderBuy();
    if (state.screen === "history") return renderHistory();
    if (state.screen === "stations") return renderStations();
    return renderHome();
}

function renderHome() {
    const active = state.tickets[0];
    return `
        <div>
            <section class="hero-card">
                <div class="hero-copy"><span class="eyebrow">TUYẾN NHỔN – CẦU GIẤY</span><h1>Đi metro nhẹ nhàng<br><em>mỗi ngày.</em></h1><p>Mua vé nhanh, theo dõi hành trình và lưu toàn bộ lịch sử ngay trên một ứng dụng.</p><button class="primary-btn light" data-screen="buy">Mua vé ngay <span>→</span></button></div>
                <div class="hero-route"><div class="route-badge"><span>🚆</span><span><strong>8 nhà ga</strong><small>15 phút toàn tuyến</small></span></div><div class="route-line">${"<i></i>".repeat(8)}</div><div class="route-ends"><span>Nhổn</span><span>Cầu Giấy</span></div></div>
            </section>
            <section class="quick-grid">
                ${quick("🎫", "Mua vé tàu", "Vé lượt & vé tháng", "blue", "buy")}
                ${quick("📍", "Tìm nhà ga", "8 ga gần bạn", "green", "stations")}
                ${quick("🧾", "Vé của tôi", `${state.tickets.length} vé đã lưu`, "orange", "history")}
                ${quick("🛡", "Hỗ trợ", "An toàn hành trình", "purple", "support")}
            </section>
            <div class="dashboard-grid">
                <section class="surface"><div class="section-head"><div><span class="eyebrow dark">CHUYẾN GẦN NHẤT</span><h2>Lịch tàu hôm nay</h2></div><button class="text-btn" data-screen="stations">Xem tất cả</button></div><div class="nearest"><div><span>Ga gần bạn</span><strong>Minh Khai</strong><small>1,4 km</small></div><button class="round-icon">➜</button></div><div class="departure"><i></i><div><span>Hướng</span><strong>Ga Cầu Giấy</strong></div><b>10 phút</b></div><div class="departure"><i></i><div><span>Hướng</span><strong>Ga Nhổn</strong></div><b>18 phút</b></div></section>
                <section class="surface active-ticket"><div class="section-head"><div><span class="eyebrow dark">VÉ ĐANG HOẠT ĐỘNG</span><h2>${active ? escapeHtml(active.id) : "Chưa có vé"}</h2></div>${active ? `<span class="live-dot">Đang dùng</span>` : ""}</div>${active ? `<div class="ticket-route"><span>Nhổn</span><div><i></i><i></i><i></i><span>🚆</span></div><span>Cầu Giấy</span></div><div class="ticket-meta"><span>Loại vé<strong>${displayTicketType(active.type)}</strong></span><span>Giá vé<strong>${money(active.price)}</strong></span></div>` : `<div class="empty-state"><div class="big-icon">🎫</div><p>Bạn chưa có vé nào.</p><button class="primary-btn" data-screen="buy">Mua vé đầu tiên</button></div>`}</section>
            </div>
        </div>`;
}

function quick(icon, label, note, color, action) {
    const attribute = action === "support" ? `data-action="support"` : `data-screen="${action}"`;
    return `<button class="quick-card" ${attribute}><span class="quick-icon ${color}">${icon}</span><span><strong>${label}</strong><small>${note}</small></span><span class="arrow">›</span></button>`;
}

function renderBuy() {
    const from = findStation(state.from);
    const to = findStation(state.to);
    const distance = from && to ? Math.abs(from.order - to.order) : 0;
    const valid = state.ticketType === "VE_THANG" || state.from !== state.to;
    const price = state.ticketType === "VE_LUOT"
        ? 8000 + distance * 1000
        : state.monthlyType === "PHO_THONG" ? 280000 : 140000;
    const route = state.ticketType === "VE_LUOT" ? `${from?.name || "—"} → ${to?.name || "—"}` : "Toàn tuyến";
    return `
        ${pageHeading("VÉ ĐIỆN TỬ", "Mua vé metro", "Chọn hành trình và loại vé phù hợp với bạn.")}
        <div class="purchase-layout">
            <section class="surface purchase-form">
                <div class="segmented"><button class="${state.ticketType === "VE_LUOT" ? "active" : ""}" data-ticket-type="VE_LUOT">Vé lượt</button><button class="${state.ticketType === "VE_THANG" ? "active" : ""}" data-ticket-type="VE_THANG">Vé tháng</button></div>
                ${state.ticketType === "VE_LUOT" ? `<div class="route-form"><label>Ga đi<select id="from-station">${stationOptions(state.from)}</select></label><button class="swap-btn" data-action="swap">⇅</button><label>Ga đến<select id="to-station">${stationOptions(state.to)}</select></label></div>` : `<div class="monthly-options"><button class="monthly-option ${state.monthlyType === "PHO_THONG" ? "active" : ""}" data-monthly="PHO_THONG"><span><strong>Phổ thông</strong><small>Dùng không giới hạn trong 30 ngày</small></span><b>280.000 ₫</b></button><button class="monthly-option ${state.monthlyType === "UU_DAI" ? "active" : ""}" data-monthly="UU_DAI"><span><strong>Ưu đãi</strong><small>Dành cho đối tượng đủ điều kiện</small></span><b>140.000 ₫</b></button></div>`}
                <div class="benefits"><span><b>✓</b> Lưu vé tự động</span><span><b>✓</b> Tra cứu bất cứ lúc nào</span><span><b>✓</b> Không cần in vé giấy</span></div>
            </section>
            <aside class="surface order-summary"><span class="eyebrow dark">TÓM TẮT ĐƠN HÀNG</span><h3>${state.ticketType === "VE_LUOT" ? "Vé lượt" : "Vé tháng"}</h3><div class="summary-row"><span>Hành trình</span><strong>${escapeHtml(route)}</strong></div><div class="summary-row"><span>Hiệu lực</span><strong>${state.ticketType === "VE_LUOT" ? "Trong ngày" : "30 ngày"}</strong></div><div class="summary-total"><span>Tổng thanh toán</span><strong>${valid ? money(price) : "—"}</strong></div>${valid ? "" : `<p class="form-error">Ga đi và ga đến phải khác nhau.</p>`}<button class="primary-btn full" data-action="purchase" ${valid ? "" : "disabled"}>Xác nhận mua vé →</button></aside>
        </div>`;
}

function stationOptions(selected) {
    return state.stations.map(station => `<option value="${station.id}" ${station.id === selected ? "selected" : ""}>${escapeHtml(station.id)} – ${escapeHtml(station.name)}</option>`).join("");
}

function renderHistory() {
    const visible = state.historyFilter === "ALL" ? state.tickets : state.tickets.filter(ticket => ticket.type === state.historyFilter);
    return `${pageHeading("HÀNH TRÌNH CỦA BẠN", "Vé của tôi", "Tất cả vé được lưu an toàn và dễ dàng tra cứu.", `<button class="primary-btn" data-screen="buy">+ Mua vé mới</button>`)}<div class="filter-row">${[["ALL","Tất cả"],["VE_LUOT","Vé lượt"],["VE_THANG","Vé tháng"]].map(([id,label]) => `<button class="${state.historyFilter === id ? "active" : ""}" data-filter="${id}">${label}</button>`).join("")}</div><div class="ticket-list">${visible.length ? visible.map(ticket => `<article class="ticket-item"><div class="ticket-type-icon">🎫</div><div class="ticket-main"><span>${displayTicketType(ticket.type)}</span><h3>${escapeHtml(ticket.description)}</h3><small>${formatDate(ticket.purchasedAt)}</small></div><div class="ticket-id"><small>Mã vé</small><strong>${escapeHtml(ticket.id)}</strong></div><div class="ticket-price"><strong>${money(ticket.price)}</strong><span>${ticket.active ? "Đang hoạt động" : "Đã hết hạn"}</span></div></article>`).join("") : `<div class="surface empty-state"><div class="big-icon">🎟</div><h3>Chưa có vé trong mục này</h3><p>Mua một vé mới để bắt đầu hành trình.</p></div>`}</div>`;
}

function renderStations() {
    return `${pageHeading("TUYẾN TRÊN CAO", "8 nhà ga Metro Nhổn", "Từ Nhổn tới Cầu Giấy, kết nối những điểm đến quan trọng.")}<section class="surface"><div class="station-line">${state.stations.map((station,index) => `<div class="station-row"><div class="station-index">${String(index + 1).padStart(2,"0")}</div><i></i><div><h3>Ga ${escapeHtml(station.name)}</h3><p>${escapeHtml(station.location)}</p></div><button class="text-btn" data-screen="buy">Mua vé</button></div>`).join("")}</div></section>`;
}

function renderAdmin() {
    const data = state.admin || { revenue: 0, ticketCount: 0, accounts: [] };
    const active = data.accounts.filter(account => account.active).length;
    return `${pageHeading("QUẢN TRỊ HỆ THỐNG", "Bảng điều khiển", "Theo dõi hoạt động bán vé và quản lý người dùng.")}<section class="admin-stats"><article class="stat-card blue"><span>Tổng doanh thu</span><strong>${money(data.revenue)}</strong><small>Đã cập nhật hôm nay</small></article><article class="stat-card green"><span>Vé đã bán</span><strong>${data.ticketCount}</strong><small>Lưu trong dữ liệu hệ thống</small></article><article class="stat-card orange"><span>Tài khoản hoạt động</span><strong>${active}</strong><small>Trên tổng số ${data.accounts.length}</small></article></section><section class="surface data-surface"><div class="section-head"><div><span class="eyebrow dark">NGƯỜI DÙNG</span><h2>Danh sách tài khoản</h2></div></div><div class="table-wrap"><table><thead><tr><th>Tên đăng nhập</th><th>Vai trò</th><th>Trạng thái</th><th>Thao tác</th></tr></thead><tbody>${data.accounts.map(account => `<tr><td><strong>${escapeHtml(account.username)}</strong></td><td>${displayRole(account.role)}</td><td><span class="state ${account.active ? "" : "off"}">${account.active ? "Hoạt động" : "Đã khóa"}</span></td><td>${account.role === "ADMIN" ? "—" : `<button class="table-action" data-account="${escapeHtml(account.username)}" data-active="${!account.active}">${account.active ? "Khóa" : "Mở khóa"}</button>`}</td></tr>`).join("")}</tbody></table></div></section>`;
}

function pageHeading(eyebrow, title, description, action = "") {
    return `<header class="page-heading"><div><span class="eyebrow dark">${eyebrow}</span><h1>${title}</h1><p>${description}</p></div>${action}</header>`;
}

function renderAuth(message = "") {
    state.session = null;
    app.innerHTML = `<main class="auth-screen"><section class="auth-visual"><div class="auth-brand"><span class="brand-mark">M</span><span><strong>Metro Nhổn</strong><small>Smart ticket</small></span></div><div><span class="eyebrow">DI CHUYỂN XANH MỖI NGÀY</span><h1>Một chạm<br>cho mọi hành trình.</h1><p>Vé điện tử nhanh chóng cho tuyến Nhổn – Cầu Giấy.</p></div><div class="auth-line">${"<i></i>".repeat(8)}</div></section><section class="auth-form-wrap"><form class="auth-form" id="auth-form"><span class="eyebrow dark">${state.registerMode ? "ĐĂNG KÝ HÀNH KHÁCH" : "CỔNG VÉ ĐIỆN TỬ"}</span><h2>${state.registerMode ? "Tạo tài khoản" : "Chào mừng trở lại"}</h2><p>${state.registerMode ? "Điền thông tin để bắt đầu hành trình." : "Đăng nhập để quản lý vé của bạn."}</p>${state.registerMode ? `<label>Họ và tên<input name="hoTen" required placeholder="Nguyễn Văn Dũng"></label>` : ""}<label>Tên đăng nhập<input name="tenDangNhap" required value="${state.registerMode ? "" : "admin"}" placeholder="Tên đăng nhập"></label><label>Mật khẩu<input name="matKhau" required type="password" value="${state.registerMode ? "" : "admin123"}" placeholder="••••••••"></label>${state.registerMode ? `<div class="auth-double"><label>Số điện thoại<input name="soDienThoai" required placeholder="09xxxxxxxx"></label><label>Email<input name="email" required type="email" placeholder="ban@email.com"></label></div>` : ""}<div class="auth-error">${escapeHtml(message)}</div><button class="primary-btn full" type="submit">${state.registerMode ? "Đăng ký tài khoản" : "Đăng nhập"} →</button><button class="switch-auth" type="button" data-action="switch-auth">${state.registerMode ? "Đã có tài khoản? Đăng nhập" : "Chưa có tài khoản? Đăng ký ngay"}</button><small class="demo-note">Tài khoản quản trị: admin / admin123</small></form></section></main>`;
}

function renderFatal(message) {
    app.innerHTML = `<div class="loading-screen"><div class="metro-loader">!</div><h2>Không thể khởi động ứng dụng</h2><p>${escapeHtml(message)}</p><button class="primary-btn" onclick="location.reload()">Thử lại</button></div>`;
}

app.addEventListener("click", async event => {
    const target = event.target.closest("button,[data-screen]");
    if (!target) return;
    if (target.dataset.screen) {
        state.screen = target.dataset.screen;
        state.mobileMenu = false;
        render();
        window.scrollTo({ top: 0, behavior: "smooth" });
        return;
    }
    if (target.dataset.ticketType) { state.ticketType = target.dataset.ticketType; render(); return; }
    if (target.dataset.monthly) { state.monthlyType = target.dataset.monthly; render(); return; }
    if (target.dataset.filter) { state.historyFilter = target.dataset.filter; render(); return; }
    if (target.dataset.account) { await updateAccount(target.dataset.account, target.dataset.active === "true"); return; }

    switch (target.dataset.action) {
        case "logout": await logout(); break;
        case "open-menu": state.mobileMenu = true; render(); break;
        case "close-menu": state.mobileMenu = false; render(); break;
        case "switch-auth": state.registerMode = !state.registerMode; renderAuth(); break;
        case "swap": [state.from, state.to] = [state.to, state.from]; render(); break;
        case "purchase": await purchaseTicket(); break;
        case "support": notify("Hotline Metro: 1900 1009"); break;
    }
});

app.addEventListener("change", event => {
    if (event.target.id === "from-station") { state.from = event.target.value; render(); }
    if (event.target.id === "to-station") { state.to = event.target.value; render(); }
});

app.addEventListener("submit", async event => {
    if (event.target.id !== "auth-form") return;
    event.preventDefault();
    const form = new FormData(event.target);
    try {
        if (state.registerMode) {
            await api("/api/dang-ky", { method: "POST", headers: { "Content-Type": "application/x-www-form-urlencoded" }, body: formBody(Object.fromEntries(form)) });
            state.registerMode = false;
            renderAuth("Đăng ký thành công. Bạn có thể đăng nhập ngay.");
        } else {
            const session = await api("/api/dang-nhap", { method: "POST", headers: { "Content-Type": "application/x-www-form-urlencoded" }, body: formBody(Object.fromEntries(form)) });
            state.session = session;
            state.screen = session.role === "ADMIN" ? "admin" : "home";
            await loadData();
            render();
        }
    } catch (error) { renderAuth(error.message); }
});

async function purchaseTicket() {
    try {
        const path = state.ticketType === "VE_LUOT" ? "/api/mua-ve-luot" : "/api/mua-ve-thang";
        const values = state.ticketType === "VE_LUOT"
            ? { maGaDi: state.from, maGaDen: state.to }
            : { loaiVe: state.monthlyType };
        const result = await api(path, { method: "POST", headers: { "Content-Type": "application/x-www-form-urlencoded" }, body: formBody(values) });
        notify(`${result.message}: ${result.ticket.id}`);
        await loadData();
        state.screen = "history";
        render();
    } catch (error) { notify(error.message, true); }
}

async function updateAccount(username, active) {
    try {
        const result = await api("/api/admin/trang-thai", { method: "POST", headers: { "Content-Type": "application/x-www-form-urlencoded" }, body: formBody({ tenDangNhap: username, active }) });
        notify(result.message);
        await loadData();
        render();
    } catch (error) { notify(error.message, true); }
}

async function logout() {
    try { await api("/api/dang-xuat", { method: "POST" }); } catch {}
    state.session = null;
    state.registerMode = false;
    renderAuth();
}

function notify(message, error = false) {
    toast.textContent = message;
    toast.style.background = error ? "#8e2430" : "#113460";
    toast.classList.add("show");
    clearTimeout(notify.timer);
    notify.timer = setTimeout(() => toast.classList.remove("show"), 2800);
}

function findStation(id) { return state.stations.find(station => station.id === id); }
function displayTicketType(type) { return type === "VE_LUOT" ? "Vé lượt" : "Vé tháng"; }
function displayRole(role) { return role === "ADMIN" ? "Quản trị" : "Hành khách"; }
function initialsOf(name) { return String(name || "HK").trim().split(/\s+/).slice(-2).map(word => word[0]).join("").toUpperCase(); }
function formatDate(value) { try { return new Intl.DateTimeFormat("vi-VN", { dateStyle: "short", timeStyle: "short" }).format(new Date(value)); } catch { return value; } }

boot();