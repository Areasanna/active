// Define e centraliza o estado global da aplicação web em um só objeto (S)

const S = {
  page: 'login',
  token: localStorage.getItem('active_token') || null,
  user: null,

  // caches de referência (carregados no boot)
  muscles: [],
  equipments: [],
  exercises: [],

  stats: { exercises: 0, plans: 0, sessions: 0, prs: 0 },
  loading: false,

  loginForm:    { email: '', password: '' },
  registerForm: { name:'', email:'', password:'', age:'', weight:'', height:'', trainingLevel:'BEGINNER' },
};

// Centraliza e padroniza todas as alterações do estado para garantir que a
// interface sempre reflita o que está em S
function setState(patch) {
  Object.assign(S, patch);
  render();
}

// Decodifica o payload de um JWT
function decodeJwt(token) {
  try {
    const payload = token.split('.')[1];
    return JSON.parse(atob(payload.replace(/-/g,'+').replace(/_/g,'/')));
  } catch { return {}; }
}

// Salva o token JWT no localStorage, para manter sessão ao recarregar
function setToken(token) {
  localStorage.setItem('active_token', token);
  S.token = token;
  const p = decodeJwt(token);
  S.user = { email: p.sub, role: p.role || 'USER', name: p.name || p.sub };
}

// Remove o token do localStorage
function logout() {
  localStorage.removeItem('active_token');
  Object.assign(S, { token: null, user: null, page: 'login' });
  render();
}