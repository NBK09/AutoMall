import { api } from '../api.js';
import { loadTemplate } from '../ui.js';
import { renderAdCard } from '../components/adCard.js';
import { store, toggleFavorite } from '../store.js';

export async function renderFavoritesPage(root) {
  root.innerHTML = await loadTemplate('/pages/favorites.html');
  const list = root.querySelector('#favoritesList');
  let ads = [];
  const ids = [...store.favorites];
  if (!ids.length) { list.innerHTML = '<div class="card">Пока пусто</div>'; return; }
  await Promise.all(ids.map(async (id) => {
    const res = await api(`/api/ads/${id}`);
    if (res.ok) ads.push(await res.json());
  }));
  list.innerHTML = ads.map((a) => renderAdCard({ ...a, favorite: true }, 'favorites')).join('');
  list.addEventListener('click', async (e) => {
    const open = e.target.closest('[data-open]');
    if (open) location.hash = `#/ad/${open.dataset.open}`;
    const fav = e.target.closest('[data-favorite]');
    if (fav) {
      const id = Number(fav.dataset.favorite);
      toggleFavorite(id);
      await api(`/api/favorites/${id}`, { method: 'DELETE' });
      fav.closest('.ad').remove();
    }
  });
}
