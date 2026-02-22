import { store } from './store.js';

export const API_BASE = '';

async function request(path, options = {}) {
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
  if (store.token) headers.Authorization = `Bearer ${store.token}`;
  if (options.body instanceof FormData) delete headers['Content-Type'];
  const res = await fetch(`${API_BASE}${path}`, { ...options, headers });
  return res;
}

export async function apiJson(path, options = {}) {
  const res = await request(path, options);
  const data = res.headers.get('content-type')?.includes('application/json') ? await res.json() : null;
  if (!res.ok) throw new Error(data?.message || `HTTP ${res.status}`);
  return data;
}

export async function uploadPhotos(files = []) {
  if (!files.length) return [];
  const form = new FormData();
  files.forEach((f) => form.append('files', f));
  const data = await apiJson('/api/ad-photos/upload', { method: 'POST', body: form });
  return Array.isArray(data.photoUrls) ? data.photoUrls : [];
}

export { request };
