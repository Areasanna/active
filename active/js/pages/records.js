// Renderiza a tela de consulta de Personal Records (maior peso, maior volume, data)
// Permite selecionar qualquer exercício cadastrado e consultar seus recordes pessoais
// Mostra mensagens amigáveis se não houver registros

function pageRecords() {
  return `
    <div class="page-header">
      <div class="page-title">PERSONAL <em>RECORDS</em></div>
      <div class="page-sub">Maior peso, maior volume e data da conquista por exercício</div>
    </div>
    <div class="card mb-12">
      <div class="field" style="max-width:380px">
        <label>Selecione o exercício</label>
        <select id="pr-select">
          <option value="">— Escolha um exercício —</option>
          ${S.exercises.map(e=>`<option value="${e.id}">${esc(e.title)}</option>`).join('')}
        </select>
      </div>
    </div>
    <div id="pr-result">${empty('🏆','Selecione um exercício para ver o PR.')}</div>
  `;
}

async function loadPR(exerciseId) {
  const box = document.getElementById('pr-result');
  if (!box) return;
  box.innerHTML = `<div class="loading-box">${spinner()}</div>`;
  try {
    const pr = await api.getPR(exerciseId);
    box.innerHTML = `
      <div class="pr-grid">
        <div class="pr-card">
          <div class="pr-lbl">Maior Peso</div>
          <div><span class="pr-num">${pr.maxWeightKg}</span><span class="pr-unit">kg</span></div>
        </div>
        <div class="pr-card">
          <div class="pr-lbl">Maior Volume</div>
          <div><span class="pr-num">${pr.maxReps}</span><span class="pr-unit">reps</span></div>
        </div>
        <div class="pr-card">
          <div class="pr-lbl">Data da Conquista</div>
          <div><span class="pr-num" style="font-size:22px">${fmtDate(pr.achievedAt)}</span></div>
        </div>
      </div>
      <div class="card">
        <div class="flex items-center gap-8">
          <span style="font-family:var(--font-disp);font-weight:700;font-size:18px">🏆 ${esc(pr.exerciseTitle)}</span>
          <span class="badge bk">ID #${pr.exerciseId}</span>
        </div>
      </div>
    `;
  } catch(e) {
    if (e.status === 404) {
      box.innerHTML = empty('🏋️','Nenhum registro encontrado para este exercício ainda. Registre uma sessão para começar!');
    } else {
      box.innerHTML = `<div class="alert alert-err">${esc(loadErrorMsg(e, 'personal record'))}</div>`;
    }
  }
}