// Gera o HTML da tela de login. Gera o HTML da tela de cadastro.
// Liga eventos dos formulários (submit/login/cadastro e troca entre telas).

function pageLogin() {
  return `
    <div class="login-page">
      <div class="login-box">
        <div class="login-logo">ACT<span>IVE</span></div>
        <div class="login-sub">// plataforma de gestão de treinos</div>
        <div id="login-alert" class="alert alert-err hidden"></div>
        <form class="login-form" id="form-login" novalidate>
          <div class="field">
            <label>Email</label>
            <input type="email" id="li-email" placeholder="seu@email.com" value="${esc(S.loginForm.email)}" required />
          </div>
          <div class="field">
            <label>Senha</label>
            <input type="password" id="li-pass" placeholder="••••••••" required />
          </div>
          <button class="btn btn-acc" type="submit" id="btn-li" style="margin-top:4px">
            Entrar
          </button>
        </form>
        <div class="login-switch">
          Não tem conta?
          <span class="link" id="go-register">Cadastre-se</span>
        </div>
      </div>
    </div>
  `;
}

function pageRegister() {
  const f = S.registerForm;
  return `
    <div class="login-page">
      <div class="login-box" style="max-width:480px">
        <div class="login-logo">ACT<span>IVE</span></div>
        <div class="login-sub">// criar nova conta</div>
        <div id="reg-alert" class="alert alert-err hidden"></div>
        <form class="login-form" id="form-reg" novalidate>
          <div class="field">
            <label>Nome completo</label>
            <input id="rg-name" value="${esc(f.name)}" placeholder="João Silva" required />
          </div>
          <div class="form-grid" style="gap:12px">
            <div class="field">
              <label>Email</label>
              <input type="email" id="rg-email" value="${esc(f.email)}" required />
            </div>
            <div class="field">
              <label>Senha</label>
              <input type="password" id="rg-pass" required />
            </div>
            <div class="field">
              <label>Idade</label>
              <input type="number" id="rg-age" value="${esc(f.age)}" min="10" max="100" required />
            </div>
            <div class="field">
              <label>Peso (kg)</label>
              <input type="number" step="0.1" id="rg-weight" value="${esc(f.weight)}" min="30" required />
            </div>
            <div class="field">
              <label>Altura (m)</label>
              <input type="number" step="0.01" id="rg-height" value="${esc(f.height)}" min="1" max="2.5" required />
            </div>
            <div class="field">
              <label>Nível de treino</label>
              <select id="rg-level">
                ${['BEGINNER','INTERMEDIATE','ADVANCED'].map(v =>
                  `<option value="${v}" ${f.trainingLevel===v?'selected':''}>${humanize(v)}</option>`
                ).join('')}
              </select>
            </div>
            <div class="field">
              <label>Tipo de Conta</label>
              <select id="rg-role">
                <option value="USER">Usuário (Atleta)</option>
                <option value="ADMIN">Administrador (Professor)</option>
              </select>
            </div>
          </div>
          <button class="btn btn-acc" type="submit" id="btn-rg" style="margin-top:6px">
            Criar conta
          </button>
        </form>
        <div class="login-switch">
          Já tem conta? <span class="link" id="go-login">Entrar</span>
        </div>
      </div>
    </div>
  `;
}

// Registra todos os eventos dos formulários de login e cadastro
function bindLogin() {
  document.getElementById('go-register')?.addEventListener('click', () => {
    S.page = 'register'; render();
  });

  document.getElementById('form-login')?.addEventListener('submit', async e => {
    e.preventDefault();
    const email = document.getElementById('li-email').value.trim();
    const pass  = document.getElementById('li-pass').value;
    const btn   = document.getElementById('btn-li');
    btn.disabled = true; btn.innerHTML = spinner();
    clearAlert('login-alert');
    try {
      const res = await api.login({ email, password: pass });
      if (!res) return;
      setToken(res.token);
      S.page = 'dashboard';
      await boot();
      render();
    } catch(err) {
      alert$('login-alert', loginErrorMsg(err));
      btn.disabled = false;
      btn.textContent = 'Entrar';
    }
  });

  document.getElementById('go-login')?.addEventListener('click', () => {
    S.page = 'login'; render();
  });

  document.getElementById('form-reg')?.addEventListener('submit', async e => {
    e.preventDefault();
    const btn = document.getElementById('btn-rg');
    btn.disabled = true; btn.innerHTML = spinner();
    clearAlert('reg-alert');
    const data = {
      name:          document.getElementById('rg-name').value.trim(),
      email:         document.getElementById('rg-email').value.trim(),
      password:      document.getElementById('rg-pass').value,
      age:     +     document.getElementById('rg-age').value,
      weight:  +     document.getElementById('rg-weight').value,
      height:  +     document.getElementById('rg-height').value,
      trainingLevel: document.getElementById('rg-level').value,
      role:          document.getElementById('rg-role').value,
    };
    try {
      await api.registerUser(data);
      const res = await api.login({ email: data.email, password: data.password });
      if (!res) return;
      setToken(res.token);
      S.page = 'dashboard';
      await boot();
      render();
    } catch(err) {
      alert$('reg-alert', registerErrorMsg(err));
      btn.disabled = false;
      btn.textContent = 'Criar conta';
    }
  });
}