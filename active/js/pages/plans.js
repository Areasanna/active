let planDraft = null;
const SPLIT_OPTS = ['UPPER_PUSH','UPPER_PULL','LOWER_PUSH','LOWER_PULL','FULL_BODY'];
const DAY_NAMES  = ['Segunda','Terça','Quarta','Quinta','Sexta','Sábado','Domingo'];

// Retorna o HTML da página de planos
function pagePlans() {
  return `
    <div class="page-header">
      <div class="page-title">PLANOS DE <em>TREINO</em></div>
      <div class="page-sub">Periodização semanal com dias e exercícios estruturados</div>
    </div>
    <div class="filter-bar">
      <div class="field" style="min-width:180px">
        <label>Nome do plano</label>
        <input id="flt-plan-name" placeholder="Buscar..." />
      </div>
      <div class="field" style="min-width:150px">
        <label>Objetivo</label>
        <select id="flt-plan-goal">
          <option value="">Todos</option>
          ${['WEIGHT_LOSS','MUSCLE_GAIN','CONDITIONING'].map(g=>
            `<option value="${g}">${humanize(g)}</option>`).join('')}
        </select>
      </div>
      <button class="btn btn-ghost btn-sm" id="btn-plan-clear">Limpar</button>
      <button class="btn btn-acc btn-sm ml-a" id="btn-plan-new">+ Novo Plano</button>
    </div>
    <div class="card" id="plans-table-wrap">
      <div class="loading-box">${spinner()}</div>
    </div>
  `;
}

// Retorna um objeto com os filtros atuais da tela
function currentPlanFilters() {
  return {
    name: document.getElementById('flt-plan-name')?.value || undefined,
    goal: document.getElementById('flt-plan-goal')?.value || undefined,
  };
}

// Carrega a lista de planos da API
async function loadPlans(params = {}) {
  const wrap = document.getElementById('plans-table-wrap');
  if (!wrap) return;
  wrap.innerHTML = `<div class="loading-box">${spinner()}</div>`;
  try {
    const res  = await api.listPlans(params);
    const data = res?.content ?? res ?? [];
    if (!data.length) { wrap.innerHTML = empty('📋','Nenhum plano criado ainda.'); return; }
    const GOAL = {WEIGHT_LOSS:'br',MUSCLE_GAIN:'bb',CONDITIONING:'bg'};
    wrap.innerHTML = `
      <table>
        <thead><tr><th>Nome</th><th>Objetivo</th><th>Semanas</th><th>Criado em</th><th></th></tr></thead>
        <tbody>
          ${data.map(p=>`
            <tr>
              <td class="fw5">${esc(p.name)}</td>
              <td><span class="badge ${GOAL[p.goal]||'bk'}">${humanize(p.goal)}</span></td>
              <td class="mono">${p.weekCount}</td>
              <td class="muted fs-12">${fmtDate(p.createdAt)}</td>
              <td>
                <button class="btn btn-ghost btn-xs" data-plan-detail="${p.id}">Ver detalhes →</button>
              </td>
            </tr>
          `).join('')}
        </tbody>
      </table>
    `;
    wrap.querySelectorAll('[data-plan-detail]').forEach(btn =>
      btn.addEventListener('click', () => openPlanDetail(+btn.dataset.planDetail))
    );
  } catch(e) {
    wrap.innerHTML = `<div class="alert alert-err">${esc(loadErrorMsg(e, 'planos'))}</div>`;
  }
}

// Busca detalhes completos do plano na API
async function openPlanDetail(id) {
  showModal('Carregando plano...', `<div class="loading-box">${spinner()}</div>`);
  try {
    const plan = await api.getPlan(id);
    if (!plan) { closeModal(); return; }
    const SPLIT = {UPPER_PUSH:'bb',UPPER_PULL:'bk',LOWER_PUSH:'br',LOWER_PULL:'bg',FULL_BODY:'bg'};
    const DAYS  = ['Segunda','Terça','Quarta','Quinta','Sexta','Sábado','Domingo'];
    showModal(esc(plan.name), `
      <div class="flex gap-8 mb-12">
        <span class="badge bb">${humanize(plan.goal)}</span>
        <span class="badge bk">${plan.weekCount} semanas</span>
      </div>
      ${(plan.weeks||[]).map(w=>`
        <div class="week-block">
          <div class="week-head">SEMANA ${w.weekNumber}</div>
          ${(w.days||[]).map(d=>`
            <div class="day-block">
              <div class="day-head">
                ${DAYS[(d.dayOfWeek||1)-1]||'Dia '+d.dayOfWeek}
                ${d.splitFocus ? `<span class="badge ${SPLIT[d.splitFocus]||'bk'}">${humanize(d.splitFocus)}</span>` : ''}
              </div>
              ${(d.exercises||[]).map(s=>`
                <div class="flex items-center" style="font-size:12px;padding:5px 0;border-top:1px solid var(--border);justify-content:space-between">
                  <span>${esc(s.exerciseTitle||s.exercise?.title||`#${s.exerciseId}`)}</span>
                  <span class="mono muted">${s.sets}×${s.reps} · ${s.restSeconds}s</span>
                </div>
              `).join('')}
            </div>
          `).join('')}
        </div>
      `).join('')}
      <div class="modal-foot">
        <button class="btn btn-ghost" onclick="closeModal()">Fechar</button>
      </div>
    `);
  } catch(err) {
    closeModal();
    toast(loadErrorMsg(err, 'plano'));
  }
}

