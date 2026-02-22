import { api } from '../api.js';
import { loadTemplate } from '../ui.js';
import { renderAdCard } from '../components/adCard.js';

export async function renderProfilePage(root) {
  root.innerHTML = await loadTemplate('/pages/profile.html');
  const list = root.querySelector('#profileList');
  const tabs = [...root.querySelectorAll('[data-my-tab]')];

  async function load(status = 'ACTIVE') {
    const res = await api(`/api/ads/my?status=${status}`);
    const ads = res.ok ? await res.json() : [];
    list.innerHTML = ads.map((a) => renderAdCard(a, 'profile')).join('');
  }

  tabs.forEach((t) => t.addEventListener('click', () => {
    tabs.forEach((x) => x.classList.remove('active'));
    t.classList.add('active');
    load(t.dataset.myTab);
  }));

  list.addEventListener('click', async (e) => {
    const open = e.target.closest('[data-open]');
    if (open) location.hash = `#/ad/${open.dataset.open}`;
    const edit = e.target.closest('[data-edit]');
    if (edit) location.hash = `#/edit/${edit.dataset.edit}`;
    const arch = e.target.closest('[data-archive]');
    if (arch) {
      const id = arch.dataset.archive;
      const endpoint = arch.textContent.includes('Восстановить') ? 'restore' : 'archive';
      await api(`/api/ads/${id}/${endpoint}`, { method: 'PUT' });
      load(root.querySelector('[data-my-tab].active').dataset.myTab);
    }
  });

  load();
}
