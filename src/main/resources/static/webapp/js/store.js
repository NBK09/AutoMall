const TOKEN_KEY = 'token';
const FAV_KEY = 'favorites';
const SETTINGS_KEY = 'webappSettings';

export const store = {
  token: localStorage.getItem(TOKEN_KEY) || '',
  favorites: new Set(JSON.parse(localStorage.getItem(FAV_KEY) || '[]')),
  settings: JSON.parse(localStorage.getItem(SETTINGS_KEY) || '{"sendMessages":true,"showPhone":false,"phone":"","country":"Россия"}')
};

export function setToken(token) {
  store.token = token || '';
  if (token) localStorage.setItem(TOKEN_KEY, token); else localStorage.removeItem(TOKEN_KEY);
}

export function toggleFavorite(adId) {
  const id = Number(adId);
  if (store.favorites.has(id)) store.favorites.delete(id); else store.favorites.add(id);
  localStorage.setItem(FAV_KEY, JSON.stringify([...store.favorites]));
  return store.favorites.has(id);
}

export function setFavorites(ids = []) {
  store.favorites = new Set(ids.map(Number));
  localStorage.setItem(FAV_KEY, JSON.stringify([...store.favorites]));
}

export function saveSettings(next) {
  store.settings = { ...store.settings, ...next };
  localStorage.setItem(SETTINGS_KEY, JSON.stringify(store.settings));
}
