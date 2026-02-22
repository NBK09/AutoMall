import { api } from './api.js';
import { initRouter } from './router.js';
import { setToken } from './store.js';
import { toast } from './ui.js';

async function telegramAuth() {
  const authPill = document.getElementById('authPill');
  try {
    const tg = window.Telegram?.WebApp;
    tg?.ready();
    tg?.expand();
    if (!tg?.initData) {
      authPill.textContent = '⚠️ открой через Telegram';
      return;
    }
    const res = await api('/api/auth/telegram', { method: 'POST', body: JSON.stringify({ initData: tg.initData }) });
    if (!res.ok) throw new Error(`Auth ${res.status}`);
    const data = await res.json();
    if (data?.token) {
      setToken(data.token);
      authPill.textContent = '✅ авторизован';
    }
  } catch (e) {
    authPill.textContent = '⛔ auth error';
    toast('Ошибка авторизации');
  }
}

telegramAuth().finally(initRouter);
