import { escapeHtml } from '../ui.js';

export function renderPhotoCarousel(urls = [], adId) {
  const safe = urls.length ? urls : [''];
  return `<div class="carousel" data-ad="${adId}"><div class="carousel-track">${safe.map((u, i) => `<img src="${escapeHtml(u)}" alt="photo-${i}" data-index="${i}"/>`).join('')}</div><div class="dots">${safe.map((_, i) => `<span class="dot ${i===0?'active':''}" data-dot="${i}"></span>`).join('')}</div></div>`;
}

export function bindCarousel(root) {
  root.querySelectorAll('.carousel').forEach((c) => {
    const track = c.querySelector('.carousel-track');
    const dots = [...c.querySelectorAll('.dot')];
    track.addEventListener('scroll', () => {
      const idx = Math.round(track.scrollLeft / Math.max(track.clientWidth, 1));
      dots.forEach((d, i) => d.classList.toggle('active', i === idx));
    });
  });
}
