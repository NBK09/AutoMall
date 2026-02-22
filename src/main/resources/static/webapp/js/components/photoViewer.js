export function openPhotoViewer(photos = [], start = 0) {
  if (!photos.length) return;
  let idx = start;
  let scale = 1;
  let x = 0;
  let y = 0;
  const root = document.createElement('div');
  root.className = 'photo-viewer';
  root.innerHTML = `<img/><button class="btn ghost" style="position:fixed;top:12px;right:12px">Закрыть</button>`;
  const img = root.querySelector('img');
  const closeBtn = root.querySelector('button');

  const render = () => {
    img.src = photos[idx];
    img.style.transform = `translate(${x}px, ${y}px) scale(${scale})`;
  };
  const close = () => root.remove();
  closeBtn.onclick = close;

  let sx = 0;
  root.addEventListener('pointerdown', (e) => { sx = e.clientX; root.setPointerCapture(e.pointerId); });
  root.addEventListener('pointerup', (e) => {
    const dx = e.clientX - sx;
    if (Math.abs(dx) > 50 && scale === 1) {
      idx = dx < 0 ? (idx + 1) % photos.length : (idx - 1 + photos.length) % photos.length;
      render();
    }
  });
  img.addEventListener('dblclick', () => {
    scale = scale > 1 ? 1 : 2;
    x = 0; y = 0; render();
  });
  img.addEventListener('pointermove', (e) => {
    if (scale <= 1 || e.buttons !== 1) return;
    x += e.movementX; y += e.movementY; render();
  });

  render();
  document.body.appendChild(root);
}
