const tabs = [
  { key: 'main', label: '🏠 Главная', href: '#/main' },
  { key: 'favorites', label: '❤️ Избранное', href: '#/favorites' },
  { key: 'create', label: '➕ Подать', href: '#/create' },
  { key: 'profile', label: '👤 Профиль', href: '#/profile' }
];

export function renderBottomNav(activeKey) {
  const node = document.getElementById('bottomNav');
  node.innerHTML = `<div class="bottom-tabs">${tabs.map((t) => `<a class="tab ${activeKey === t.key ? 'active' : ''}" href="${t.href}">${t.label}</a>`).join('')}</div>`;
}
