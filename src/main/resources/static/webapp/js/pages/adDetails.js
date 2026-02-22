import { api } from '../api.js';
import { loadTemplate } from '../ui.js';
import { renderPhotoCarousel, bindCarousel } from '../components/photoCarousel.js';
import { openPhotoViewer } from '../components/photoViewer.js';

export async function renderAdDetailsPage(root, id) {
  root.innerHTML = await loadTemplate('/webapp/js/pages/adDetails.html');
  const box = root.querySelector('#adDetails');
  const res = await api(`/api/ads/${id}`);
  if (!res.ok) { box.innerHTML = '<div class="card">Объявление не найдено</div>'; return; }
  const ad = await res.json();
  const photos = ad.photoUrls || [];
  const tgLink = ad.seller?.telegramUsername ? `https://t.me/${ad.seller.telegramUsername}` : null;
  box.innerHTML = `<article class="card">${renderPhotoCarousel(photos, id)}<h2>${ad.brand} ${ad.model}</h2><div class="meta">${ad.city} · ${ad.year}</div><p>${ad.description || ''}</p><div class="meta">Пробег: ${ad.mileage || '-'} · Цвет: ${ad.color || '-'} · VIN: ${ad.vin || '-'}</div><div class="meta">Двигатель: ${ad.engine || '-'} · Коробка: ${ad.transmission || '-'} · Привод: ${ad.driveType || '-'} · Поколение: ${ad.generation || '-'}</div>${tgLink ? `<a class="btn" href="${tgLink}" target="_blank">Написать в Telegram</a>` : '<div class="meta">Контакты скоро</div>'}</article>`;
  bindCarousel(box);
  box.querySelectorAll('.carousel-track img').forEach((img, i) => img.addEventListener('click', () => openPhotoViewer(photos, i)));
}
