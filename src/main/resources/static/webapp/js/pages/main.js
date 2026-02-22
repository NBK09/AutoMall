import { apiJson } from '../api.js';
import { adCard } from '../components/adCard.js';
import { toggleFavorite } from '../store.js';
import { toast } from '../ui.js';

export async function renderMainPage() {
  const list = document.getElementById('mainList');
  const ads = await apiJson('/api/ads');
  list.innerHTML = ads.filter((a) => a.status === 'ACTIVE').map((ad) => adCard(ad, 'main')).join('');
  bindCardActions(list);
}

function bindCardActions(list) {
  list.onclick = async (e) => {
    const action = e.target.dataset.action;
    const id = e.target.dataset.id;
    const card = e.target.closest('[data-open-id]');
    if (action === 'favorite') {
      toggleFavorite(id);
      toast('Избранное обновлено');
      location.hash = '#/main';
      return;
    }
    if (card && !action) location.hash = `#/ad/${card.dataset.openId}`;
  };
}
