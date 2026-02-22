import { store } from './store.js';

export const API_BASE = '';

function headers(extra = {}) {
  const h = { ...extra };
  if (!('Content-Type' in h)) h['Content-Type'] = 'application/json';
  if (store.token) h.Authorization = `Bearer ${store.token}`;
  return h;
}

export async function api(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: headers(options.headers || {}),
  });
  return response;
}

export async function apiMultipart(path, formData, options = {}) {
  const h = {};
  if (store.token) h.Authorization = `Bearer ${store.token}`;
  return fetch(`${API_BASE}${path}`, { ...options, method: options.method || 'POST', headers: h, body: formData });
}
