import { renderBottomNav } from './components/bottomNav.js';
import { renderMainPage } from './pages/main.js';
import { renderFavoritesPage } from './pages/favorites.js';
import { renderCreatePage } from './pages/create.js';
import { renderProfilePage } from './pages/profile.js';
import { renderSettingsPage } from './pages/settings.js';
import { renderAdDetailsPage } from './pages/adDetails.js';
import { renderEditPage } from './pages/edit.js';

function parseHash() {
  const hash = location.hash || '#/main';
  const [, route, id] = hash.match(/^#\/(\w+)(?:\/(\d+))?/) || [];
  return { route: route || 'main', id };
}

export async function renderRoute() {
  const root = document.getElementById('appRoot');
  const { route, id } = parseHash();
  renderBottomNav(['main','favorites','create','profile'].includes(route) ? route : '');
  if (route === 'main') return renderMainPage(root);
  if (route === 'favorites') return renderFavoritesPage(root);
  if (route === 'create') return renderCreatePage(root);
  if (route === 'profile') return renderProfilePage(root);
  if (route === 'settings') return renderSettingsPage(root);
  if (route === 'ad' && id) return renderAdDetailsPage(root, Number(id));
  if (route === 'edit' && id) return renderEditPage(root, Number(id));
  location.hash = '#/main';
}

export function initRouter() {
  window.addEventListener('hashchange', renderRoute);
  renderRoute();
}
