import { apiJson } from '../api.js';
import { renderCarousel } from '../components/photoCarousel.js';
import { openPhotoViewer } from '../components/photoViewer.js';
import { escapeHtml } from '../ui.js';

export async function renderAdDetailsPage({ id }) {
  const ad = await apiJson(`/api/ads/${id}`);
  const root = document.getElementById('adDetails');
  root.innerHTML = `
    <div class="row-btns"><button class="btn ghost" id="backBtn">← Назад</button></div>
    ${renderCarousel(ad.photoUrls || [])}
    <h1>${escapeHtml(ad.brand)} ${escapeHtml(ad.model)}</h1>
    <div class="ad-meta">${escapeHtml(ad.generation || '')} · ${escapeHtml(ad.year || '')}</div>
    <p><b>Цена:</b> ${escapeHtml(ad.price)} ${escapeHtml(ad.currency || '')}</p>
    <p><b>Город:</b> ${escapeHtml(ad.city || '')}</p>
    <p><b>Пробег:</b> ${escapeHtml(ad.mileage || '')}</p>
    <p><b>Цвет:</b> ${escapeHtml(ad.color || '')}</p>
    <p><b>VIN:</b> ${escapeHtml(ad.vin || '—')}</p>
    <p><b>Двигатель:</b> ${escapeHtml(ad.engine || '')}</p>
    <p><b>Коробка:</b> ${escapeHtml(ad.transmission || '')}</p>
    <p><b>Привод:</b> ${escapeHtml(ad.driveType || '')}</p>
    <p><b>Описание:</b> ${escapeHtml(ad.description || '')}</p>
    ${ad.seller?.telegramUsername ? `<a class="btn" href="https://t.me/${escapeHtml(ad.seller.telegramUsername)}" target="_blank">Написать в Telegram</a>` : '<button class="btn ghost" disabled>Контакты скоро</button>'}
  `;
  document.getElementById('backBtn').onclick = () => history.back();
  const carousel = root.querySelector('.carousel img');
  if (!carousel) return;
  let idx = 0;
  let x0 = 0;
  carousel.addEventListener('click', () => openPhotoViewer(ad.photoUrls || [], idx));
  carousel.addEventListener('touchstart', (e) => (x0 = e.touches[0].clientX), { passive: true });
  carousel.addEventListener('touchend', (e) => {
    const dx = e.changedTouches[0].clientX - x0;
    if (Math.abs(dx) < 40 || !ad.photoUrls?.length) return;
    idx = dx < 0 ? (idx + 1) % ad.photoUrls.length : (idx - 1 + ad.photoUrls.length) % ad.photoUrls.length;
    carousel.src = ad.photoUrls[idx];
    root.querySelector('.carousel-ind').textContent = `${idx + 1}/${ad.photoUrls.length}`;
  });
}
