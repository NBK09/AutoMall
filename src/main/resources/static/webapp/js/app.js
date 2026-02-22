import { apiJson } from './api.js';
import { initRouter } from './router.js';
import { setToken } from './store.js';
import { toast } from './ui.js';

async function telegramAuth() {
  const pill = document.getElementById('authPill');
  try {
    const tg = window.Telegram?.WebApp;
    tg?.ready();
    tg?.expand();
    if (!tg?.initData) { pill.textContent = 'Откройте из Telegram'; return; }
    const data = await apiJson('/api/auth/telegram', { method: 'POST', body: JSON.stringify({ initData: tg.initData }) });
    if (data?.token) {
      setToken(data.token);
      pill.textContent = '✅ Авторизован';
    }
  } catch (e) {
    pill.textContent = 'Ошибка auth';
    toast(`Auth: ${e.message}`, false);
  }
}

document.getElementById('headerSettingsBtn').addEventListener('click', () => (location.hash = '#/settings'));
await telegramAuth();
initRouter();