// Iniciar criação/edição de plano
function openPlanWizard() {
  planDraft = { name:'', goal:'MUSCLE_GAIN', weekCount: 4, weeks: [] };
  showModal('Novo Plano — Informações', planWizardStep1());
  document.getElementById('btn-pw-next').addEventListener('click', () => {
    planDraft.name      = document.getElementById('pw-name').value.trim();
    planDraft.goal      = document.getElementById('pw-goal').value;
    planDraft.weekCount = +document.getElementById('pw-weeks').value;
    if (!planDraft.name) { alert$('pw-alert','O nome do plano é obrigatório.'); return; }
    planDraft.weeks = Array.from({length: planDraft.weekCount}, (_,wi) => ({
      weekNumber: wi+1,
      days: [{ dayOfWeek:1, splitFocus:'FULL_BODY', exercises:[] }],
    }));
    showModal('Novo Plano — Montar Semanas', planWizardStep2());
    bindPlanWizardStep2();
  });
}

// Retorna o HTML do primeiro passo do wizard
function planWizardStep1() {
  const goals = ['WEIGHT_LOSS','MUSCLE_GAIN','CONDITIONING'];
  return `
    <div id="pw-alert" class="alert alert-err hidden"></div>
    <div class="form-grid mb-12">
      <div class="field col-2">
        <label>Nome do Plano</label>
        <input id="pw-name" value="${esc(planDraft.name)}" placeholder="ex: Hipertrofia 4x" required />
      </div>
      <div class="field">
        <label>Objetivo</label>
        <select id="pw-goal">
          ${goals.map(g=>`<option value="${g}" ${planDraft.goal===g?'selected':''}>${humanize(g)}</option>`).join('')}
        </select>
      </div>
      <div class="field">
        <label>Número de Semanas (1–52)</label>
        <input type="number" id="pw-weeks" value="${planDraft.weekCount}" min="1" max="52" />
      </div>
    </div>
    <div class="modal-foot">
      <button class="btn btn-ghost" onclick="closeModal()">Cancelar</button>
      <button class="btn btn-acc" id="btn-pw-next">Montar semanas →</button>
    </div>
  `;
}

// Gera o HTML dos exercícios (slots) de um determinado dia
function renderSlots(wi, di) {
  return planDraft.weeks[wi].days[di].exercises.map((slot, ei) => `
    <div class="slot-row" id="slot-${wi}-${di}-${ei}">
      <div class="field">
        <select data-slot="${wi},${di},${ei},exerciseId">
          <option value="">— Exercício —</option>
          ${S.exercises.map(e=>
            `<option value="${e.id}" ${slot.exerciseId==e.id?'selected':''}>${esc(e.title)}</option>`
          ).join('')}
        </select>
      </div>
      <div class="field"><label>Séries</label>
        <input type="number" min="1" value="${slot.sets}" data-slot="${wi},${di},${ei},sets" /></div>
      <div class="field"><label>Reps</label>
        <input type="number" min="1" value="${slot.reps}" data-slot="${wi},${di},${ei},reps" /></div>
      <div class="field"><label>Descanso(s)</label>
        <input type="number" min="0" value="${slot.restSeconds}" data-slot="${wi},${di},${ei},restSeconds" /></div>
      <button class="icon-btn" data-rm-slot="${wi},${di},${ei}">✕</button>
    </div>
  `).join('');
}

// Retorna HTML para a montagem das semanas/dias do plano
function planWizardStep2() {
  return `
    <div id="pw2-alert" class="alert alert-err hidden"></div>
    <div id="pw2-weeks" style="max-height:440px;overflow-y:auto;padding-right:4px">
      ${planDraft.weeks.map((w,wi) => `
        <div class="week-block">
          <div class="week-head">SEMANA ${w.weekNumber}</div>
          ${w.days.map((d,di) => `
            <div class="day-block" id="day-${wi}-${di}">
              <div class="day-head">
                <select data-day="${wi},${di},dayOfWeek" style="width:120px">
                  ${DAY_NAMES.map((dn,idx)=>
                    `<option value="${idx+1}" ${d.dayOfWeek===idx+1?'selected':''}>${dn}</option>`
                  ).join('')}
                </select>
                <select data-day="${wi},${di},splitFocus" style="width:150px">
                  ${SPLIT_OPTS.map(s=>
                    `<option value="${s}" ${d.splitFocus===s?'selected':''}>${humanize(s)}</option>`
                  ).join('')}
                </select>
              </div>
              <div id="slots-${wi}-${di}">${renderSlots(wi,di)}</div>
              <button class="btn btn-ghost btn-xs mt-8" data-add-slot="${wi},${di}">+ Exercício</button>
            </div>
          `).join('')}
          <button class="btn btn-ghost btn-xs mt-8" data-add-day="${wi}">+ Dia</button>
        </div>
      `).join('')}
    </div>
    <div class="modal-foot">
      <button class="btn btn-ghost" id="btn-pw-back">← Voltar</button>
      <button class="btn btn-acc" id="btn-pw-save">Salvar Plano</button>
    </div>
  `;
}

