(() => {
  // защита от двойного подключения app.js
  if (window.__AUTO_MALL_APP_INITED__) return;
  window.__AUTO_MALL_APP_INITED__ = true;

  const $ = (id) => document.getElementById(id);

  const LS = {
    apiBase: "am_api_base",
    jwt: "am_jwt",
  };

  const state = {
    activeTab: "feed",
    mySeg: "active",
    refs: null,
    selectedFiles: [],
    uploadedPhotos: [], // сюда кладём { publicUrl, objectKey }
    editingAdId: null,
  };

  const cfg = {
    apiBase: localStorage.getItem(LS.apiBase) || "",
    jwt: localStorage.getItem(LS.jwt) || "",
  };

  const isTelegram = () => !!(window.Telegram && Telegram.WebApp);

  const API = {
    base() {
      // если пусто — используем same-origin
      return (cfg.apiBase || "").replace(/\/$/, "");
    },
    headers(json = true) {
      const h = {};
      if (json) h["Content-Type"] = "application/json";
      if (cfg.jwt) h["Authorization"] = `Bearer ${cfg.jwt}`;
      return h;
    },
    async ping() {
      return fetch(`${API.base()}/api/ping`, { headers: API.headers(false) });
    },

    // auth
    async telegramAuth(initData) {
      // ожидаем /api/auth/telegram
      const res = await fetch(`${API.base()}/api/auth/telegram`, {
        method: "POST",
        headers: API.headers(true),
        body: JSON.stringify({ initData }),
      });
      if (!res.ok) throw new Error(`auth failed: ${res.status}`);
      return res.json(); // ожидаем { token: "..." } или { jwt: "..." }
    },

    // reference
    async referenceFull() {
      const res = await fetch(`${API.base()}/api/reference/full`, { headers: API.headers(false) });
      if (!res.ok) throw new Error(`reference full: ${res.status}`);
      return res.json();
    },
    async ref(path) {
      const res = await fetch(`${API.base()}/api/reference/${path}`, { headers: API.headers(false) });
      if (!res.ok) throw new Error(`reference ${path}: ${res.status}`);
      return res.json();
    },

    // ads
    async feedAds() {
      const res = await fetch(`${API.base()}/api/ads`, { headers: API.headers(false) });
      if (!res.ok) throw new Error(`ads: ${res.status}`);
      return res.json();
    },
    async myAds() {
      const res = await fetch(`${API.base()}/api/ads/my`, { headers: API.headers(false) });
      if (!res.ok) throw new Error(`ads/my: ${res.status}`);
      return res.json();
    },
    async createAd(payload) {
      const res = await fetch(`${API.base()}/api/ads`, {
        method: "POST",
        headers: API.headers(true),
        body: JSON.stringify(payload),
      });

      const text = await res.text();

      if (!res.ok) {
        console.error("CREATE AD ERROR:", text);
        throw new Error(`create ad: ${res.status} ${text}`);
      }

      return JSON.parse(text);
    },
    async archiveAd(id) {
      const res = await fetch(`${API.base()}/api/ads/${id}/archive`, {
        method: "PUT",
        headers: API.headers(false),
      });
      if (!res.ok) throw new Error(`archive: ${res.status}`);
      return res.text().catch(() => "");
    },
    async restoreAd(id) {
      const res = await fetch(`${API.base()}/api/ads/${id}/restore`, {
        method: "PUT",
        headers: API.headers(false),
      });
      if (!res.ok) throw new Error(`restore: ${res.status}`);
      return res.text().catch(() => "");
    },

    // S3 multipart via backend
    async mpInit({ adId, fileName, contentType }) {
      if (!adId) throw new Error("mpInit: adId is required");

      const res = await fetch(`${API.base()}/api/ads/${adId}/photos/multipart/init`, {
        method: "POST",
        headers: API.headers(true),
        body: JSON.stringify({ fileName, contentType }),
      });

      if (!res.ok) throw new Error(`multipart/init: ${res.status}`);
      return res.json();
    },

    async mpPresign({ adId, uploadId, objectKey, partNumbers }) {
      if (!adId) throw new Error("mpPresign: adId is required");

      const res = await fetch(`${API.base()}/api/ads/${adId}/photos/multipart/presign`, {
        method: "POST",
        headers: API.headers(true),
        body: JSON.stringify({ uploadId, objectKey, partNumbers }),
      });

      if (!res.ok) throw new Error(`multipart/presign: ${res.status}`);
      return res.json();
    },

    async mpComplete({ adId, uploadId, objectKey, parts }) {
      if (!adId) throw new Error("mpComplete: adId is required");

      const res = await fetch(`${API.base()}/api/ads/${adId}/photos/multipart/complete`, {
        method: "POST",
        headers: API.headers(true),
        body: JSON.stringify({ uploadId, objectKey, parts }),
      });

      if (!res.ok) throw new Error(`multipart/complete: ${res.status}`);
      return res.json();
    },
  };

  function setSubtitle(text) {
    const el = $("subtitle");
    if (el) el.textContent = text;
  }

  function toast(msg) {
    // мини-тост через alert пока
    // можно заменить на красивый later
    alert(msg);
  }

  function show(el, on) {
    if (!el) return;
    el.classList.toggle("hidden", !on);
  }

  function setActiveTab(tab) {
    state.activeTab = tab;

    document.querySelectorAll(".tab").forEach(b => {
      b.classList.toggle("active", b.dataset.tab === tab);
    });

    ["feed", "my", "create"].forEach(t => {
      const pane = $(`tab-${t}`);
      if (pane) pane.classList.toggle("active", t === tab);
    });

    if (tab === "feed") loadFeed();
    if (tab === "my") loadMy();
    if (tab === "create") { /* nothing */ }
  }

  function applyFeedFilters(items) {
    const q = ($("q")?.value || "").trim().toLowerCase();
    const sort = $("sort")?.value || "new";

    let out = items;

    if (q) {
      out = out.filter(a => {
        const s = `${a.title || ""} ${a.brand || ""} ${a.model || ""} ${a.city || ""} ${a.description || ""}`.toLowerCase();
        return s.includes(q);
      });
    }

    if (sort === "price_asc") out = [...out].sort((a,b) => (a.price||0) - (b.price||0));
    if (sort === "price_desc") out = [...out].sort((a,b) => (b.price||0) - (a.price||0));

    return out;
  }

  function firstPhotoUrl(ad) {
    // подстрахуемся под разные форматы:
    // photos: [url] или photoUrls: [url] или photos: [{url:...}]
    const p = ad.photos || ad.photoUrls || [];
    if (!p.length) return null;
    if (typeof p[0] === "string") return p[0];
    if (p[0]?.url) return p[0].url;
    if (p[0]?.publicUrl) return p[0].publicUrl;
    return null;
  }

  function renderCards(listEl, items, mode) {
    listEl.innerHTML = "";

    items.forEach(ad => {
      const img = firstPhotoUrl(ad);

      const card = document.createElement("div");
      card.className = "card";

      const media = document.createElement("div");
      media.className = "card-media";
      if (img) {
        const im = document.createElement("img");
        im.src = img;
        im.alt = "photo";
        media.appendChild(im);
      } else {
        media.textContent = "📷 нет фото";
      }

      const badge = document.createElement("div");
      badge.className = "badge";
      badge.textContent = (ad.status || "").toUpperCase() || "AD";
      media.appendChild(badge);

      const body = document.createElement("div");
      body.className = "card-body";

      const title = document.createElement("div");
      title.className = "card-title";
      title.innerHTML = `
        <div>${(ad.title || `${ad.brand || ""} ${ad.model || ""}`).trim() || "Объявление"}</div>
        <div class="price">${ad.price ? Intl.NumberFormat("ru-RU").format(ad.price) + " ₸" : ""}</div>
      `;

      const meta = document.createElement("div");
      meta.className = "meta";
      meta.innerHTML = `
        ${ad.city ? `📍 ${ad.city}` : ""}
        ${ad.year ? ` • ${ad.year} г.` : ""}
        ${ad.mileage ? ` • ${Intl.NumberFormat("ru-RU").format(ad.mileage)} км` : ""}
      `;

      const actions = document.createElement("div");
      actions.className = "card-actions";

      if (mode === "my") {
        const btn = document.createElement("button");
        btn.className = "btn";
        btn.textContent = (state.mySeg === "archived") ? "Восстановить" : "В архив";
        btn.onclick = async () => {
          try {
            if (state.mySeg === "archived") await API.restoreAd(ad.id);
            else await API.archiveAd(ad.id);
            await loadMy();
          } catch (e) {
            toast(String(e.message || e));
          }
        };
        actions.appendChild(btn);
      }

      body.appendChild(title);
      body.appendChild(meta);
      body.appendChild(actions);

      card.appendChild(media);
      card.appendChild(body);

      listEl.appendChild(card);
    });
  }

  async function loadFeed() {
    const list = $("feedList");
    const empty = $("feedEmpty");
    if (!list || !empty) return;

    try {
      const data = await API.feedAds();
      const items = Array.isArray(data) ? data : (data.items || []);
      const filtered = applyFeedFilters(items);

      show(empty, filtered.length === 0);
      show(list, filtered.length > 0);

      renderCards(list, filtered, "feed");
    } catch (e) {
      show(empty, true);
      empty.textContent = `Ошибка загрузки ленты: ${e.message || e}`;
      list.innerHTML = "";
    }
  }

  async function loadMy() {
    const list = $("myList");
    const empty = $("myEmpty");
    if (!list || !empty) return;

    try {
      const data = await API.myAds();
      const items = Array.isArray(data) ? data : (data.items || []);
      const filtered = items.filter(a => {
        const st = (a.status || "").toLowerCase();
        return state.mySeg === "archived"
          ? (st.includes("arch") || st === "archived")
          : !(st.includes("arch") || st === "archived");
      });

      show(empty, filtered.length === 0);
      show(list, filtered.length > 0);

      renderCards(list, filtered, "my");
    } catch (e) {
      show(empty, true);
      empty.textContent = `Ошибка загрузки "Мои": ${e.message || e}`;
      list.innerHTML = "";
    }
  }

  function fillSelect(selectEl, items, { valueKey="id", labelKey="name", placeholder="—" } = {}) {
    selectEl.innerHTML = "";
    const opt0 = document.createElement("option");
    opt0.value = "";
    opt0.textContent = placeholder;
    selectEl.appendChild(opt0);

    items.forEach(it => {
      const opt = document.createElement("option");
      opt.value = it[valueKey] ?? it.code ?? it.id ?? it.name;
      opt.textContent = it[labelKey] ?? it.title ?? it.name ?? String(opt.value);
      selectEl.appendChild(opt);
    });
  }

  async function loadRefs() {
    try {
      // пробуем full
      const full = await API.referenceFull();
      state.refs = full;
      setSubtitle(isTelegram() ? "Telegram WebApp" : "браузер");
    } catch (e) {
      // fallback на отдельные
      const [brands, transmissions, driveTypes, cities] = await Promise.all([
        API.ref("brands"),
        API.ref("transmissions"),
        API.ref("drive-types"),
        API.ref("cities"),
      ]);
      state.refs = { brands, transmissions, driveTypes, cities };
    }

    const brand = $("brand");
    const model = $("model");
    const city = $("city");
    const transmission = $("transmission");
    const driveType = $("driveType");

    if (!brand || !model || !city || !transmission || !driveType) return;

    const brands = state.refs.brands || [];
    const cities = state.refs.cities || [];
    const transmissions = state.refs.transmissions || [];
    const driveTypes = state.refs.driveTypes || [];

    fillSelect(brand, brands, { placeholder: "Выбери марку" });
    fillSelect(city, cities, { placeholder: "Выбери город" });
    fillSelect(transmission, transmissions, { placeholder: "Выбери КПП" });
    fillSelect(driveType, driveTypes, { placeholder: "Выбери привод" });

    // модели зависят от марки
    brand.addEventListener("change", async () => {
      const brandId = brand.value;
      if (!brandId) {
        fillSelect(model, [], { placeholder: "Сначала выбери марку" });
        return;
      }
      try {
        const models = await API.ref(`brands/${encodeURIComponent(brandId)}/models`);
        fillSelect(model, models, { placeholder: "Выбери модель" });
      } catch {
        fillSelect(model, [], { placeholder: "Нет моделей (ошибка)" });
      }
    });

    // initial
    fillSelect(model, [], { placeholder: "Сначала выбери марку" });
  }

  function renderPhotoGrid() {
    const grid = $("photoGrid");
    const count = $("photoCount");
    if (!grid || !count) return;

    count.textContent = String(state.selectedFiles.length);
    grid.innerHTML = "";

    state.selectedFiles.forEach((file, idx) => {
      const item = document.createElement("div");
      item.className = "photo-item";

      const img = document.createElement("img");
      img.src = URL.createObjectURL(file);
      img.alt = "preview";

      const x = document.createElement("button");
      x.className = "x";
      x.textContent = "✕";
      x.onclick = () => {
        state.selectedFiles.splice(idx, 1);
        renderPhotoGrid();
      };

      item.appendChild(img);
      item.appendChild(x);
      grid.appendChild(item);
    });
  }

  function resetForm() {
    state.selectedFiles = [];
    state.uploadedPhotos = [];
    state.editingAdId = null;

    ["brand","model","city","year","price","mileage","description","transmission","driveType"]
      .forEach(id => {
        const el = $(id);
        if (!el) return;
        if (el.tagName === "SELECT") el.value = "";
        else el.value = "";
      });

    renderPhotoGrid();
    $("formTitle").textContent = "Подача объявления";
  }

  function setProgress(pct) {
    const wrap = $("uploadProgress");
    const bar = $("uploadBar");
    if (!wrap || !bar) return;
    show(wrap, true);
    bar.style.width = `${Math.max(0, Math.min(100, pct))}%`;
  }

  function hideProgress() {
    const wrap = $("uploadProgress");
    const bar = $("uploadBar");
    if (!wrap || !bar) return;
    show(wrap, false);
    bar.style.width = "0%";
  }

  // режем файл на части (по 5MB)
  function splitToParts(file, partSize = 5 * 1024 * 1024) {
    const parts = [];
    let offset = 0;
    let partNumber = 1;
    while (offset < file.size) {
      const end = Math.min(offset + partSize, file.size);
      parts.push({ partNumber, blob: file.slice(offset, end) });
      offset = end;
      partNumber++;
    }
    return parts;
  }

  // главная загрузка 1 файла через multipart
async function uploadOneFileMultipart(adId, file, progressBase, progressSpan) {
  if (!adId) throw new Error("uploadOneFileMultipart: adId is required");

  // 1) init
  const init = await API.mpInit({
    adId,
    fileName: file.name,
    contentType: file.type || "application/octet-stream"
  });

  const uploadId = init.uploadId;
  const objectKey = init.objectKey;

  if (!uploadId || !objectKey) {
    throw new Error("multipart/init вернул пустой uploadId/objectKey");
  }

  const parts = splitToParts(file);
  const partNumbers = parts.map(p => p.partNumber);

  // 2) presign
  const pres = await API.mpPresign({ adId, uploadId, objectKey, partNumbers });

  const urlByPart = new Map((pres.parts || []).map(p => [p.partNumber, p.url]));

  const completed = [];
  for (let i = 0; i < parts.length; i++) {
    const p = parts[i];
    const url = urlByPart.get(p.partNumber);
    if (!url) throw new Error(`нет presigned url для part ${p.partNumber}`);

    const put = await fetch(url, {
      method: "PUT",
      body: p.blob,
      headers: { "Content-Type": file.type || "application/octet-stream" },
    });

    if (!put.ok) throw new Error(`PUT part ${p.partNumber} failed: ${put.status}`);

    const eTag = (put.headers.get("ETag") || put.headers.get("etag") || "").replaceAll('"', "");
    completed.push({ partNumber: p.partNumber, eTag });

    const pct = progressBase + ((i + 1) / parts.length) * progressSpan;
    setProgress(pct);
  }

  // 3) complete
  const done = await API.mpComplete({ adId, uploadId, objectKey, parts: completed });

  const publicUrl =
    done.publicUrl ||
    done.url ||
    done.publicURL || // на всякий
    "";

  return { publicUrl, objectKey };
}

    async function uploadSelectedPhotos(adId) {
      if (!adId) throw new Error("uploadSelectedPhotos: adId is required");

      if (state.selectedFiles.length === 0) return [];

      if (state.selectedFiles.length > 10) {
        throw new Error("Можно максимум 10 фото");
      }

      hideProgress();
      setProgress(1);

      const uploaded = [];
      for (let i = 0; i < state.selectedFiles.length; i++) {
        const file = state.selectedFiles[i];
        const base = (i / state.selectedFiles.length) * 100;
        const span = 100 / state.selectedFiles.length;

        const one = await uploadOneFileMultipart(adId, file, base, span);
        uploaded.push(one);
      }

      setProgress(100);
      setTimeout(hideProgress, 400);

      return uploaded;
    }

    async function publish() {
      try {
        // 1) create ad FIRST (без фоток)
    const payload = {
      brandId: Number($("brand")?.value),
      modelId: Number($("model")?.value),
      cityId: Number($("city")?.value),
      transmissionId: Number($("transmission")?.value),
      driveTypeId: Number($("driveType")?.value),

      year: Number($("year")?.value),
      mileage: Number($("mileage")?.value),
      price: Number($("price")?.value),

      description: $("description")?.value || ""
    };

        const created = await API.createAd(payload);
        const adId = created.id;          // <-- ВАЖНО
        if (!adId) throw new Error("createAd вернул пустой id");

        // 2) upload photos AFTER we have adId
        const uploaded = await uploadSelectedPhotos(adId); // <-- передать adId
        state.uploadedPhotos = uploaded;

        toast("✅ Объявление создано + фото загружены");
        resetForm();
        setActiveTab("my");
        await loadMy();
        return created;

      } catch (e) {
        hideProgress();
        toast(`Ошибка: ${e.message || e}`);
      }
    }

  function openSettings() {
    $("apiBase").value = cfg.apiBase || "";
    $("jwtToken").value = cfg.jwt || "";
    show($("settingsModal"), true);
  }
  function closeSettings() {
    show($("settingsModal"), false);
  }

  function saveSettings() {
    cfg.apiBase = ($("apiBase").value || "").trim().replace(/\/$/, "");
    cfg.jwt = ($("jwtToken").value || "").trim();

    localStorage.setItem(LS.apiBase, cfg.apiBase);
    localStorage.setItem(LS.jwt, cfg.jwt);

    closeSettings();
    toast("Сохранено");
  }

  function resetSettings() {
    cfg.apiBase = "";
    cfg.jwt = "";
    localStorage.removeItem(LS.apiBase);
    localStorage.removeItem(LS.jwt);
    $("apiBase").value = "";
    $("jwtToken").value = "";
    toast("Сброшено");
  }

  async function tryTelegramAutoAuth() {
    if (!isTelegram()) return;

    try {
      Telegram.WebApp.ready();
      Telegram.WebApp.expand();

      const initData = Telegram.WebApp.initData || "";
      if (!initData) return;

      const res = await API.telegramAuth(initData);

      // поддержка разных форматов ответа
      const token = res.token || res.jwt || res.accessToken || "";
      if (token) {
        cfg.jwt = token;
        localStorage.setItem(LS.jwt, cfg.jwt);
        setSubtitle("авто-авторизация: ok");
      } else {
        setSubtitle("авто-авторизация: нет токена");
      }
    } catch (e) {
      setSubtitle("авто-авторизация: ошибка");
      // не валим приложение
      console.warn(e);
    }
  }

  function bindUI() {
    // tabs
    document.querySelectorAll(".tab").forEach(b => {
      b.addEventListener("click", () => {
        const tab = b.dataset.tab;
        if (tab === "create") resetForm();
        setActiveTab(tab);
      });
    });

    // refresh / settings
    $("btnRefresh")?.addEventListener("click", async () => {
      if (state.activeTab === "feed") return loadFeed();
      if (state.activeTab === "my") return loadMy();
      return;
    });

    $("btnSettings")?.addEventListener("click", openSettings);
    $("btnCloseSettings")?.addEventListener("click", closeSettings);
    $("btnSaveSettings")?.addEventListener("click", saveSettings);
    $("btnResetSettings")?.addEventListener("click", resetSettings);

    // feed filters
    $("q")?.addEventListener("input", loadFeed);
    $("sort")?.addEventListener("change", loadFeed);

    // my segmented
    document.querySelectorAll(".seg").forEach(b => {
      b.addEventListener("click", () => {
        document.querySelectorAll(".seg").forEach(x => x.classList.remove("active"));
        b.classList.add("active");
        state.mySeg = b.dataset.seg;
        loadMy();
      });
    });

    // photos
    $("btnAddPhotos")?.addEventListener("click", () => {
      const picker = $("filePicker");
      if (!picker) return;
      const files = Array.from(picker.files || []);
      if (!files.length) return;

      const merged = [...state.selectedFiles, ...files].slice(0, 10);
      state.selectedFiles = merged;
      picker.value = ""; // сброс
      renderPhotoGrid();
    });

    $("btnClearPhotos")?.addEventListener("click", () => {
      state.selectedFiles = [];
      renderPhotoGrid();
    });

    $("btnResetForm")?.addEventListener("click", resetForm);
    $("btnPublish")?.addEventListener("click", publish);
  }

  async function init() {
    bindUI();
    await tryTelegramAutoAuth();
    await loadRefs();
    setActiveTab("feed");
  }
  console.log(window.Telegram?.WebApp?.initData);
  document.addEventListener("DOMContentLoaded", init);
})();