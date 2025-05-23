// Configurações da API
const API_BASE_URL = 'http://localhost:8081/api';

// Variável global para armazenar informações do usuário
window.usuarioLogado = null;

// Carregamento inicial
document.addEventListener('DOMContentLoaded', () => {
    // Verifica se o usuário está logado (recuperando do localStorage)
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
    const cadastroNavItem = document.getElementById('cadastroNavItem'); 
    
    if (loginBtn) { 
        if (usuario) {
            loginBtn.textContent = usuario.nome;
            loginBtn.href = 'perfil.html';
            
            if (cadastroNavItem) {
                cadastroNavItem.style.display = 'none';
            }

            const nav = document.querySelector('nav ul');
            const existingLogout = nav.querySelector('.logout-item');
            if (!existingLogout) {
                const logoutItem = document.createElement('li');
                logoutItem.className = 'logout-item';
                const logoutLink = document.createElement('a');
                logoutLink.href = '#';
                logoutLink.textContent = 'Sair';
                logoutLink.addEventListener('click', (e) => {
                    e.preventDefault();
                    logout();
                });
                logoutItem.appendChild(logoutLink);
                nav.appendChild(logoutItem);
            }
        } else {
            loginBtn.textContent = 'Login';
            loginBtn.href = 'login.html';
            
            if (cadastroNavItem) {
                cadastroNavItem.style.display = 'list-item'; // Ou o display original do <li>
            }

            const logoutItem = document.querySelector('.logout-item');
            if (logoutItem) {
                logoutItem.remove();
            }
        }
    } else {
        // Se o loginBtn não existir na página, ainda assim garantir que o cadastroNavItem seja exibido
        // (caso seja uma página que não tem o botão de login mas tem o de cadastro)
        if (cadastroNavItem) {
            cadastroNavItem.style.display = 'list-item';
        }
    }
}

function getUsuarioLogado() {
    // Tenta obter do localStorage
    try {
        const usuarioJSON = localStorage.getItem('usuarioLogado');
        if (usuarioJSON) {
            return JSON.parse(usuarioJSON);
        }
    } catch (e) {
        console.error("Erro ao obter usuário do localStorage:", e);
    }
    return null;
}

function setUsuarioLogado(usuario) {
    if (usuario) {
        localStorage.setItem('usuarioLogado', JSON.stringify(usuario));
        console.log("Usuário armazenado no localStorage:", usuario);
    } else {
        localStorage.removeItem('usuarioLogado');
    }
}

function logout() {
    localStorage.removeItem('usuarioLogado');
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
function criarCardReceita(receita, contexto = 'default') {
    const card = document.createElement('div');
    card.className = 'receita-card';
    card.dataset.receitaId = receita.id;

    // Event listener principal do card para ver detalhes
    const linkDetalhes = document.createElement('a');
    linkDetalhes.href = `receita-detalhes.html?id=${receita.id}`;
    linkDetalhes.className = 'receita-card-link';

    const img = document.createElement('img');
    img.className = 'receita-img';
    img.src = receita.imagemUrl || 'img/placeholder.jpg';
    img.alt = receita.titulo;
    linkDetalhes.appendChild(img);

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
    linkDetalhes.appendChild(info);
    card.appendChild(linkDetalhes);

    // Se o contexto for 'perfil', adiciona botões de ação (Excluir, Editar)
    if (contexto === 'perfil') {
        const acoesDiv = document.createElement('div');
        acoesDiv.className = 'receita-card-acoes';

        // Botão Excluir
        const btnExcluir = document.createElement('button');
        btnExcluir.textContent = 'Excluir';
        btnExcluir.className = 'btn btn-sm btn-danger btn-excluir-receita';
        btnExcluir.dataset.receitaId = receita.id;
        acoesDiv.appendChild(btnExcluir);

        // Placeholder para Botão Editar (será implementado depois)
        const btnEditar = document.createElement('button');
        btnEditar.textContent = 'Editar';
        btnEditar.className = 'btn btn-sm btn-warning btn-editar-receita';
        btnEditar.dataset.receitaId = receita.id;
        btnEditar.style.marginLeft = '5px';
        acoesDiv.appendChild(btnEditar);

        card.appendChild(acoesDiv);
    }

    return card;
}

function truncateText(text, maxLength) {
    if (text.length > maxLength) {
        return text.substring(0, maxLength) + '...';
    }
    return text;
} 