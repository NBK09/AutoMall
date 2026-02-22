import { store } from '../store.js';
import { escapeHtml } from '../ui.js';

export function adCard(ad, mode = 'main') {
  const favorite = store.favorites.has(ad.id) || ad.isFavorite;
  const ownerActions = mode === 'profile' ? `
    <div class="row-btns">
      ${ad.status === 'ACTIVE' ? `<button class="btn ghost" data-action="archive" data-id="${ad.id}">Архивировать</button>` : `<button class="btn ok" data-action="restore" data-id="${ad.id}">Восстановить</button>`}
      <a class="btn ghost" href="#/edit/${ad.id}">Редактировать</a>
    </div>` : '';
  const favAction = `<button class="btn ghost" data-action="favorite" data-id="${ad.id}">${favorite ? '💔' : '❤️'}</button>`;

  return `<article class="card ad-card" data-open-id="${ad.id}">
    <div class="ad-thumb">${ad.photoUrls?.[0] ? `<img src="${escapeHtml(ad.photoUrls[0])}" alt="thumb"/>` : ''}</div>
    <div>
      <div class="ad-head"><h3 class="ad-title">${escapeHtml(ad.brand || '')} ${escapeHtml(ad.model || '')}</h3><span class="price">${escapeHtml(ad.price || '')}</span></div>
      <div class="ad-meta">${escapeHtml(ad.city || '')} · ${escapeHtml(ad.year || '')} · ${escapeHtml(ad.mileage || '')} км</div>
      <div class="ad-meta">${escapeHtml(ad.generation || '')} · ${escapeHtml(ad.engine || '')}</div>
      <div class="row-btns" style="margin-top:8px">${favAction}</div>
      ${ownerActions}
    </div>
  </article>`;
}
