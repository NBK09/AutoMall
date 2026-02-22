import { apiJson, uploadPhotos } from '../api.js';
import { toast } from '../ui.js';

const fills = async () => {
  const [brands, transmissions, drives, cities] = await Promise.all([
    apiJson('/api/reference/brands'), apiJson('/api/reference/transmissions'), apiJson('/api/reference/drive-types'), apiJson('/api/reference/cities')
  ]);
  setOptions('brandId', brands);
  setOptions('transmissionId', transmissions);
  setOptions('driveTypeId', drives);
  setOptions('cityId', cities);
};

const setOptions = (id, arr = []) => {
  document.getElementById(id).innerHTML = arr.map((x) => `<option value="${x.id}">${x.name}</option>`).join('');
};

export async function renderCreatePage() {
  await fills();
  document.getElementById('brandId').onchange = async (e) => {
    const data = await apiJson(`/api/reference/brands/${e.target.value}/models`);
    setOptions('modelId', data); document.getElementById('modelId').disabled = false;
  };
  document.getElementById('modelId').onchange = async (e) => {
    const data = await apiJson(`/api/reference/models/${e.target.value}/generations`);
    setOptions('generationId', data); document.getElementById('generationId').disabled = false;
  };
  document.getElementById('generationId').onchange = async (e) => {
    const data = await apiJson(`/api/reference/generations/${e.target.value}/engines`);
    setOptions('engineId', data); document.getElementById('engineId').disabled = false;
  };

  document.getElementById('createSubmit').onclick = async () => {
    const payload = {
      brandId: Number(document.getElementById('brandId').value),
      modelId: Number(document.getElementById('modelId').value),
      generationId: Number(document.getElementById('generationId').value),
      engineId: Number(document.getElementById('engineId').value),
      transmissionId: Number(document.getElementById('transmissionId').value),
      driveTypeId: Number(document.getElementById('driveTypeId').value),
      cityId: Number(document.getElementById('cityId').value),
      year: Number(document.getElementById('year').value),
      mileage: Number(document.getElementById('mileage').value),
      color: document.getElementById('color').value.trim(),
      vin: document.getElementById('vin').value.trim(),
      price: Number(document.getElementById('price').value),
      description: document.getElementById('description').value.trim()
    };
    if (!payload.description || payload.description.length < 5) return toast('Описание минимум 5 символов', false);
    const files = [...document.getElementById('photos').files];
    payload.photoUrls = await uploadPhotos(files);
    await apiJson('/api/ads', { method: 'POST', body: JSON.stringify(payload) });
    toast('Объявление создано');
    location.hash = '#/profile';
  };
}
