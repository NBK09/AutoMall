import { api, apiMultipart } from '../api.js';
import { loadTemplate, toast } from '../ui.js';

const setOptions = (el, arr) => { el.innerHTML = '<option value="">Выбрать</option>' + arr.map((x) => `<option value="${x.id}">${x.name}</option>`).join(''); };

export async function renderCreatePage(root) {
  root.innerHTML = await loadTemplate('/pages/create.html');
  const $ = (id) => root.querySelector(`#${id}`);
  const refs = {
    brand: await (await api('/api/reference/brands')).json(),
    transmission: await (await api('/api/reference/transmissions')).json(),
    driveType: await (await api('/api/reference/drive-types')).json(),
    city: await (await api('/api/reference/cities')).json()
  };
  setOptions($('brand'), refs.brand); setOptions($('transmission'), refs.transmission); setOptions($('driveType'), refs.driveType); setOptions($('city'), refs.city);
  $('brand').addEventListener('change', async () => { const v = $('brand').value; const r = await api(`/api/reference/brands/${v}/models`); const d = r.ok ? await r.json() : []; $('model').disabled = false; setOptions($('model'), d); });
  $('model').addEventListener('change', async () => { const r = await api(`/api/reference/models/${$('model').value}/generations`); const d = r.ok ? await r.json() : []; $('generation').disabled = false; setOptions($('generation'), d); });
  $('generation').addEventListener('change', async () => { const r = await api(`/api/reference/generations/${$('generation').value}/engines`); const d = r.ok ? await r.json() : []; $('engine').disabled = false; setOptions($('engine'), d); });

  root.querySelector('#createForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const files = [...$('photos').files];
    let photoUrls = [];
    if (files.length) {
      const fd = new FormData();
      files.forEach((f) => fd.append('files', f));
      const up = await apiMultipart('/api/ad-photos/upload', fd);
      if (up.ok) photoUrls = (await up.json()).photoUrls || [];
    }
    const payload = {
      brandId: Number($('brand').value), modelId: Number($('model').value), generationId: Number($('generation').value), engineId: Number($('engine').value), transmissionId: Number($('transmission').value), driveTypeId: Number($('driveType').value), cityId: Number($('city').value), year: Number($('year').value), mileage: Number($('mileage').value), color: $('color').value.trim(), vin: $('vin').value.trim() || null, price: Number($('price').value), description: $('description').value.trim(), photoUrls
    };
    const res = await api('/api/ads', { method: 'POST', body: JSON.stringify(payload) });
    if (res.ok) { toast('Объявление создано'); location.hash = '#/profile'; } else toast('Ошибка создания');
  });
}
