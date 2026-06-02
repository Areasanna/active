// Serve como identificador para a URL base da API
  const API = 'http://localhost:8080';

  // ApiError - Customização de erros HTTP
  // lança erros mais detalhados do backend e facilita tratamento no frontend
  class ApiError extends Error {
    constructor(message, status = 0, type = 'unknown') {
      super(message);
      this.status = status;
      this.type   = type;
      this.name   = 'ApiError';
    }
  }
// Facilita tratamento diferenciado conforme o tipo de erro retornado pela API
  function classifyStatus(status) {
    if (status === 401 || status === 403) return 'auth';
    if (status === 400 || status === 422) return 'validation';
    if (status >= 500)                    return 'server';
    return 'client';
  }
// Mostra mensagens mais compreensíveis ao usuário final
  function friendlyHttpError(status, body) {
    try {
      const json = JSON.parse(body);
      const raw  = json.message || json.error || json.detail || '';
      if (raw) return cleanSpringMessage(raw);
    } catch {}

    const map = {
      400: 'Os dados enviados são inválidos. Revise o formulário.',
      401: 'Sessão expirada. Faça login novamente.',
      403: 'Você não tem permissão para realizar esta ação.',
      404: 'O item solicitado não foi encontrado.',
      409: 'Já existe um registro com esses dados.',
      422: 'Os dados enviados não puderam ser processados.',
      429: 'Muitas tentativas seguidas. Aguarde alguns instantes.',
      500: 'Erro interno no servidor. Tente novamente em breve.',
      503: 'Serviço temporariamente indisponível.',
    };
    return map[status] || `Ocorreu um erro inesperado (código ${status}).`;
  }
// Limpa mensagens do backend Spring, deixa apenas a parte relevante da mensagem
  function cleanSpringMessage(msg) {
    return msg
      .replace(/^Validation failed for.*?:\s*/i, '')
      .replace(/^Erro de validação:\s*/i, '')
      .replace(/\[.*?\]/g, '')
      .trim();
  }

  // Função central de requisição HTTP
  async function request(method, path, body, params) {
    if (!navigator.onLine) {
      throw new ApiError(
        'Sem conexão com a internet. Verifique sua rede e tente novamente.',
        0, 'network'
      );
    }

    let url = API + path;
    if (params) {
      const qs = new URLSearchParams(
        Object.fromEntries(Object.entries(params).filter(([,v]) => v != null && v !== ''))
      ).toString();
      if (qs) url += '?' + qs;
    }

    const controller = new AbortController();
    const timeoutId  = setTimeout(() => controller.abort(), 15000);

    try {
      const res = await fetch(url, {
        method,
        signal: controller.signal,
        headers: {
          'Content-Type': 'application/json',
          ...(S.token ? { Authorization: 'Bearer ' + S.token } : {}),
        },
        ...(body ? { body: JSON.stringify(body) } : {}),
      });
      clearTimeout(timeoutId);

      if (!res.ok) {
        const text = await res.text().catch(() => '');
        if (res.status === 401 && S.token) {
          toast('Sua sessão expirou. Faça login novamente.', 'info', 6000);
          logout();
          return null;
        }
        throw new ApiError(
          friendlyHttpError(res.status, text),
          res.status,
          classifyStatus(res.status)
        );
      }

      if (res.status === 204) return null;
      return res.json();

    } catch (err) {
      clearTimeout(timeoutId);
      if (err.name === 'AbortError') {
        throw new ApiError('O servidor demorou demais para responder. Tente novamente.', 0, 'network');
      }
      if (err instanceof ApiError) throw err;
      throw new ApiError('Não foi possível conectar ao servidor. Verifique sua rede.', 0, 'network');
    }
  }

  // Endpoints centralizados, objeto chamado api com métodos para cada
  // endpoint REST principal da aplicação
  const api = {
    login:           (d)    => request('POST',   '/auth/login', d),
    registerUser:    (d)    => request('POST',   '/users', d),
    listUsers:       (p)    => request('GET',    '/users', null, p),
    deleteUser:      (id)   => request('DELETE', `/users/${id}`),
    listExercises:   (p)    => request('GET',    '/exercises', null, p),
    getExercise:     (id)   => request('GET',    `/exercises/${id}`),
    createExercise:  (d)    => request('POST',   '/exercises', d),
    updateExercise:  (id,d) => request('PUT',    `/exercises/${id}`, d),
    deleteExercise:  (id)   => request('DELETE', `/exercises/${id}`),
    getPR:           (id)   => request('GET',    `/exercises/${id}/personal-record`),
    listMuscles:     (p)    => request('GET',    '/muscles', null, p),
    createMuscle:    (d)    => request('POST',   '/muscles', d),
    listEquipments:  (p)    => request('GET',    '/equipments', null, p),
    createEquipment: (d)    => request('POST',   '/equipments', d),
    listPlans:       (p)    => request('GET',    '/training-plans', null, p),
    getPlan:         (id)   => request('GET',    `/training-plans/${id}`),
    createPlan:      (d)    => request('POST',   '/training-plans', d),
    listSessions:    (p)    => request('GET',    '/workout-sessions', null, p),
    getSession:      (id)   => request('GET',    `/workout-sessions/${id}`),
    createSession:   (d)    => request('POST',   '/workout-sessions', d),
  };