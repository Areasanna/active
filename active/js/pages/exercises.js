// Página completa de catálogo/listagem de exercícios:
// Filtros por categoria, músculo e equipamento
// Permite adicionar/editar/excluir exercícios (se admin)
// Mostra detalhes completos em modal
// Formulário dinâmico e interativo para cadastro/edição

const CAT_BADGE = {
  CHEST:'bb', BACK:'bg', LEGS:'br', ARMS:'bb',
  SHOULDERS:'bk', ABS:'bk', CARDIO:'bg', CALVES:'bk',
};

// Montar o layout inicial da página e interface de filtros
function pageExercises() {
  const isAdmin = S.user?.role === 'ADMIN';
  const cats    = ['ABS','ARMS','BACK','CALVES','CARDIO','CHEST','LEGS','SHOULDERS'];
  return `
    <div class="page-header">
      <div class="page-title">EXER<em>CÍCIOS</em></div>
      <div class="page-sub">Catálogo com filtros por categoria, músculo e equipamento</div>
    </div>
    <div class="filter-bar">
      <div class="field" style="min-width:150px">
        <label>Categoria</label>
        <select id="flt-cat">
          <option value="">Todas</option>
          ${cats.map(c => `<option value="${c}">${humanize(c)}</option>`).join('')}
        </select>
      </div>
      <div class="field" style="min-width:150px">
        <label>Músculo</label>
        <select id="flt-muscle">
          <option value="">Todos</option>
          ${S.muscles.map(m => `<option value="${m.id}">${esc(m.name)}</option>`).join('')}
        </select>
      </div>
      <div class="field" style="min-width:150px">
        <label>Equipamento</label>
        <select id="flt-eq">
          <option value="">Todos</option>
          ${S.equipments.map(e => `<option value="${e.id}">${esc(e.name)}</option>`).join('')}
        </select>
      </div>
      <button class="btn btn-ghost btn-sm" id="btn-ex-clear">Limpar</button>
      ${isAdmin ? `<button class="btn btn-acc btn-sm ml-a" id="btn-ex-new">+ Novo Exercício</button>` : ''}
    </div>
    <div class="card" id="ex-table-wrap">
      <div class="loading-box">${spinner()}</div>
    </div>
  `;
}

// Facilita pegar filtros para recarregar a lista conforme seleção do usuário
function currentExFilters() {
  return {
    category:    document.getElementById('flt-cat')?.value    || undefined,
    muscleId:    document.getElementById('flt-muscle')?.value || undefined,
    equipmentId: document.getElementById('flt-eq')?.value     || undefined,
  };
}

