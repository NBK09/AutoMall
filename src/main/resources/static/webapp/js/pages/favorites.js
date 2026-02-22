import { apiJson } from '../api.js';
import { adCard } from '../components/adCard.js';
import { store, toggleFavorite } from '../store.js';

export async function renderFavoritesPage() {
  const list = document.getElementById('favoritesList');
  let ads = [];
  try {
    ads = await apiJson('/api/favorites');
  } catch {
    const ids = [...store.favorites];
    ads = (await Promise.all(ids.map(async (id) => {
      try { return await apiJson(`/api/ads/${id}`); } catch { return null; }
    }))).filter(Boolean);
  }
  list.innerHTML = ads.filter((a) => store.favorites.has(a.id) || a.isFavorite).map((ad) => adCard(ad, 'favorites')).join('');
  list.onclick = (e) => {
    const action = e.target.dataset.action;
    const id = e.target.dataset.id;
    const card = e.target.closest('[data-open-id]');
    if (action === 'favorite') { toggleFavorite(id); location.hash = '#/favorites'; return; }
    if (card && !action) location.hash = `#/ad/${card.dataset.openId}`;
  };
}
