import { api, apiMultipart } from '../api.js';
import { loadTemplate, toast } from '../ui.js';

export async function renderEditPage(root, id) {
  root.innerHTML = await loadTemplate('/webapp/js/pages/edit.html');
  const $ = (sel) => root.querySelector(sel);
  const res = await api(`/api/ads/${id}/edit`);
  if (!res.ok) { root.innerHTML = '<div class="card">Недоступно для редактирования</div>'; return; }
  const ad = await res.json();
  $('#eBrand').value = ad.brandId ?? '';
  $('#eModel').value = ad.modelId ?? '';
  $('#eGeneration').value = ad.generationId ?? '';
  $('#eEngine').value = ad.engineId ?? '';
  $('#eTransmission').value = ad.transmissionId ?? '';
  $('#eDriveType').value = ad.driveTypeId ?? '';
  $('#eCity').value = ad.cityId ?? '';
  $('#eYear').value = ad.year ?? '';
  $('#eMileage').value = ad.mileage ?? '';
  $('#eColor').value = ad.color ?? '';
  $('#eVin').value = ad.vin ?? '';
  $('#ePrice').value = ad.price ?? '';
  $('#eDescription').value = ad.description ?? '';

  $('#editForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    let photoUrls = ad.photoUrls || [];
    const files = [...$('#ePhotos').files];
    if (files.length) {
      const fd = new FormData(); files.forEach((f) => fd.append('files', f));
      const up = await apiMultipart('/api/ad-photos/upload', fd);
      if (up.ok) photoUrls = photoUrls.concat((await up.json()).photoUrls || []);
    }
    const payload = {
      generationId: Number($('#eGeneration').value),
      engineId: Number($('#eEngine').value), transmissionId: Number($('#eTransmission').value), driveTypeId: Number($('#eDriveType').value), cityId: Number($('#eCity').value), year: Number($('#eYear').value), mileage: Number($('#eMileage').value), color: $('#eColor').value.trim(), vin: $('#eVin').value.trim() || null, price: Number($('#ePrice').value), description: $('#eDescription').value.trim(), photoUrls, mainIndex: ad.mainIndex || 0
    };
    const upd = await api(`/api/ads/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
    if (upd.ok) { toast('Обновлено'); location.hash = `#/ad/${id}`; }
    else { toast('Не удалось обновить'); /* TODO: fallback endpoint if backend changes */ }
  });
}
