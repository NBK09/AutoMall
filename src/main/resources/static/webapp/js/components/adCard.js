import { escapeHtml } from '../ui.js';

export function renderAdCard(ad, mode = 'main') {
  const isFav = ad.isFavorite || ad.favorite;
  const ownerActions = mode === 'profile';
  const favoriteOnly = mode === 'favorites';
  return `<article class="card ad" data-id="${ad.id}">
    <div class="ad-card" data-open="${ad.id}">
      <div class="thumb">${ad.photoUrls?.[0] ? `<img src="${escapeHtml(ad.photoUrls[0])}" alt="thumb"/>` : ''}</div>
      <div>
        <div class="ad-top"><strong>${escapeHtml(`${ad.brand || ''} ${ad.model || ''}`.trim())}</strong><span class="price">${ad.price ?? ''}</span></div>
        <div class="meta">${escapeHtml(ad.city || '')} · ${ad.year || ''} · ${ad.mileage || ''} км</div>
      </div>
    </div>
    <div class="actions">
      <button class="icon-btn" data-favorite="${ad.id}">${isFav ? '💙' : '🤍'}</button>
      ${ownerActions ? `<button class="btn ghost" data-edit="${ad.id}">Редактировать</button><button class="btn ${ad.status === 'ARCHIVED' ? 'ok' : 'danger'}" data-archive="${ad.id}">${ad.status === 'ARCHIVED' ? 'Восстановить' : 'Архивировать'}</button>` : ''}
      ${favoriteOnly ? '' : ''}
    </div>
  </article>`;
}
