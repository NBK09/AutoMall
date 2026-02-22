export function escapeHtml(str = '') {
  return String(str).replace(/[&<>'"]/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '\'': '&#39;', '"': '&quot;' }[c]));
}

// Защищаем создание RegExp: удаляем недопустимые флаги и даем fallback, чтобы UI не падал в рантайме.
export function safeRegExp(pattern, flags = '') {
  const sanitizedFlags = String(flags).replace(/[^gimsuy]/g, '');
  try {
    return new RegExp(pattern, sanitizedFlags);
  } catch {
    try {
      return new RegExp(String(pattern || '').replace(/[.*+?^${}()|[\]\\]/g, '\\$&'));
    } catch {
      return /(?:)/;
    }
  }
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