// Liga todos os eventos dos botões e selects do passo 2
function bindPlanWizardStep2() {
  const modal = document.querySelector('.modal');

  modal.addEventListener('click', e => {
    const t = e.target;
    if (t.dataset.addSlot) {
      const [wi,di] = t.dataset.addSlot.split(',').map(Number);
      planDraft.weeks[wi].days[di].exercises.push({exerciseId:'',sets:3,reps:10,restSeconds:60});
      document.getElementById(`slots-${wi}-${di}`).innerHTML = renderSlots(wi,di);
      bindSlotInputs(modal);
    }
    if (t.dataset.rmSlot) {
      const [wi,di,ei] = t.dataset.rmSlot.split(',').map(Number);
      planDraft.weeks[wi].days[di].exercises.splice(ei,1);
      document.getElementById(`slots-${wi}-${di}`).innerHTML = renderSlots(wi,di);
      bindSlotInputs(modal);
    }
    if (t.dataset.addDay) {
      const wi = +t.dataset.addDay;
      const dayCount = planDraft.weeks[wi].days.length;
      planDraft.weeks[wi].days.push({dayOfWeek: Math.min(dayCount+1,7), splitFocus:'FULL_BODY', exercises:[]});
      reRenderWeeks();
    }
    if (t.id === 'btn-pw-back') {
      showModal('Novo Plano — Informações', planWizardStep1());
      document.getElementById('btn-pw-next').addEventListener('click', () => {
        planDraft.name      = document.getElementById('pw-name').value.trim();
        planDraft.goal      = document.getElementById('pw-goal').value;
        planDraft.weekCount = +document.getElementById('pw-weeks').value;
        if (!planDraft.name) { alert$('pw-alert','O nome do plano é obrigatório.'); return; }
        showModal('Novo Plano — Montar Semanas', planWizardStep2());
        bindPlanWizardStep2();
      });
    }
  });

  document.getElementById('btn-pw-save').addEventListener('click', async () => {
    const btn = document.getElementById('btn-pw-save');
    btn.disabled = true; btn.innerHTML = spinner();
    modal.querySelectorAll('[data-slot]').forEach(el => {
      const [wi,di,ei,key] = el.dataset.slot.split(',');
      planDraft.weeks[+wi].days[+di].exercises[+ei][key] = el.value;
    });
    modal.querySelectorAll('[data-day]').forEach(el => {
      const [wi,di,key] = el.dataset.day.split(',');
      planDraft.weeks[+wi].days[+di][key] = key === 'dayOfWeek' ? +el.value : el.value;
    });
    const payload = {
      name: planDraft.name, goal: planDraft.goal, weekCount: planDraft.weekCount,
      weeks: planDraft.weeks.map(w => ({
        weekNumber: w.weekNumber,
        days: w.days.map(d => ({
          dayOfWeek: +d.dayOfWeek, splitFocus: d.splitFocus,
          exercises: d.exercises.filter(s=>s.exerciseId).map(s=>({
            exerciseId:+s.exerciseId, sets:+s.sets, reps:+s.reps, restSeconds:+s.restSeconds,
          })),
        })),
      })),
    };
    try {
      await api.createPlan(payload);
      closeModal();
      toast('Plano criado com sucesso!', 'ok', 3000);
      loadPlans(currentPlanFilters());
      const res = await api.listPlans();
      S.stats.plans = (res?.content??res??[]).length;
    } catch(err) {
      alert$('pw2-alert', saveErrorMsg(err, 'plano'));
      btn.disabled = false; btn.textContent = 'Salvar Plano';
    }
  });

  bindSlotInputs(modal);
  bindDaySelects(modal);
}

// Atualização dos campos dos slots
function bindSlotInputs(modal) {
  modal.querySelectorAll('[data-slot]').forEach(el => {
    el.addEventListener('change', () => {
      const [wi,di,ei,key] = el.dataset.slot.split(',');
      planDraft.weeks[+wi].days[+di].exercises[+ei][key] = el.value;
    });
  });
}

// Atualização dos campos dos dias
function bindDaySelects(modal) {
  modal.querySelectorAll('[data-day]').forEach(el => {
    el.addEventListener('change', () => {
      const [wi,di,key] = el.dataset.day.split(',');
      planDraft.weeks[+wi].days[+di][key] = key==='dayOfWeek' ? +el.value : el.value;
    });
  });
}

// Redesenha toda a tela de semanas/dias após adicionar/remover dias ou exercícios
function reRenderWeeks() {
  const modal = document.querySelector('.modal');
  modal.innerHTML = planWizardStep2();
  bindPlanWizardStep2();
}