// Configurações da API
const API_BASE_URL = 'http://localhost:8080/api';

// Carregamento inicial
document.addEventListener('DOMContentLoaded', () => {
    // Verifica se o usuário está logado
    verificarAutenticacao();
    
    // Carrega as receitas em destaque na página inicial
    const receitasDestaque = document.getElementById('receitas-destaque');
    if (receitasDestaque) {
        carregarReceitasDestaque();
    }
});

// Funções para manipulação de autenticação
function verificarAutenticacao() {
    const usuario = getUsuarioLogado();
    const loginBtn = document.getElementById('loginBtn');
    
    if (loginBtn) {
        if (usuario) {
            loginBtn.textContent = 'Minha Conta';
            loginBtn.href = 'perfil.html';
            
            // Adicionar botão de logout ao menu
            const nav = document.querySelector('nav ul');
            const logoutItem = document.createElement('li');
            const logoutLink = document.createElement('a');
            logoutLink.href = '#';
            logoutLink.textContent = 'Sair';
            logoutLink.addEventListener('click', (e) => {
                e.preventDefault();
                logout();
            });
            logoutItem.appendChild(logoutLink);
            nav.appendChild(logoutItem);
        } else {
            loginBtn.textContent = 'Login';
            loginBtn.href = 'login.html';
        }
    }
}

function getUsuarioLogado() {
    const usuarioJson = localStorage.getItem('usuario');
    return usuarioJson ? JSON.parse(usuarioJson) : null;
}

function setUsuarioLogado(usuario) {
    localStorage.setItem('usuario', JSON.stringify(usuario));
}

function logout() {
    localStorage.removeItem('usuario');
    window.location.href = 'index.html';
}

// Funções para carregar dados da API
async function carregarReceitasDestaque() {
    const receitasGrid = document.getElementById('receitas-destaque');
    
    try {
        const response = await fetch(`${API_BASE_URL}/receitas`);
        
        if (!response.ok) {
            throw new Error('Falha ao carregar receitas');
        }
        
        const receitas = await response.json();
        
        // Limpa o container
        receitasGrid.innerHTML = '';
        
        // Exibe apenas as 6 primeiras receitas
        const receitasLimitadas = receitas.slice(0, 6);
        
        if (receitasLimitadas.length === 0) {
            receitasGrid.innerHTML = '<p class="no-results">Nenhuma receita disponível no momento.</p>';
            return;
        }
        
        // Adiciona as receitas ao grid
        receitasLimitadas.forEach(receita => {
            receitasGrid.appendChild(criarCardReceita(receita));
        });
    } catch (error) {
        console.error('Erro:', error);
        receitasGrid.innerHTML = '<p class="error">Erro ao carregar receitas. Tente novamente mais tarde.</p>';
    }
}

// Funções auxiliares para criar elementos HTML
function criarCardReceita(receita) {
    const card = document.createElement('div');
    card.className = 'receita-card';
    card.addEventListener('click', () => {
        window.location.href = `receita.html?id=${receita.id}`;
    });
    
    const img = document.createElement('img');
    img.className = 'receita-img';
    img.src = receita.imagemUrl || 'img/placeholder.jpg';
    img.alt = receita.titulo;
    card.appendChild(img);
    
    const info = document.createElement('div');
    info.className = 'receita-info';
    
    const titulo = document.createElement('h3');
    titulo.textContent = receita.titulo;
    info.appendChild(titulo);
    
    const desc = document.createElement('p');
    desc.textContent = receita.descricao ? truncateText(receita.descricao, 80) : 'Sem descrição disponível';
    info.appendChild(desc);
    
    const meta = document.createElement('div');
    meta.className = 'receita-meta';
    
    const tempo = document.createElement('span');
    tempo.innerHTML = `<i class="fa fa-clock-o"></i> ${receita.tempoPreparo} min`;
    meta.appendChild(tempo);
    
    const dificuldade = document.createElement('span');
    dificuldade.textContent = receita.dificuldade;
    meta.appendChild(dificuldade);
    
    info.appendChild(meta);
    card.appendChild(info);
    
    return card;
}

function truncateText(text, maxLength) {
    if (text.length > maxLength) {
        return text.substring(0, maxLength) + '...';
    }
    return text;
} 