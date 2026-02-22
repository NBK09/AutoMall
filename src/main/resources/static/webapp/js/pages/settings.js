import { loadTemplate, toast } from '../ui.js';
import { saveSettings, store } from '../store.js';

export async function renderSettingsPage(root) {
  root.innerHTML = await loadTemplate('/webapp/js/pages/settings.html');
  const s = store.settings;
  root.querySelector('#allowMessages').checked = !!s.allowMessages;
  root.querySelector('#showPhone').checked = !!s.showPhone;
  root.querySelector('#phone').value = s.phone || '';
  root.querySelector('#country').value = s.country || 'RU';

  root.addEventListener('change', (e) => {
    if (e.target.id === 'allowMessages' || e.target.id === 'showPhone' || e.target.id === 'country') {
      saveSettings({ allowMessages: root.querySelector('#allowMessages').checked, showPhone: root.querySelector('#showPhone').checked, country: root.querySelector('#country').value });
      // TODO: sync settings endpoint when backend is available.
    }
  });
  root.querySelector('#savePhone').addEventListener('click', () => {
    saveSettings({ phone: root.querySelector('#phone').value.trim() });
    toast('Номер сохранён');
    // TODO: sync phone endpoint when backend is available.
  });
}
