// Renderiza a tela de histórico de sessões de treino, com filtros por data/plano/exercício.
// Permite ver detalhes completos de cada sessão em um modal.
// Permite registrar novas sessões via modal interativo, adicionando/removendo exercícios e séries dinamicamente.

let sessionDraft = null;

function pageSessions() {
  return `
    <div class="page-header">
      <div class="page-title">SESSÕES DE <em>TREINO</em></div>
      <div class="page-sub">Histórico de treinos com filtros por data, plano e exercício</div>
    </div>
    <div class="filter-bar">
      <div class="field"><label>De</label>
        <input type="date" id="flt-ss-from" /></div>
      <div class="field"><label>Até</label>
        <input type="date" id="flt-ss-to" /></div>
      <div class="field" style="min-width:160px"><label>Nome do plano</label>
        <input id="flt-ss-plan" placeholder="Buscar plano..." /></div>
      <div class="field" style="min-width:150px"><label>Exercício</label>
        <select id="flt-ss-ex">
          <option value="">Todos</option>
          ${S.exercises.map(e=>`<option value="${e.id}">${esc(e.title)}</option>`).join('')}
        </select>
      </div>
      <button class="btn btn-ghost btn-sm" id="btn-ss-clear">Limpar</button>
      <button class="btn btn-acc btn-sm ml-a" id="btn-ss-new">+ Registrar Treino</button>
    </div>
    <div class="card" id="ss-table-wrap">
      <div class="loading-box">${spinner()}</div>
    </div>
  `;
}

async function loadSessions(params = {}) {
  const wrap = document.getElementById('ss-table-wrap');
  if (!wrap) return;
  wrap.innerHTML = `<div class="loading-box">${spinner()}</div>`;
  try {
    const res  = await api.listSessions(params);
    const data = res?.content ?? res ?? [];
    if (!data.length) { wrap.innerHTML = empty('🏋️','Nenhuma sessão registrada ainda.'); return; }
    wrap.innerHTML = `
      <table>
        <thead><tr><th>Data</th><th>Plano/Dia</th><th>Exercícios</th><th>Registrado em</th><th></th></tr></thead>
        <tbody>
          ${data.map(s=>`
            <tr>
              <td class="mono">${fmtDate(s.date)}</td>
              <td class="muted fs-12">${s.trainingPlanDayId ? 'Dia #'+s.trainingPlanDayId : 'Treino livre'}</td>
              <td><span class="badge bg">${s.totalExercises} ex.</span></td>
              <td class="muted fs-12">${fmtDT(s.createdAt)}</td>
              <td><button class="btn btn-ghost btn-xs" data-ss-detail="${s.id}">Detalhes →</button></td>
            </tr>
          `).join('')}
        </tbody>
      </table>
    `;
    wrap.querySelectorAll('[data-ss-detail]').forEach(btn =>
      btn.addEventListener('click', () => openSessionDetail(+btn.dataset.ssDetail))
    );
  } catch(e) {
    wrap.innerHTML = `<div class="alert alert-err">${esc(loadErrorMsg(e, 'sessões'))}</div>`;
  }
}

async function openSessionDetail(id) {
  showModal('Detalhes da Sessão', `<div class="loading-box">${spinner()}</div>`);
  try {
    const s = await api.getSession(id);
    if (!s) { closeModal(); return; }
    showModal('Detalhes da Sessão', `
      <div class="flex gap-8 mb-12">
        <span class="badge bk">📅 ${fmtDate(s.date)}</span>
        <span class="badge bg">${s.totalExercises} exercícios</span>
      </div>
      ${(s.exercises||[]).map(ex=>`
        <div style="margin-bottom:16px">
          <div style="font-family:var(--font-disp);font-weight:700;font-size:15px;margin-bottom:8px">
            ${esc(ex.exerciseTitle)}
          </div>
          <table>
            <thead><tr><th>Série</th><th>Reps</th><th>Peso (kg)</th></tr></thead>
            <tbody>
              ${(ex.sets||[]).map((set,i)=>`
                <tr>
                  <td class="mono muted">${i+1}</td>
                  <td class="mono">${set.reps}</td>
                  <td class="mono acc">${set.weightKg}</td>
                </tr>`).join('')}
            </tbody>
          </table>
        </div>
      `).join('')}
      <div class="modal-foot">
        <button class="btn btn-ghost" onclick="closeModal()">Fechar</button>
      </div>
    `);
  } catch(err) {
    closeModal();
    toast(loadErrorMsg(err, 'sessão'));
  }
}

function openSessionForm() {
  const today = new Date().toISOString().split('T')[0];
  sessionDraft = {
    date: today,
    trainingPlanDayId: '',
    exercises: [{ exerciseId:'', sets:[{reps:10, weightKg:0}] }],
  };
  showModal('Registrar Treino', renderSessionForm());
  bindSessionForm();
}

function renderSessionForm() {
  return `
    <div id="ss-form-alert" class="alert alert-err hidden"></div>
    <div class="form-grid mb-12">
      <div class="field">
        <label>Data do Treino</label>
        <input type="date" id="sf-date" value="${sessionDraft.date}" />
      </div>
      <div class="field">
        <label>ID do Dia do Plano (opcional)</label>
        <input type="number" id="sf-dayid" value="${sessionDraft.trainingPlanDayId}"
          placeholder="Deixe vazio para treino livre" />
      </div>
    </div>
    <div id="sf-exercises" style="max-height:360px;overflow-y:auto;padding-right:4px">
      ${sessionDraft.exercises.map((ex,ei) => renderSessionExercise(ei)).join('')}
    </div>
    <button class="btn btn-ghost btn-sm mt-8" id="sf-add-ex">+ Exercício</button>
    <div class="modal-foot">
      <button class="btn btn-ghost" onclick="closeModal()">Cancelar</button>
      <button class="btn btn-acc" id="btn-ss-save">Salvar Sessão</button>
    </div>
  `;
}

