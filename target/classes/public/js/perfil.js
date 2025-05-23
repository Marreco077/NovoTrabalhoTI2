const API_BASE_URL_PERFIL = 'http://localhost:8081/api'; // Consistente com outros arquivos

document.addEventListener('DOMContentLoaded', () => {
    const perfilNome = document.getElementById('perfil-nome');
    const perfilEmail = document.getElementById('perfil-email');
    const btnLogoutPerfil = document.getElementById('btn-logout-perfil');
    const minhasReceitasGrid = document.getElementById('minhas-receitas-grid');
    const mensagemMinhasReceitas = document.getElementById('mensagem-minhas-receitas');

    // Tenta carregar dados do usuário do localStorage (função de main.js)
    const usuario = getUsuarioLogado(); // Esta função deve estar disponível de main.js

    if (usuario) {
        if (perfilNome) {
            perfilNome.textContent = usuario.nome || 'Não informado';
        }
        if (perfilEmail) {
            perfilEmail.textContent = usuario.email || 'Não informado';
        }
        // Carregar as receitas do usuário
        carregarMinhasReceitas(usuario.id);
    } else {
        // Se não houver usuário logado, redireciona para o login
        // ou mostra uma mensagem, dependendo da preferência.
        // Por agora, vamos apenas limpar os campos e deixar o botão de logout funcional
        // (que também redirecionará).
        if (perfilNome) {
            perfilNome.textContent = 'Nenhum usuário logado.';
        }
        if (perfilEmail) {
            perfilEmail.textContent = '';
        }
        if (mensagemMinhasReceitas) {
            mensagemMinhasReceitas.textContent = 'Você precisa estar logado para ver suas receitas.';
            mensagemMinhasReceitas.className = 'info';
        }
        // Ocultar o grid se não houver usuário
        if (minhasReceitasGrid) {
             minhasReceitasGrid.style.display = 'none'; 
        }
        // Poderia redirecionar: window.location.href = 'login.html';
        // Mas o menu já terá "Login" e o botão de Sair também fará o logout.
    }

    if (btnLogoutPerfil) {
        btnLogoutPerfil.addEventListener('click', () => {
            logout(); // Esta função deve estar disponível de main.js
        });
    }

    async function carregarMinhasReceitas(usuarioId) {
        if (!minhasReceitasGrid || !mensagemMinhasReceitas) return;

        mensagemMinhasReceitas.textContent = 'Carregando suas receitas...';
        mensagemMinhasReceitas.style.display = 'block';
        minhasReceitasGrid.innerHTML = ''; // Limpa o grid antes de adicionar novos cards

        try {
            const response = await fetch(`${API_BASE_URL_PERFIL}/usuarios/${usuarioId}/receitas`);
            if (!response.ok) {
                throw new Error(`Falha ao carregar suas receitas: ${response.statusText}`);
            }
            const receitas = await response.json();

            if (receitas.length === 0) {
                mensagemMinhasReceitas.textContent = 'Você ainda não cadastrou nenhuma receita.';
                mensagemMinhasReceitas.className = 'info';
            } else {
                mensagemMinhasReceitas.style.display = 'none'; // Oculta a mensagem de carregamento/info
                receitas.forEach(receita => {
                    if (typeof criarCardReceita === 'function') {
                        minhasReceitasGrid.appendChild(criarCardReceita(receita));
                    } else {
                        console.warn("Função criarCardReceita não encontrada globalmente. Cards de 'Minhas Receitas' não serão renderizados corretamente.");
                        const p = document.createElement('p');
                        p.textContent = receita.titulo;
                        minhasReceitasGrid.appendChild(p);
                    }
                });
            }
        } catch (error) {
            console.error('Erro ao carregar minhas receitas:', error);
            mensagemMinhasReceitas.textContent = 'Erro ao carregar suas receitas. Tente novamente mais tarde.';
            mensagemMinhasReceitas.className = 'error';
        }
    }
});

// Nota: As funções getUsuarioLogado(), logout() e criarCardReceita() são esperadas do arquivo main.js,
// que deve ser carregado antes deste script na página perfil.html.
// Se elas não estiverem disponíveis globalmente, precisarão ser duplicadas aqui
// ou movidas para um arquivo utilitário. 