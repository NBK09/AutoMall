import { renderBottomNav } from './components/bottomNav.js';
import { loadTemplate } from './ui.js';
import { renderMainPage } from './pages/main.js';
import { renderFavoritesPage } from './pages/favorites.js';
import { renderCreatePage } from './pages/create.js';
import { renderProfilePage } from './pages/profile.js';
import { renderSettingsPage } from './pages/settings.js';
import { renderAdDetailsPage } from './pages/adDetails.js';
import { renderEditPage } from './pages/edit.js';

const routes = {
  main: renderMainPage,
  favorites: renderFavoritesPage,
  create: renderCreatePage,
  profile: renderProfilePage,
  settings: renderSettingsPage,
  ad: renderAdDetailsPage,
  edit: renderEditPage
};

export async function navigate() {
  const hash = location.hash || '#/main';
  const parts = hash.replace('#/', '').split('/');
  const page = parts[0];
  const id = parts[1];
  renderBottomNav(['main','favorites','create','profile'].includes(page) ? `#/${page}` : '');
  const root = document.getElementById('appRoot');
  if (!routes[page]) { location.hash = '#/main'; return; }
  root.innerHTML = await loadTemplate(page === 'ad' ? 'adDetails' : page);
  await routes[page]({ id });
}

export function initRouter() {
  window.addEventListener('hashchange', navigate);
  navigate();
}
