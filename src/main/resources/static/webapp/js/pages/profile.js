import { apiJson, request } from '../api.js';
import { adCard } from '../components/adCard.js';
import { toggleFavorite } from '../store.js';

export async function renderProfilePage() {
  let mode = 'ACTIVE';
  const list = document.getElementById('profileList');
  const a = document.getElementById('tabActive');
  const r = document.getElementById('tabArchive');

  const load = async () => {
    const ads = await apiJson(`/api/ads/my?status=${mode}`);
    list.innerHTML = ads.map((ad) => adCard(ad, 'profile')).join('');
  };
  a.onclick = () => { mode = 'ACTIVE'; a.classList.add('active'); r.classList.remove('active'); load(); };
  r.onclick = () => { mode = 'ARCHIVED'; r.classList.add('active'); a.classList.remove('active'); load(); };
  list.onclick = async (e) => {
    const action = e.target.dataset.action;
    const id = e.target.dataset.id;
    if (action === 'favorite') { toggleFavorite(id); return load(); }
    if (action === 'archive') { await request(`/api/ads/${id}/archive`, { method: 'PUT' }); return load(); }
    if (action === 'restore') { await request(`/api/ads/${id}/restore`, { method: 'PUT' }); return load(); }
    const card = e.target.closest('[data-open-id]');
    if (card && !action) location.hash = `#/ad/${card.dataset.openId}`;
  };
  await load();
}
