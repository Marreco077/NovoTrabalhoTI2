// URL base da API
const API_URL = 'http://localhost:8080/api';

// Elementos do DOM
const formReceita = document.getElementById('form-receita');
const loginAlerta = document.getElementById('login-alerta');
const ingredientesLista = document.getElementById('ingredientes-lista');
const adicionarIngredienteBtn = document.getElementById('adicionar-ingrediente');

// Estado da aplicação
let usuarioLogado = null;

// Verificar se o usuário está logado ao carregar a página
document.addEventListener('DOMContentLoaded', () => {
    verificarUsuarioLogado();
    
    // Adicionar eventos
    adicionarIngredienteBtn.addEventListener('click', adicionarIngrediente);
    formReceita.addEventListener('submit', salvarReceita);
    
    // Adicionar evento para os botões de remover ingrediente existentes
    document.querySelectorAll('.remover-ingrediente').forEach(btn => {
        btn.addEventListener('click', removerIngrediente);
    });
});

// Verificar se o usuário está logado
function verificarUsuarioLogado() {
    const usuarioJSON = localStorage.getItem('usuario');
    
    if (usuarioJSON) {
        try {
            usuarioLogado = JSON.parse(usuarioJSON);
            // Usuário está logado, mostrar formulário
            formReceita.style.display = 'block';
            loginAlerta.style.display = 'none';
        } catch (e) {
            // Erro ao processar dados do usuário
            mostrarLoginAlerta();
        }
    } else {
        // Usuário não está logado
        mostrarLoginAlerta();
    }
}

// Mostrar alerta para login
function mostrarLoginAlerta() {
    formReceita.style.display = 'none';
    loginAlerta.style.display = 'block';
}

// Adicionar novo ingrediente
function adicionarIngrediente() {
    const novoIngrediente = document.createElement('div');
    novoIngrediente.className = 'ingrediente-item';
    
    novoIngrediente.innerHTML = `
        <input type="text" name="ingrediente" placeholder="Nome do ingrediente" required>
        <input type="text" name="quantidade" placeholder="Quantidade" required>
        <input type="text" name="unidade" placeholder="Unidade">
        <button type="button" class="btn btn-sm remover-ingrediente">Remover</button>
    `;
    
    // Adicionar evento ao botão remover
    const btnRemover = novoIngrediente.querySelector('.remover-ingrediente');
    btnRemover.addEventListener('click', removerIngrediente);
    
    ingredientesLista.appendChild(novoIngrediente);
}

// Remover ingrediente
function removerIngrediente(event) {
    const ingredienteItem = event.target.closest('.ingrediente-item');
    
    // Não remover se for o único ingrediente
    if (ingredientesLista.children.length > 1) {
        ingredienteItem.remove();
    }
}

// Salvar receita
async function salvarReceita(event) {
    event.preventDefault();
    
    if (!usuarioLogado) {
        mostrarLoginAlerta();
        return;
    }
    
    try {
        // Coletar dados do formulário
        const formData = new FormData(formReceita);
        
        // Criar objeto da receita
        const receita = {
            titulo: formData.get('titulo'),
            descricao: formData.get('descricao'),
            categoriaId: parseInt(formData.get('categoriaId')),
            dificuldade: formData.get('dificuldade'),
            tempoPreparo: parseInt(formData.get('tempoPreparo')),
            porcoes: parseInt(formData.get('porcoes')),
            imagemUrl: formData.get('imagemUrl'),
            modoPreparo: formData.get('modoPreparo'),
            usuarioId: usuarioLogado.id,
            ingredientes: coletarIngredientes()
        };
        
        // Enviar para a API
        const response = await fetch(`${API_URL}/receitas`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(receita)
        });
        
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.mensagem || 'Erro ao salvar receita');
        }
        
        const receitaSalva = await response.json();
        
        // Receita salva com sucesso, redirecionar para página de detalhes
        alert('Receita salva com sucesso!');
        window.location.href = `receita-detalhes.html?id=${receitaSalva.id}`;
    } catch (error) {
        alert(`Erro: ${error.message}`);
    }
}

// Coletar ingredientes do formulário
function coletarIngredientes() {
    const ingredientes = [];
    const itens = ingredientesLista.querySelectorAll('.ingrediente-item');
    
    itens.forEach(item => {
        const nomeInput = item.querySelector('[name="ingrediente"]');
        const quantidadeInput = item.querySelector('[name="quantidade"]');
        const unidadeInput = item.querySelector('[name="unidade"]');
        
        if (nomeInput.value.trim() && quantidadeInput.value.trim()) {
            ingredientes.push({
                nome: nomeInput.value.trim(),
                quantidade: quantidadeInput.value.trim(),
                unidade: unidadeInput.value.trim()
            });
        }
    });
    
    return ingredientes;
} 