// Cada função recebe um ApiError e retorna uma string amigável em português.

function loginErrorMsg(err) {
  if (err.type === 'network') return err.message;
  if (err.status === 401)     return 'Email ou senha incorretos. Tente novamente.';
  if (err.status === 403)     return 'Conta desativada. Entre em contato com o suporte.';
  if (err.type === 'server')  return 'O servidor está com problemas. Tente em alguns instantes.';
  return 'Não foi possível fazer login. Tente novamente.';
}

function registerErrorMsg(err) {
  if (err.type === 'network')    return err.message;
  if (err.type === 'validation') return err.message;
  if (err.status === 409)        return 'Este email já está cadastrado. Tente fazer login.';
  if (err.type === 'server')     return 'Erro ao criar conta. Tente novamente em breve.';
  return 'Não foi possível criar a conta. Verifique os dados e tente novamente.';
}

function saveErrorMsg(err, entity = 'item') {
  if (err.type === 'network')    return err.message;
  if (err.type === 'validation') return err.message;
  if (err.status === 409)        return `Já existe um(a) ${entity} com esses dados.`;
  if (err.status === 403)        return 'Você não tem permissão para salvar este item.';
  if (err.type === 'server')     return `Erro ao salvar ${entity}. Tente novamente.`;
  return `Não foi possível salvar o(a) ${entity}. Verifique os dados.`;
}

function deleteErrorMsg(err, entity = 'item') {
  if (err.type === 'network') return err.message;
  if (err.status === 403)     return 'Você não tem permissão para excluir este item.';
  if (err.status === 404)     return `Este(a) ${entity} não foi encontrado(a).`;
  return `Não foi possível excluir o(a) ${entity}. Tente novamente.`;
}

function loadErrorMsg(err, entity = 'dados') {
  if (err.type === 'network') return err.message;
  if (err.type === 'server')  return `Erro ao carregar ${entity}. Tente recarregar a página.`;
  return `Não foi possível carregar os ${entity}.`;
}