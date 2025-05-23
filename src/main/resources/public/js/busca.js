// Configurações da API (igual ao main.js, mas pode ser redundante se main.js for sempre carregado antes)
const API_BASE_URL_BUSCA = 'http://localhost:8081/api'; // Garanta que esta URL esteja correta

document.addEventListener('DOMContentLoaded', () => {
    const termoBuscaInput = document.getElementById('termo-busca');
    const categoriaSelect = document.getElementById('categoria');
    const dificuldadeSelect = document.getElementById('dificuldade');
    const btnBuscarFiltrar = document.getElementById('btn-buscar-filtrar');
    const receitasGrid = document.getElementById('receitas-lista-busca');
    const mensagemBusca = document.getElementById('mensagem-busca');
    const loadingIndicator = document.querySelector('#receitas-lista-busca .loading');

    let todasAsReceitas = []; // Cache para todas as receitas carregadas

    // Função para carregar todas as receitas uma vez
    async function carregarTodasAsReceitas() {
        if (loadingIndicator) loadingIndicator.style.display = 'block';
        if (mensagemBusca) mensagemBusca.style.display = 'none';
        receitasGrid.innerHTML = ''; // Limpa resultados anteriores

        try {
            const response = await fetch(`${API_BASE_URL_BUSCA}/receitas`);
            if (!response.ok) {
                throw new Error(`Falha ao carregar receitas: ${response.statusText}`);
            }
            todasAsReceitas = await response.json();
            // Inicialmente, não exibe nada até que uma busca seja feita
            if (mensagemBusca) {
                mensagemBusca.textContent = 'Digite um termo ou selecione filtros para iniciar a busca.';
                mensagemBusca.style.display = 'block';
            }
            aplicarFiltrosEBusca(); // Aplicar filtros caso já haja algo nos campos

        } catch (error) {
            console.error('Erro ao carregar todas as receitas:', error);
            if (mensagemBusca) {
                mensagemBusca.textContent = 'Erro ao carregar receitas. Tente novamente mais tarde.';
                mensagemBusca.className = 'error';
                mensagemBusca.style.display = 'block';
            }
        } finally {
            if (loadingIndicator) loadingIndicator.style.display = 'none';
        }
    }

    // Função para aplicar filtros e o termo de busca
    function aplicarFiltrosEBusca() {
        const termo = termoBuscaInput.value.toLowerCase().trim();
        const categoriaId = categoriaSelect.value;
        const dificuldade = dificuldadeSelect.value;

        if (loadingIndicator) loadingIndicator.style.display = 'block';
        if (mensagemBusca) mensagemBusca.style.display = 'none';
        receitasGrid.innerHTML = ''; // Limpa resultados anteriores

        let receitasFiltradas = todasAsReceitas;

        // Filtra por termo de busca (título)
        if (termo) {
            receitasFiltradas = receitasFiltradas.filter(receita => 
                receita.titulo.toLowerCase().includes(termo)
            );
        }

        // Filtra por categoria
        if (categoriaId) {
            receitasFiltradas = receitasFiltradas.filter(receita => receita.categoriaId == categoriaId);
        }

        // Filtra por dificuldade
        if (dificuldade) {
            receitasFiltradas = receitasFiltradas.filter(receita => receita.dificuldade === dificuldade);
        }

        if (loadingIndicator) loadingIndicator.style.display = 'none';

        if (receitasFiltradas.length === 0) {
            if (mensagemBusca) {
                mensagemBusca.textContent = 'Nenhuma receita encontrada com os critérios fornecidos.';
                mensagemBusca.className = 'info'; // ou 'no-results'
                mensagemBusca.style.display = 'block';
            }
        } else {
            receitasFiltradas.forEach(receita => {
                // Reutiliza a função criarCardReceita do main.js se estiver disponível globalmente
                // ou duplica/importa a lógica aqui.
                // Para este exemplo, vamos assumir que main.js já tornou criarCardReceita acessível
                // ou você pode copiar a função para cá.
                if (typeof criarCardReceita === 'function') {
                    receitasGrid.appendChild(criarCardReceita(receita));
                } else {
                    // Fallback ou cópia da função criarCardReceita aqui, se necessário.
                    // Por simplicidade, estou omitindo a duplicação completa do card aqui,
                    // mas você precisaria garantir que os cards sejam renderizados.
                    const cardPlaceholder = document.createElement('div');
                    cardPlaceholder.textContent = `ID: ${receita.id}, Título: ${receita.titulo}`;
                    receitasGrid.appendChild(cardPlaceholder);
                    console.warn("Função criarCardReceita não encontrada globalmente. Os cards podem não ser renderizados corretamente.");
                }
            });
        }
    }

    // Event Listeners
    if (btnBuscarFiltrar) {
        btnBuscarFiltrar.addEventListener('click', aplicarFiltrosEBusca);
    }

    // Opcional: aplicar filtros automaticamente ao mudar selects ou digitar
    if (termoBuscaInput) {
        termoBuscaInput.addEventListener('input', aplicarFiltrosEBusca); // Busca enquanto digita
    }
    if (categoriaSelect) {
        categoriaSelect.addEventListener('change', aplicarFiltrosEBusca);
    }
    if (dificuldadeSelect) {
        dificuldadeSelect.addEventListener('change', aplicarFiltrosEBusca);
    }

    // Carregar as receitas inicialmente para ter o cache local
    carregarTodasAsReceitas();
});

// Se a função criarCardReceita não estiver globalmente acessível por main.js,
// você precisará copiar sua definição para cá ou para um arquivo utilitário compartilhado.
// Exemplo de como seria se copiasse (lembre-se de ajustar caminhos de imagem de placeholder):
/*
function criarCardReceita(receita) {
    const card = document.createElement('div');
    card.className = 'receita-card';
    card.addEventListener('click', () => {
        window.location.href = `receita-detalhes.html?id=${receita.id}`;
    });

    const img = document.createElement('img');
    img.className = 'receita-img';
    // Ajuste o caminho do placeholder se necessário, ex: '../img/placeholder.jpg' ou '/img/placeholder.jpg'
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

    const dificuldadeText = document.createElement('span');
    dificuldadeText.textContent = receita.dificuldade;
    meta.appendChild(dificuldadeText);

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
*/ 