// Função para carregar exercícios
async function loadExercises(params = {}) {
  const wrap = document.getElementById('ex-table-wrap');
  if (!wrap) return;
  wrap.innerHTML = `<div class="loading-box">${spinner()}</div>`;
  try {
    const res     = await api.listExercises(params);
    const data    = res?.content ?? res ?? [];
    const isAdmin = S.user?.role === 'ADMIN';
    if (!data.length) { wrap.innerHTML = empty('💪','Nenhum exercício encontrado.'); return; }
    wrap.innerHTML = `
      <table>
        <thead>
          <tr>
            <th>Nome</th><th>Categoria</th>
            <th>Músculos Primários</th><th>Equipamentos</th>
            ${isAdmin ? '<th>Ações</th>' : ''}
          </tr>
        </thead>
        <tbody>
          ${data.map(ex => `
            <tr>
              <td>
                <span class="fw5 link" data-ex-detail="${ex.id}">${esc(ex.title)}</span>
                ${ex.videoUrl ? `<a href="${esc(ex.videoUrl)}" target="_blank"
                  style="margin-left:8px;font-size:11px;color:var(--accent)">▶ vídeo</a>` : ''}
              </td>
              <td><span class="badge ${CAT_BADGE[ex.category]||'bk'}">${humanize(ex.category)}</span></td>
              <td class="muted fs-12">${ex.primaryMuscles?.map(m=>esc(m.name)).join(', ')||'—'}</td>
              <td class="muted fs-12">${ex.equipment?.map(e=>esc(e.name)).join(', ')||'—'}</td>
              ${isAdmin ? `
                <td>
                  <div class="flex gap-8">
                    <button class="btn btn-ghost btn-xs" data-ex-edit="${ex.id}">Editar</button>
                    <button class="btn btn-danger btn-xs" data-ex-del="${ex.id}">Excluir</button>
                  </div>
                </td>` : ''}
            </tr>
          `).join('')}
        </tbody>
      </table>
    `;
    wrap.querySelectorAll('[data-ex-detail]').forEach(el =>
      el.addEventListener('click', () => openExerciseDetail(+el.dataset.exDetail))
    );
    if (isAdmin) {
      wrap.querySelectorAll('[data-ex-edit]').forEach(el =>
        el.addEventListener('click', () => openExerciseForm(+el.dataset.exEdit))
      );
      wrap.querySelectorAll('[data-ex-del]').forEach(el =>
        el.addEventListener('click', async () => {
          if (!confirm('Excluir exercício?')) return;
          try {
            await api.deleteExercise(+el.dataset.exDel);
            toast('Exercício excluído.', 'ok', 3000);
            loadExercises(currentExFilters());
          } catch(err) {
            toast(deleteErrorMsg(err, 'exercício'));
          }
        })
      );
    }
  } catch(e) {
    wrap.innerHTML = `<div class="alert alert-err">${esc(loadErrorMsg(e, 'exercícios'))}</div>`;
  }
}

// Função para mostrar detalhes do exercício
async function openExerciseDetail(id) {
  showModal('Carregando...', `<div class="loading-box">${spinner()}</div>`);
  try {
    const ex = await api.getExercise(id);
    if (!ex) { closeModal(); return; }
    showModal(esc(ex.title), `
      <div class="flex gap-8 mb-12">
        <span class="badge ${CAT_BADGE[ex.category]||'bk'}">${humanize(ex.category)}</span>
      </div>
      <p class="muted fs-12" style="line-height:1.7;margin-bottom:14px">${esc(ex.description)}</p>
      ${ex.videoUrl ? `<p class="mb-12"><a href="${esc(ex.videoUrl)}" target="_blank" class="btn btn-ghost btn-sm">▶ Ver vídeo</a></p>` : ''}
      ${ex.primaryMuscles?.length ? `
        <div class="mb-12">
          <label style="display:block;margin-bottom:6px">Músculos primários</label>
          <div class="pill-group">${ex.primaryMuscles.map(m=>`<span class="pill on">${esc(m.name)}</span>`).join('')}</div>
        </div>` : ''}
      ${ex.secondaryMuscles?.length ? `
        <div>
          <label style="display:block;margin-bottom:6px">Músculos secundários</label>
          <div class="pill-group">${ex.secondaryMuscles.map(m=>`<span class="pill">${esc(m.name)}</span>`).join('')}</div>
        </div>` : ''}
      <div class="modal-foot"><button class="btn btn-ghost" onclick="closeModal()">Fechar</button></div>
    `);
  } catch(err) {
    closeModal();
    toast(loadErrorMsg(err, 'exercício'));
  }
}

