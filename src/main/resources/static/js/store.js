const FAVORITES_KEY = 'favorites';
const SETTINGS_KEY = 'settings';

export const store = {
  token: localStorage.getItem('token') || null,
  favorites: new Set(JSON.parse(localStorage.getItem(FAVORITES_KEY) || '[]')),
  settings: JSON.parse(localStorage.getItem(SETTINGS_KEY) || '{"allowMessages":true,"showPhone":false,"phone":"","country":"RU"}')
};

export function setToken(token) {
  store.token = token;
  if (token) localStorage.setItem('token', token);
}

export function toggleFavorite(adId) {
  const id = Number(adId);
  if (store.favorites.has(id)) store.favorites.delete(id); else store.favorites.add(id);
  localStorage.setItem(FAVORITES_KEY, JSON.stringify([...store.favorites]));
  return store.favorites.has(id);
}

export function setFavorites(ids) {
  store.favorites = new Set(ids.map(Number));
  localStorage.setItem(FAVORITES_KEY, JSON.stringify([...store.favorites]));
}

export function saveSettings(next) {
  store.settings = { ...store.settings, ...next };
  localStorage.setItem(SETTINGS_KEY, JSON.stringify(store.settings));
}
