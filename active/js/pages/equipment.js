// Renderiza a página de equipamentos: Mostra cabeçalho e subtítulo. Se admin, mostra formulário para cadastrar novo equipamento.
// Sempre mostra tabela com todos os equipamentos cadastrados.

function pageEquipment() {
  const isAdmin = S.user?.role === 'ADMIN';
  return `
    <div class="page-header">
      <div class="page-title">EQUI<em>PAMENTOS</em></div>
      <div class="page-sub">Barras, halteres, máquinas e acessórios</div>
    </div>
    ${isAdmin ? `
    <div class="card mb-12">
      <div class="card-title" style="margin-bottom:14px">Novo Equipamento</div>
      <div id="eq-alert" class="alert alert-err hidden"></div>
      <div class="flex gap-12" style="align-items:flex-end">
        <div class="field" style="flex:1"><label>Nome</label>
          <input id="eq-name" placeholder="ex: Barra olímpica" /></div>
        <button class="btn btn-acc btn-sm" id="btn-eq-save">+ Adicionar</button>
      </div>
    </div>` : ''}
    <div class="card">
      <table>
        <thead><tr><th>#</th><th>Nome</th></tr></thead>
        <tbody>
          ${S.equipments.length
            ? S.equipments.map(e=>`
              <tr>
                <td class="mono muted fs-12">${e.id}</td>
                <td class="fw5">${esc(e.name)}</td>
              </tr>`).join('')
            : `<tr><td colspan="2">${empty('🔩','Nenhum equipamento cadastrado.')}</td></tr>`
          }
        </tbody>
      </table>
    </div>
  `;
}