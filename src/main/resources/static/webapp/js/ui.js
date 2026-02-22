export function escapeHtml(str = '') {
  return String(str).replace(/[&<>'"]/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '\'': '&#39;', '"': '&quot;' }[c]));
}

export function toast(message) {
  const t = document.getElementById('toast');
  t.textContent = message;
  t.style.display = 'block';
  setTimeout(() => (t.style.display = 'none'), 2200);
}

export async function loadTemplate(path) {
  const res = await fetch(path);
  return res.text();
}