function renderSessionExercise(ei) {
  const ex = sessionDraft.exercises[ei];
  return `
    <div class="week-block" id="sf-ex-${ei}">
      <div class="flex items-center gap-8 mb-12">
        <select style="flex:1" data-sf-exid="${ei}">
          <option value="">— Selecione exercício —</option>
          ${S.exercises.map(e=>
            `<option value="${e.id}" ${ex.exerciseId==e.id?'selected':''}>${esc(e.title)}</option>`
          ).join('')}
        </select>
        <button class="icon-btn" data-sf-rm-ex="${ei}">✕</button>
      </div>
      <div id="sf-sets-${ei}">
        ${ex.sets.map((s,si) => renderSessionSet(ei,si)).join('')}
      </div>
      <button class="btn btn-ghost btn-xs mt-8" data-sf-add-set="${ei}">+ Série</button>
    </div>
  `;
}

function renderSessionSet(ei, si) {
  const s = sessionDraft.exercises[ei].sets[si];
  return `
    <div class="set-row" id="sf-set-${ei}-${si}">
      <div class="field">
        <label>Reps</label>
        <input type="number" min="1" value="${s.reps}" data-sf-set="${ei},${si},reps" />
      </div>
      <div class="field">
        <label>Peso (kg)</label>
        <input type="number" step="0.5" min="0" value="${s.weightKg}" data-sf-set="${ei},${si},weightKg" />
      </div>
      <button class="icon-btn" data-sf-rm-set="${ei},${si}">−</button>
    </div>
  `;
}

function bindSessionForm() {
  const modal = document.querySelector('.modal');

  modal.addEventListener('click', e => {
    const t = e.target;
    if (t.dataset.sfAddSet !== undefined) {
      const ei = +t.dataset.sfAddSet;
      sessionDraft.exercises[ei].sets.push({reps:10,weightKg:0});
      document.getElementById(`sf-sets-${ei}`).innerHTML =
        sessionDraft.exercises[ei].sets.map((_,si)=>renderSessionSet(ei,si)).join('');
      rebindSessionInputs(modal);
    }
    if (t.dataset.sfRmSet !== undefined) {
      const [ei,si] = t.dataset.sfRmSet.split(',').map(Number);
      sessionDraft.exercises[ei].sets.splice(si,1);
      document.getElementById(`sf-sets-${ei}`).innerHTML =
        sessionDraft.exercises[ei].sets.map((_,si2)=>renderSessionSet(ei,si2)).join('');
      rebindSessionInputs(modal);
    }
    if (t.dataset.sfRmEx !== undefined) {
      const ei = +t.dataset.sfRmEx;
      sessionDraft.exercises.splice(ei,1);
      document.getElementById('sf-exercises').innerHTML =
        sessionDraft.exercises.map((_,i)=>renderSessionExercise(i)).join('');
      rebindSessionInputs(modal);
    }
    if (t.id === 'sf-add-ex') {
      sessionDraft.exercises.push({exerciseId:'', sets:[{reps:10,weightKg:0}]});
      document.getElementById('sf-exercises').innerHTML =
        sessionDraft.exercises.map((_,i)=>renderSessionExercise(i)).join('');
      rebindSessionInputs(modal);
    }
  });

  document.getElementById('btn-ss-save').addEventListener('click', async () => {
    const btn = document.getElementById('btn-ss-save');
    btn.disabled = true; btn.innerHTML = spinner();
    modal.querySelectorAll('[data-sf-set]').forEach(el => {
      const [ei,si,key] = el.dataset.sfSet.split(',');
      sessionDraft.exercises[+ei].sets[+si][key] = +el.value;
    });
    modal.querySelectorAll('[data-sf-exid]').forEach(el => {
      sessionDraft.exercises[+el.dataset.sfExid].exerciseId = el.value;
    });
    sessionDraft.date              = document.getElementById('sf-date').value;
    sessionDraft.trainingPlanDayId = document.getElementById('sf-dayid').value;

    const payload = {
      date:              sessionDraft.date,
      trainingPlanDayId: sessionDraft.trainingPlanDayId ? +sessionDraft.trainingPlanDayId : null,
      exercises: sessionDraft.exercises.filter(ex=>ex.exerciseId).map(ex=>({
        exerciseId: +ex.exerciseId,
        sets: ex.sets.map(s=>({ reps:+s.reps, weightKg:+s.weightKg })),
      })),
    };
    try {
      await api.createSession(payload);
      closeModal();
      toast('Sessão registrada com sucesso!', 'ok', 3000);
      loadSessions();
      const res = await api.listSessions();
      S.stats.sessions = (res?.content??res??[]).length;
    } catch(err) {
      alert$('ss-form-alert', saveErrorMsg(err, 'sessão'));
      btn.disabled = false; btn.textContent = 'Salvar Sessão';
    }
  });

  rebindSessionInputs(modal);
}

function rebindSessionInputs(modal) {
  modal.querySelectorAll('[data-sf-set]').forEach(el =>
    el.addEventListener('change', () => {
      const [ei,si,key] = el.dataset.sfSet.split(',');
      sessionDraft.exercises[+ei].sets[+si][key] = +el.value;
    })
  );
  modal.querySelectorAll('[data-sf-exid]').forEach(el =>
    el.addEventListener('change', () => {
      sessionDraft.exercises[+el.dataset.sfExid].exerciseId = el.value;
    })
  );
}