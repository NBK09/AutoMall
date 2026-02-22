import { apiJson, uploadPhotos } from '../api.js';
import { toast } from '../ui.js';

export async function renderEditPage({ id }) {
  const ad = await apiJson(`/api/ads/${id}/edit`);
  document.getElementById('eBrand').value = String(ad.brandId ?? '');
  document.getElementById('eModel').value = String(ad.modelId ?? '');
  document.getElementById('eYear').value = ad.year ?? '';
  document.getElementById('eMileage').value = ad.mileage ?? '';
  document.getElementById('eColor').value = ad.color ?? '';
  document.getElementById('eVin').value = ad.vin ?? '';
  document.getElementById('ePrice').value = ad.price ?? '';
  document.getElementById('eDescription').value = ad.description ?? '';

  document.getElementById('editSubmit').onclick = async () => {
    const payload = {
      engineId: ad.engineId,
      transmissionId: ad.transmissionId,
      driveTypeId: ad.driveTypeId,
      cityId: ad.cityId,
      year: Number(document.getElementById('eYear').value),
      mileage: Number(document.getElementById('eMileage').value),
      color: document.getElementById('eColor').value.trim(),
      vin: document.getElementById('eVin').value.trim(),
      price: Number(document.getElementById('ePrice').value),
      description: document.getElementById('eDescription').value.trim(),
      photoUrls: [...(ad.photoUrls || [])]
    };
    if (payload.description.length < 5) return toast('Описание минимум 5 символов', false);
    const files = [...document.getElementById('ePhotos').files];
    if (files.length) payload.photoUrls = [...payload.photoUrls, ...(await uploadPhotos(files))];
    await apiJson(`/api/ads/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
    toast('Изменения сохранены');
    location.hash = `#/ad/${id}`;
  };
}
