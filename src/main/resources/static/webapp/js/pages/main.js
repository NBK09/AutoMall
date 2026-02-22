import { api } from '../api.js';
import { loadTemplate, toast } from '../ui.js';
import { renderAdCard } from '../components/adCard.js';
import { toggleFavorite, store } from '../store.js';

export async function renderMainPage(root) {
  root.innerHTML = await loadTemplate('/webapp/js/pages/main.html');
  const list = root.querySelector('#mainList');
  const res = await api('/api/ads');
  const ads = res.ok ? await res.json() : [];
  list.innerHTML = ads.map((a) => renderAdCard({ ...a, favorite: store.favorites.has(a.id) || a.isFavorite }, 'main')).join('');
  list.addEventListener('click', async (e) => {
    const open = e.target.closest('[data-open]');
    if (open) location.hash = `#/ad/${open.dataset.open}`;
    const fav = e.target.closest('[data-favorite]');
    if (fav) {
      const id = Number(fav.dataset.favorite);
      const active = toggleFavorite(id);
      fav.textContent = active ? '💙' : '🤍';
      try { await api(`/api/favorites/${id}`, { method: active ? 'POST' : 'DELETE' }); } catch {}
      toast(active ? 'Добавлено в избранное' : 'Убрано из избранного');
    }
  });
}
