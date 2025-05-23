document.addEventListener('DOMContentLoaded', () => {
    const perfilNome = document.getElementById('perfil-nome');
    const perfilEmail = document.getElementById('perfil-email');
    const btnLogoutPerfil = document.getElementById('btn-logout-perfil');

    // Tenta carregar dados do usuário do localStorage (função de main.js)
    const usuario = getUsuarioLogado(); // Esta função deve estar disponível de main.js

    if (usuario) {
        if (perfilNome) {
            perfilNome.textContent = usuario.nome || 'Não informado';
        }
        if (perfilEmail) {
            perfilEmail.textContent = usuario.email || 'Não informado';
        }
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
        // Poderia redirecionar: window.location.href = 'login.html';
        // Mas o menu já terá "Login" e o botão de Sair também fará o logout.
    }

    if (btnLogoutPerfil) {
        btnLogoutPerfil.addEventListener('click', () => {
            logout(); // Esta função deve estar disponível de main.js
        });
    }
});

// Nota: As funções getUsuarioLogado() e logout() são esperadas do arquivo main.js,
// que deve ser carregado antes deste script na página perfil.html.
// Se elas não estiverem disponíveis globalmente, precisarão ser duplicadas aqui
// ou movidas para um arquivo utilitário. 