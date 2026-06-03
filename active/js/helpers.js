// Funções utilitárias (helpers) para formatação (datas, textos),
// escaping de strings para HTML e carregamento inicial de dados globais

// Torna nomes como NOME_USUARIO em "Nome usuario"
const humanize = s => s
  ? s.replace(/_/g,' ').toLowerCase().replace(/^\w/, c => c.toUpperCase())
  : '—';

// Exibi datas amigáveis ao usuário, data e horário juntos, no formato brasileiro.
const fmtDate = s => s ? new Date(s).toLocaleDateString('pt-BR') : '—';
const fmtDT   = s => s ? new Date(s).toLocaleString('pt-BR')     : '—';
const esc     = s => String(s ?? '').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
const spinner = () => `<span class="spinner"></span>`;

function empty(icon, msg) {
  return `<div class="empty"><div class="empty-icon">${icon}</div><div>${msg}</div></div>`;
}

// Inicializar dados globais da aplicação (listas de referência)
// para evitar múltiplos requests depois.
async function boot() {
  if (!S.token) return;
  try {
    const [m, eq, ex, pl, ss] = await Promise.allSettled([
      api.listMuscles({ size: 200 }),
      api.listEquipments({ size: 200 }),
      api.listExercises({ size: 200 }),
      api.listPlans(),
      api.listSessions(),
    ]);
    S.muscles    = m.value?.content   ?? m.value   ?? [];
    S.equipments = eq.value?.content  ?? eq.value  ?? [];
    S.exercises  = ex.value?.content  ?? ex.value  ?? [];
    const plans    = pl.value?.content ?? pl.value ?? [];
    const sessions = ss.value?.content ?? ss.value ?? [];
    S.stats = {
      exercises: S.exercises.length,
      plans:     plans.length,
      sessions:  sessions.length,
      prs:       S.exercises.length,
    };
  } catch(e) {
    console.warn('boot error', e);
    if (e instanceof ApiError && e.type === 'network') {
      toast(e.message, 'err', 8000);
    }
  }
}