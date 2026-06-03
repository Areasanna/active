// Define funções para feedback visual ao usuário, Toast: notificações globais temporárias
// Alert inline: mensagens fixas em formulários/modais. Modal: janelas modais customizáveis,
// Gerencia avisos automáticos de perda/retorno de conexão

// Define os ícones usados em cada tipo de toast: erro, sucesso, informação.
const TOAST_ICONS = { err: '✕', ok: '✓', info: 'ℹ' };

// Função principal de toast
function toast(msg, type = 'err', duration = 5000) {
  const container = document.getElementById('toast-container');
  if (!container) return;

  const el = document.createElement('div');
  el.className = `toast toast-${type}`;
  el.setAttribute('role', type === 'err' ? 'alert' : 'status');
  el.innerHTML = `
    <span class="toast-icon" aria-hidden="true">${TOAST_ICONS[type] || '•'}</span>
    <span class="toast-msg">${esc(msg)}</span>
    <button class="toast-close" aria-label="Fechar notificação">✕</button>
  `;

  const dismiss = () => {
    el.style.animation = 'toastOut .3s ease forwards';
    setTimeout(() => el.remove(), 300);
  };

  el.querySelector('.toast-close').addEventListener('click', dismiss);
  container.appendChild(el);
  if (duration > 0) setTimeout(dismiss, duration);
}

// Mantido para alertas dentro de formulários (dentro de modais ou páginas).
// Se o elemento não existir na página, cai no toast como fallback.
function alert$(id, msg, type = 'err') {
  const el = document.getElementById(id);
  if (!el) { toast(msg, type); return; }
  el.className = `alert alert-${type}`;
  el.innerText = msg;
  el.classList.remove('hidden');
}

// Função para esconder alertas
function clearAlert(id) {
  const el = document.getElementById(id);
  if (el) el.classList.add('hidden');
}

// Monta o HTML do modal dentro do elemento com id modal-root
function showModal(title, body) {
  document.getElementById('modal-root').innerHTML = `
    <div class="overlay" id="modal-overlay">
      <div class="modal">
        <div class="modal-head">
          <div class="modal-title">${title}</div>
          <button class="btn btn-ghost btn-sm" onclick="closeModal()">✕</button>
        </div>
        ${body}
      </div>
    </div>
  `;
  document.getElementById('modal-overlay').addEventListener('click', e => {
    if (e.target.id === 'modal-overlay') closeModal();
  });
}

// Limpa o conteúdo do modal
function closeModal() {
  document.getElementById('modal-root').innerHTML = '';
}

// Mostra toast em tempo real se perder conexão com a internet, (duration=0 faz ficar até o usuário fechar)
window.addEventListener('offline', () => {
  toast('Você está sem internet. Algumas ações podem falhar.', 'err', 0);
});
window.addEventListener('online', () => {
  document.querySelectorAll('.toast-err').forEach(el => {
    if (el.querySelector('.toast-msg')?.textContent.includes('sem internet')) {
      el.style.animation = 'toastOut .3s ease forwards';
      setTimeout(() => el.remove(), 300);
    }
  });
  toast('Conexão restabelecida.', 'ok', 3000);
});