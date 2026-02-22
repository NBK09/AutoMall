import { escapeHtml } from '../ui.js';

export function renderCarousel(photos = []) {
  const src = photos[0] || '';
  return `<div class="carousel" data-photos='${escapeHtml(JSON.stringify(photos))}'>
    ${src ? `<img src="${escapeHtml(src)}" alt="photo"/>` : '<div class="ad-thumb"></div>'}
    <div class="carousel-ind">1/${Math.max(photos.length, 1)}</div>
  </div>`;
}
