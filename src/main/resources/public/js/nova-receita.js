// URL base da API
const API_URL = 'http://localhost:8081/api';

// Elementos do DOM
const formReceita = document.getElementById('form-receita');
const loginAlerta = document.getElementById('login-alerta');
const ingredientesLista = document.getElementById('ingredientes-lista');
const adicionarIngredienteBtn = document.getElementById('adicionar-ingrediente');

// Verificar se o usuário está logado ao carregar a página
document.addEventListener('DOMContentLoaded', () => {
    verificarUsuarioLogado();
    
    // Adicionar eventos
    adicionarIngredienteBtn.addEventListener('click', adicionarIngrediente);
    
    // Substitui o comportamento padrão do submit para usar AJAX
    if (formReceita) {
        formReceita.addEventListener('submit', function(e) {
            e.preventDefault(); // Impede o comportamento padrão de submissão
            salvarReceita(e);
        });
    }
    
    // Adicionar evento para os botões de remover ingrediente existentes
    document.querySelectorAll('.remover-ingrediente').forEach(btn => {
        btn.addEventListener('click', removerIngrediente);
    });
});

// Verificar se o usuário está logado
function verificarUsuarioLogado() {
    let usuario = null;
    
    try {
        const usuarioJSON = localStorage.getItem('usuarioLogado');
        if (usuarioJSON) {
            usuario = JSON.parse(usuarioJSON);
        }
    } catch (e) {
        console.error("Erro ao ler localStorage:", e);
    }
    
    console.log("Status do login na página nova-receita:", usuario ? "Usuário logado" : "Usuário não logado");
    
    if (usuario) {
        // Usuário está logado, mostrar formulário
        formReceita.style.display = 'block';
        loginAlerta.style.display = 'none';
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
    if (event) {
        event.preventDefault();
    }
    
    // Obter usuário do localStorage
    let usuario = null;
    try {
        const usuarioJSON = localStorage.getItem('usuarioLogado');
        if (usuarioJSON) {
            usuario = JSON.parse(usuarioJSON);
        }
    } catch (e) {
        console.error("Erro ao ler localStorage:", e);
    }
    
    if (!usuario) {
        mostrarLoginAlerta();
        return;
    }
    
    try {
        // Coletar dados do formulário manualmente
        const titulo = document.getElementById('titulo').value;
        const descricao = document.getElementById('descricao').value;
        const categoriaId = parseInt(document.getElementById('categoriaId').value);
        const dificuldade = document.getElementById('dificuldade').value;
        const tempoPreparo = parseInt(document.getElementById('tempoPreparo').value);
        const porcoes = parseInt(document.getElementById('porcoes').value);
        const imagemUrl = document.getElementById('imagemUrl').value;
        const modoPreparo = document.getElementById('modoPreparo').value;
        
        // Criar objeto da receita
        const receita = {
            titulo: titulo,
            descricao: descricao,
            categoriaId: categoriaId,
            dificuldade: dificuldade,
            tempoPreparo: tempoPreparo,
            porcoes: porcoes,
            imagemUrl: imagemUrl,
            modoPreparo: modoPreparo,
            usuarioId: usuario.id,
            ingredientes: coletarIngredientes()
        };
        
        console.log("Enviando receita:", receita);
        
        // Enviar para a API
        const response = await fetch(`${API_URL}/receitas`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(receita)
        });
        
        console.log("Resposta da API:", response);
        
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.mensagem || 'Erro ao salvar receita');
        }
        
        const receitaSalva = await response.json();
        console.log("Receita salva:", receitaSalva);
        
        // Receita salva com sucesso, redirecionar para página de detalhes
        alert('Receita salva com sucesso!');
        window.location.href = `receita-detalhes.html?id=${receitaSalva.id}`;
    } catch (error) {
        console.error("Erro detalhado:", error);
        alert(`Erro ao salvar receita: ${error.message}`);
    }
}

// Coletar ingredientes do formulário
function coletarIngredientes() {
    const ingredientes = [];
    const itens = ingredientesLista.querySelectorAll('.ingrediente-item');
    
    itens.forEach(item => {
        const nomeInput = item.querySelector('[name="ingrediente"]');
        const quantidadeInput = item.querySelector('[name="quantidade"]');
        
        if (nomeInput && nomeInput.value.trim() && quantidadeInput && quantidadeInput.value.trim()) {
            ingredientes.push({
                nome: nomeInput.value.trim(),
                quantidade: parseFloat(quantidadeInput.value.trim() || "0")
            });
        }
    });
    
    return ingredientes;
} 