// Função para abrir formulário de exercício (novo ou editar)
function openExerciseForm(id = null) {
  const ex   = id ? S.exercises.find(e => e.id === id) : null;
  const cats = ['ABS','ARMS','BACK','CALVES','CARDIO','CHEST','LEGS','SHOULDERS'];

  showModal(id ? 'Editar Exercício' : 'Novo Exercício', `
    <div id="ex-form-alert" class="alert alert-err hidden"></div>
    <div class="form-grid mb-12">
      <div class="field col-2">
        <label>Nome</label>
        <input id="ef-title" value="${esc(ex?.title||'')}" required />
      </div>
      <div class="field">
        <label>Categoria</label>
        <select id="ef-cat">
          ${cats.map(c=>`<option value="${c}" ${ex?.category===c?'selected':''}>${humanize(c)}</option>`).join('')}
        </select>
      </div>
      <div class="field">
        <label>URL do Vídeo</label>
        <input id="ef-video" value="${esc(ex?.videoUrl||'')}" placeholder="https://..." />
      </div>
      <div class="field col-2">
        <label>Descrição</label>
        <textarea id="ef-desc" rows="3">${esc(ex?.description||'')}</textarea>
      </div>
    </div>
    <div class="field mb-12">
      <label>Músculos Primários</label>
      <div class="pill-group" id="ef-primary">
        ${S.muscles.map(m => {
          const on = ex?.primaryMuscles?.some(p=>p.id===m.id);
          return `<span class="pill ${on?'on':''}" data-pid="${m.id}">${esc(m.name)}</span>`;
        }).join('')}
      </div>
    </div>
    <div class="field mb-12">
      <label>Músculos Secundários</label>
      <div class="pill-group" id="ef-secondary">
        ${S.muscles.map(m => {
          const on = ex?.secondaryMuscles?.some(s=>s.id===m.id);
          return `<span class="pill ${on?'on':''}" data-sid="${m.id}">${esc(m.name)}</span>`;
        }).join('')}
      </div>
    </div>
    <div class="field mb-12">
      <label>Equipamentos</label>
      <div class="pill-group" id="ef-equip">
        ${S.equipments.map(e => {
          const on = ex?.equipment?.some(eq=>eq.id===e.id);
          return `<span class="pill ${on?'on':''}" data-eqid="${e.id}">${esc(e.name)}</span>`;
        }).join('')}
      </div>
    </div>
    <div class="modal-foot">
      <button class="btn btn-ghost" onclick="closeModal()">Cancelar</button>
      <button class="btn btn-acc" id="btn-ex-save">Salvar</button>
    </div>
  `);

  document.querySelectorAll('#ef-primary [data-pid]').forEach(p =>
    p.addEventListener('click', () => p.classList.toggle('on'))
  );
  document.querySelectorAll('#ef-secondary [data-sid]').forEach(p =>
    p.addEventListener('click', () => p.classList.toggle('on'))
  );
  document.querySelectorAll('#ef-equip [data-eqid]').forEach(p =>
    p.addEventListener('click', () => p.classList.toggle('on'))
  );

  document.getElementById('btn-ex-save').addEventListener('click', async () => {
    const btn = document.getElementById('btn-ex-save');
    btn.disabled = true; btn.innerHTML = spinner();
    const payload = {
      title:              document.getElementById('ef-title').value.trim(),
      category:           document.getElementById('ef-cat').value,
      videoUrl:           document.getElementById('ef-video').value.trim(),
      description:        document.getElementById('ef-desc').value.trim(),
      primaryMuscleIds:   [...document.querySelectorAll('#ef-primary .on')].map(e=>+e.dataset.pid),
      secondaryMuscleIds: [...document.querySelectorAll('#ef-secondary .on')].map(e=>+e.dataset.sid),
      equipmentIds:       [...document.querySelectorAll('#ef-equip .on')].map(e=>+e.dataset.eqid),
    };
    try {
      if (id) await api.updateExercise(id, payload);
      else    await api.createExercise(payload);
      const res = await api.listExercises({ size: 200 });
      S.exercises = res?.content ?? res ?? [];
      closeModal();
      toast(id ? 'Exercício atualizado com sucesso.' : 'Exercício criado com sucesso.', 'ok', 3000);
      loadExercises(currentExFilters());
    } catch(err) {
      alert$('ex-form-alert', saveErrorMsg(err, 'exercício'));
      btn.disabled = false; btn.textContent = 'Salvar';
    }
  });
}