const tabs = [
  { href: '#/main', label: '🏠 Главная' },
  { href: '#/favorites', label: '❤️ Избранное' },
  { href: '#/create', label: '➕ Подать' },
  { href: '#/profile', label: '👤 Профиль' }
];

export function renderBottomNav(active) {
  const nav = document.getElementById('bottomNav');
  nav.innerHTML = tabs
    .map((t) => `<a href="${t.href}" class="${active === t.href ? 'active' : ''}">${t.label}</a>`)
    .join('');
}
