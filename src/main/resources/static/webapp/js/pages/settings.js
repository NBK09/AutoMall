import { saveSettings, store } from '../store.js';
import { toast } from '../ui.js';

export async function renderSettingsPage() {
  document.getElementById('sendMessages').checked = !!store.settings.sendMessages;
  document.getElementById('showPhone').checked = !!store.settings.showPhone;
  document.getElementById('phone').value = store.settings.phone || '';
  document.getElementById('country').value = store.settings.country || 'Россия';
  document.getElementById('saveSettings').onclick = async () => {
    saveSettings({
      sendMessages: document.getElementById('sendMessages').checked,
      showPhone: document.getElementById('showPhone').checked,
      phone: document.getElementById('phone').value.trim(),
      country: document.getElementById('country').value
    });
    // TODO: POST /api/user/settings when backend endpoint is available.
    toast('Настройки сохранены');
  };
}
