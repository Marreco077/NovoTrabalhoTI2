// URL base da API
const API_URL = 'http://localhost:8081/api';

// Elementos do DOM
const receitaConteudo = document.getElementById('receita-conteudo');
const loading = document.querySelector('.loading');
const receitaTitulo = document.getElementById('receita-titulo');
const receitaDificuldade = document.getElementById('receita-dificuldade');
const receitaTempo = document.getElementById('receita-tempo');
const receitaPorcoes = document.getElementById('receita-porcoes');
const receitaAutor = document.getElementById('receita-autor');
const receitaImg = document.getElementById('receita-img');
const receitaDescricao = document.getElementById('receita-descricao');
const receitaIngredientes = document.getElementById('receita-ingredientes');
const receitaModoPreparo = document.getElementById('receita-modo-preparo');

// Carregar receita ao iniciar a página
document.addEventListener('DOMContentLoaded', () => {
    // Obter ID da receita da URL
    const params = new URLSearchParams(window.location.search);
    const receitaId = params.get('id');
    
    if (!receitaId) {
        mostrarErro('ID da receita não fornecido');
        return;
    }
    
    carregarReceita(receitaId);
});

// Função para carregar receita da API
async function carregarReceita(id) {
    try {
        showLoading(true);
        
        // Buscar receita pelo ID
        const response = await fetch(`${API_URL}/receitas/${id}`);
        if (!response.ok) {
            if (response.status === 404) {
                throw new Error('Receita não encontrada');
            }
            throw new Error('Erro ao carregar receita');
        }
        
        const receita = await response.json();
        exibirReceita(receita);
    } catch (error) {
        mostrarErro(error.message);
    }
}

// Função para exibir os detalhes da receita
function exibirReceita(receita) {
    // Atualizar título da página
    document.title = `${receita.titulo} | Receitas Fáceis`;
    
    // Atualizar conteúdo da página
    receitaTitulo.textContent = receita.titulo;
    receitaDificuldade.innerHTML = `<i class="icon dificuldade"></i> ${receita.dificuldade || 'N/A'}`;
    receitaTempo.innerHTML = `<i class="icon tempo"></i> ${receita.tempoPreparo || '0'} minutos`;
    receitaPorcoes.innerHTML = `<i class="icon porcoes"></i> ${receita.porcoes || '1'} ${receita.porcoes > 1 ? 'porções' : 'porção'}`;
    
    // Exibir informações do autor
    if (receita.autor) {
        receitaAutor.innerHTML = `Por: <span>${receita.autor.nome || 'Usuário'}</span>`;
    } else {
        receitaAutor.innerHTML = `Por: <span>Usuário</span>`;
    }
    
    // Exibir imagem
    console.log("URL da Imagem (receita-detalhes.js):", receita.imagemUrl); 
    if (receita.imagemUrl) {
        receitaImg.src = receita.imagemUrl;
    }
    receitaImg.alt = receita.titulo;
    
    // Exibir descrição
    receitaDescricao.textContent = receita.descricao || 'Sem descrição disponível.';
    
    // Exibir ingredientes
    if (receita.ingredientes && receita.ingredientes.length > 0) {
        let ingredientesHtml = '';
        receita.ingredientes.forEach(ingrediente => {
            ingredientesHtml += `<li>${ingrediente.nome} - ${ingrediente.quantidade}</li>`;
        });
        receitaIngredientes.innerHTML = ingredientesHtml;
    } else {
        receitaIngredientes.innerHTML = '<li>Nenhum ingrediente registrado</li>';
    }
    
    // Exibir modo de preparo
    if (receita.modoPreparo) {
        // Converter quebras de linha em parágrafos
        const paragrafos = receita.modoPreparo.split('\n')
            .filter(p => p.trim() !== '')
            .map(p => `<p>${p}</p>`)
            .join('');
        
        receitaModoPreparo.innerHTML = paragrafos;
    } else {
        receitaModoPreparo.innerHTML = '<p>Nenhum modo de preparo disponível</p>';
    }
    
    // Exibir conteúdo
    showLoading(false);
    receitaConteudo.style.display = 'block';
}

// Função para mostrar mensagem de erro
function mostrarErro(mensagem) {
    loading.innerHTML = `<div class="erro">
        <p>${mensagem}</p>
        <a href="receitas.html" class="btn">Voltar para Receitas</a>
    </div>`;
}

// Função para mostrar/ocultar indicador de carregamento
function showLoading(show) {
    loading.style.display = show ? 'block' : 'none';
} 