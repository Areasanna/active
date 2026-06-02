// Ao iniciar a aplicação se já existe um token JWT salvo (S.token):
// Decodifica informações desse token, salva no estado global, define a página inicial
// como dashboard, executa tarefas iniciais com boot(). Senão, redireciona para tela
// de login. E executa render() para desenhar a interface com base na página definida.

(async () => {
  if (S.token) {
    const p = decodeJwt(S.token);
    S.user = { email: p.sub, role: p.role || 'USER', name: p.name || p.sub };
    S.page = 'dashboard';
    await boot();
  } else {
    S.page = 'login';
  }
  render();
})();