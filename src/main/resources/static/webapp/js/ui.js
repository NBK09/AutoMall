export function escapeHtml(value = '') {
  return String(value)
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#039;');
}

export function toast(message, ok = true) {
  const node = document.getElementById('toast');
  node.textContent = message;
  node.style.display = 'block';
  node.style.background = ok ? '#111' : '#8d1f2f';
  clearTimeout(node._t);
  node._t = setTimeout(() => (node.style.display = 'none'), 2400);
}

export async function loadTemplate(name) {
  const res = await fetch(`/webapp/js/pages/${name}.html`);
  return res.text();
}
