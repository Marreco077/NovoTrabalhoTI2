// Configurações da API
const API_BASE_URL = 'http://localhost:8081/api';

// Carregamento inicial
document.addEventListener('DOMContentLoaded', () => {
    checkLoginStatus();
    
    // Carrega as receitas em destaque na página inicial (se a seção existir)
    const receitasDestaque = document.getElementById('receitas-destaque');
    if (receitasDestaque) {
        carregarReceitasDestaque();
    }
});

// Funções para manipulação de autenticação
function checkLoginStatus() {
    const usuario = getUsuarioLogado();
    updateNav(usuario);
}

function getUsuarioLogado() {
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
    checkLoginStatus(); // Atualiza a navegação após login/logout
}

function logout() {
    localStorage.removeItem('usuarioLogado');
    // Garante que todos os caches relacionados ao usuário sejam limpos se necessário
    // Ex: limpar outros dados de sessão no localStorage
    
    // Redireciona e força a atualização da navegação
    window.location.href = 'index.html'; 
    // A chamada checkLoginStatus() no DOMContentLoaded da nova página cuidará da UI.
}

function updateNav(usuario) {
    const navLinks = document.getElementById('nav-links');
    if (!navLinks) return;

    const perfilNavItem = document.getElementById('perfilNavItem');
    const loginNavItem = document.getElementById('loginNavItem');
    const cadastroNavItem = document.getElementById('cadastroNavItem');
    const novaReceitaNavItem = document.getElementById('novaReceitaNavItem');
    
    // Remove o item "Sair" existente, se houver, para evitar duplicatas
    const existingLogoutItem = navLinks.querySelector('li a[href="#logout"]');
    if (existingLogoutItem) {
        existingLogoutItem.parentElement.remove();
    }

    if (usuario) {
        // Usuário logado
        if (perfilNavItem) {
            perfilNavItem.style.display = 'list-item';
            const perfilLink = perfilNavItem.querySelector('a');
            if (perfilLink) perfilLink.textContent = usuario.nome || 'Perfil'; // Mostra nome ou 'Perfil'
        }
        if (loginNavItem) loginNavItem.style.display = 'none';
        if (cadastroNavItem) cadastroNavItem.style.display = 'none';
        if (novaReceitaNavItem) novaReceitaNavItem.style.display = 'list-item';

        // Adicionar item "Sair"
        const logoutLi = document.createElement('li');
        const logoutLink = document.createElement('a');
        logoutLink.href = '#logout'; // Usar um href distintivo para fácil remoção
        logoutLink.textContent = 'Sair';
        logoutLink.addEventListener('click', (e) => {
            e.preventDefault();
            logout();
        });
        logoutLi.appendChild(logoutLink);
        
        // Insere "Sair" antes do botão de tema, se existir, ou no final
        const themeToggleButtonLi = navLinks.querySelector('li button#theme-toggle-button')?.parentElement;
        if (themeToggleButtonLi) {
            navLinks.insertBefore(logoutLi, themeToggleButtonLi);
        } else {
            navLinks.appendChild(logoutLi);
        }

    } else {
        // Usuário não logado
        if (perfilNavItem) perfilNavItem.style.display = 'none';
        if (loginNavItem) loginNavItem.style.display = 'list-item';
        if (cadastroNavItem) cadastroNavItem.style.display = 'list-item';
        if (novaReceitaNavItem) novaReceitaNavItem.style.display = 'none'; // Esconde Nova Receita se não logado
    }
}

// Funções para carregar dados da API
async function carregarReceitasDestaque() {
    const receitasGrid = document.getElementById('receitas-destaque');
    if (!receitasGrid) return; // Sai se o elemento não existir
    
    receitasGrid.innerHTML = '<p class="loading">Carregando receitas...</p>'; // Mostra carregando

    try {
        const response = await fetch(`${API_BASE_URL}/receitas`);
        if (!response.ok) {
            throw new Error('Falha ao carregar receitas');
        }
        const receitas = await response.json();
        
        receitasGrid.innerHTML = ''; // Limpa o container
        
        const receitasLimitadas = receitas.slice(0, 6);
        
        if (receitasLimitadas.length === 0) {
            receitasGrid.innerHTML = '<p class="no-results">Nenhuma receita disponível no momento.</p>';
            return;
        }
        
        receitasLimitadas.forEach(receita => {
            receitasGrid.appendChild(criarCardReceita(receita));
        });
    } catch (error) {
        console.error('Erro ao carregar receitas em destaque:', error);
        receitasGrid.innerHTML = '<p class="error">Erro ao carregar receitas. Tente novamente mais tarde.</p>';
    }
}

