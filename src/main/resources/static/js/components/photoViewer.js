let state = { index: 0, scale: 1, x: 0, y: 0, photos: [] };

function paint(img) {
  img.style.transform = `translate(${state.x}px, ${state.y}px) scale(${state.scale})`;
}

export function openPhotoViewer(photos, startIndex = 0) {
  state = { index: startIndex, scale: 1, x: 0, y: 0, photos };
  const root = document.createElement('div');
  root.className = 'viewer';
  root.innerHTML = `<div class="viewer-head"><button class="btn ghost" id="vClose">✕</button><span class="pill" id="vCount"></span></div><img id="vImg"/><button class="btn ghost" style="position:absolute;left:8px" id="vPrev">‹</button><button class="btn ghost" style="position:absolute;right:8px" id="vNext">›</button>`;
  document.body.appendChild(root);
  const img = root.querySelector('#vImg');
  const count = root.querySelector('#vCount');
  const render = () => { img.src = state.photos[state.index] || ''; count.textContent = `${state.index + 1}/${state.photos.length}`; paint(img); };
  render();
  root.querySelector('#vClose').onclick = () => root.remove();
  root.querySelector('#vPrev').onclick = () => { state.index = (state.index - 1 + state.photos.length) % state.photos.length; state.scale = 1; state.x = 0; state.y = 0; render(); };
  root.querySelector('#vNext').onclick = () => { state.index = (state.index + 1) % state.photos.length; state.scale = 1; state.x = 0; state.y = 0; render(); };
  let dragging = false, sx = 0, sy = 0;
  img.addEventListener('dblclick', () => { state.scale = state.scale > 1 ? 1 : 2; state.x = 0; state.y = 0; paint(img); });
  img.addEventListener('pointerdown', (e) => { dragging = true; sx = e.clientX; sy = e.clientY; });
  root.addEventListener('pointermove', (e) => { if (!dragging || state.scale <= 1) return; state.x += e.clientX - sx; state.y += e.clientY - sy; sx = e.clientX; sy = e.clientY; paint(img); });
  root.addEventListener('pointerup', () => dragging = false);
}
