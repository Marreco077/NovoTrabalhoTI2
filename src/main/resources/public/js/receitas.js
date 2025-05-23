// URL base da API
const API_URL = 'http://localhost:8081/api';

// Elementos do DOM
const receitasLista = document.getElementById('receitas-lista');
const categoriaSelect = document.getElementById('categoria');
const dificuldadeSelect = document.getElementById('dificuldade');
const btnFiltrar = document.getElementById('btn-filtrar');

// Estado da aplicação
let receitas = [];
let filtros = {
    categoria: '',
    dificuldade: ''
};

// Carregar todas as receitas ao iniciar a página
document.addEventListener('DOMContentLoaded', () => {
    carregarReceitas();
    
    // Adicionar eventos aos filtros
    btnFiltrar.addEventListener('click', aplicarFiltros);
});

// Função para carregar receitas da API
async function carregarReceitas() {
    try {
        showLoading(true);
        
        const response = await fetch(`${API_URL}/receitas`);
        if (!response.ok) {
            throw new Error('Erro ao carregar receitas');
        }
        
        receitas = await response.json();
        exibirReceitas(receitas);
    } catch (error) {
        console.error('Erro:', error);
        receitasLista.innerHTML = `<p class="erro">Erro ao carregar receitas: ${error.message}</p>`;
    } finally {
        showLoading(false);
    }
}

// Função para exibir as receitas na interface
function exibirReceitas(receitas) {
    if (receitas.length === 0) {
        receitasLista.innerHTML = '<p class="info">Nenhuma receita encontrada.</p>';
        return;
    }
    
    let html = '';
    
    receitas.forEach(receita => {
        console.log("URL da Imagem (receitas.js):", receita.imagemUrl); 
        const imagemUrl = receita.imagemUrl || 'img/receita-padrao.jpg';
        
        html += `
            <div class="receita-card" data-id="${receita.id}">
                <div class="receita-img">
                    <img src="${imagemUrl}" alt="${receita.titulo}">
                </div>
                <div class="receita-info">
                    <h3>${receita.titulo}</h3>
                    <p>${receita.descricao || 'Sem descrição'}</p>
                    <div class="receita-meta">
                        <span class="dificuldade">${receita.dificuldade || 'N/A'}</span>
                        <span class="tempo">${receita.tempoPreparo || 0} min</span>
                    </div>
                    <a href="receita-detalhes.html?id=${receita.id}" class="btn">Ver Receita</a>
                </div>
            </div>
        `;
    });
    
    receitasLista.innerHTML = html;
    
    // Adicionar evento de clique aos cards de receita
    document.querySelectorAll('.receita-card').forEach(card => {
        card.addEventListener('click', () => {
            window.location.href = `receita-detalhes.html?id=${card.dataset.id}`;
        });
    });
}

// Função para aplicar filtros
function aplicarFiltros() {
    filtros.categoria = categoriaSelect.value;
    filtros.dificuldade = dificuldadeSelect.value;
    
    const receitasFiltradas = receitas.filter(receita => {
        // Filtrar por categoria
        if (filtros.categoria && receita.categoriaId != filtros.categoria) {
            return false;
        }
        
        // Filtrar por dificuldade
        if (filtros.dificuldade && receita.dificuldade !== filtros.dificuldade) {
            return false;
        }
        
        return true;
    });
    
    exibirReceitas(receitasFiltradas);
}

// Função para mostrar/ocultar indicador de carregamento
function showLoading(show) {
    const loadingElement = document.querySelector('.loading');
    
    if (show) {
        if (loadingElement) {
            loadingElement.style.display = 'block';
        } else {
            const loadingDiv = document.createElement('div');
            loadingDiv.className = 'loading';
            loadingDiv.textContent = 'Carregando receitas...';
            receitasLista.innerHTML = '';
            receitasLista.appendChild(loadingDiv);
        }
    } else if (loadingElement) {
        loadingElement.style.display = 'none';
    }
} 