// Funções auxiliares para criar elementos HTML
function criarCardReceita(receita, contexto = 'default') {
    const card = document.createElement('div');
    card.className = 'receita-card';
    card.dataset.receitaId = receita.id;

    // Link envolvendo imagem e info para navegação
    const linkDetalhes = document.createElement('a');
    linkDetalhes.href = `receita-detalhes.html?id=${receita.id}`;
    linkDetalhes.className = 'receita-display-area'; // Adicionada classe para controle de layout

    const imgContainer = document.createElement('div');
    // imgContainer não precisa de classe específica se for apenas um wrapper simples
    // para a imagem dentro do linkDetalhes.
    const img = document.createElement('img');
    img.className = 'receita-img'; // Classe está na imagem
    img.src = receita.imagemUrl || 'img/receita-padrao.jpg'; // Usar uma imagem padrão local
    img.alt = receita.titulo;
    // linkDetalhes.appendChild(img); // Imagem dentro do link

    // Estrutura do card:
    // <div class="receita-card">
    //   <a href="detalhes...">  <-- Link para detalhes
    //     <img class="receita-img" ...>
    //     <div class="receita-info">...</div>
    //   </a>
    //   <div class="receita-card-acoes"> BOTOES (se perfil) </div>
    // </div>

    imgContainer.appendChild(img); // Imagem dentro de seu container (que não é o link direto)
    linkDetalhes.appendChild(imgContainer);


    const info = document.createElement('div');
    info.className = 'receita-info';

    const titulo = document.createElement('h3');
    titulo.textContent = receita.titulo;
    info.appendChild(titulo);
    
    // Adiciona o link ao card
    // card.appendChild(linkDetalhes); // Link é o principal wrapper para conteúdo clicável

    // Descrição (opcional, pode ser removida dos cards para simplificar)
    // const desc = document.createElement('p');
    // desc.textContent = receita.descricao ? truncateText(receita.descricao, 70) : '';
    // if (desc.textContent) info.appendChild(desc);


    const meta = document.createElement('div');
    meta.className = 'receita-meta';

    const tempo = document.createElement('span');
    // Ícones podem ser adicionados com FontAwesome ou SVGs se configurados
    tempo.textContent = `${receita.tempoPreparo || '?'} min`;
    meta.appendChild(tempo);

    const dificuldade = document.createElement('span');
    dificuldade.textContent = receita.dificuldade || 'N/A';
    meta.appendChild(dificuldade);
    
    info.appendChild(meta);
    linkDetalhes.appendChild(info); // Adiciona info ao link
    card.appendChild(linkDetalhes); // Adiciona o link (com imagem e info) ao card

    // Se o contexto for 'perfil', adiciona botões de ação (Excluir, Editar)
    // Esses botões não devem estar dentro do linkDetalhes para não acionar a navegação.
    if (contexto === 'perfil') {
        const acoesDiv = document.createElement('div');
        acoesDiv.className = 'receita-card-acoes';

        const btnEditar = document.createElement('button');
        btnEditar.textContent = 'Editar';
        btnEditar.className = 'btn btn-sm btn-warning btn-editar-receita';
        btnEditar.dataset.receitaId = receita.id;
        btnEditar.addEventListener('click', (e) => {
            e.stopPropagation(); 
            // A navegação para edição já está aqui e parece funcionar para ir à página certa
            window.location.href = `nova-receita.html?id=${receita.id}`; 
        });
        acoesDiv.appendChild(btnEditar);
        
        const btnExcluir = document.createElement('button');
        btnExcluir.textContent = 'Excluir';
        btnExcluir.className = 'btn btn-sm btn-danger btn-excluir-receita';
        btnExcluir.dataset.receitaId = receita.id;
        btnExcluir.style.marginLeft = '5px';
        btnExcluir.addEventListener('click', (e) => {
            e.stopPropagation(); 
            // Chama uma função global que será definida em perfil.js
            if (typeof window.handleExcluirReceitaPerfil === 'function') {
                window.handleExcluirReceitaPerfil(receita.id);
            } else {
                console.error("Função handleExcluirReceitaPerfil não encontrada.");
                alert("Erro ao tentar configurar a ação de exclusão.");
            }
        });
        acoesDiv.appendChild(btnExcluir);
        card.appendChild(acoesDiv);
    }

    return card;
}

function truncateText(text, maxLength) {
    if (!text) return '';
    if (text.length > maxLength) {
        return text.substring(0, maxLength) + '...';
    }
    return text;
} 