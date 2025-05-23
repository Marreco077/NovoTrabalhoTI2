// URL base da API
const API_URL_NOVA_RECEITA = 'http://localhost:8081/api';

// Elementos do DOM
const formReceita = document.getElementById('form-receita');
const loginAlerta = document.getElementById('login-alerta');
const ingredientesLista = document.getElementById('ingredientes-lista');
const adicionarIngredienteBtn = document.getElementById('adicionar-ingrediente');
const tituloPagina = document.querySelector('section.content h2'); // Seletor mais específico para o título h2 dentro da section content
const btnSubmitForm = formReceita ? formReceita.querySelector('button[type="submit"]') : null;

let idReceitaParaEditar = null; // Guarda o ID se estiver editando

// Verificar se o usuário está logado e se é modo de edição ao carregar a página
document.addEventListener('DOMContentLoaded', () => {
    inicializarFormularioReceita();
    
    if (adicionarIngredienteBtn) {
        adicionarIngredienteBtn.addEventListener('click', () => adicionarIngrediente()); // Chama sem args para novo
    }
    
    if (formReceita) {
        formReceita.addEventListener('submit', function(e) {
            e.preventDefault(); 
            salvarReceita(e);
        });
    }
    
    // Delegação de evento para botões de remover ingrediente, pois são adicionados dinamicamente
    if (ingredientesLista) {
        ingredientesLista.addEventListener('click', function(event) {
            if (event.target.classList.contains('remover-ingrediente')) {
                removerIngrediente(event);
            }
        });
    }
});

async function inicializarFormularioReceita() {
    console.log("Inicializando formulário de receita...");
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
        console.log("Usuário não logado, mostrando alerta.");
        mostrarLoginAlerta();
        return;
    }

    if (formReceita) formReceita.style.display = 'block';
    if (loginAlerta) loginAlerta.style.display = 'none';

    const urlParams = new URLSearchParams(window.location.search);
    idReceitaParaEditar = urlParams.get('id');
    console.log("ID da receita (parâmetro 'id'):", idReceitaParaEditar);

    if (idReceitaParaEditar) {
        console.log("Modo de EDIÇÃO.");
        if (tituloPagina) tituloPagina.textContent = 'Editar Receita';
        if (btnSubmitForm) btnSubmitForm.textContent = 'Atualizar Receita';
        await carregarDadosReceitaParaEdicao(idReceitaParaEditar);
    } else {
        console.log("Modo de CRIAÇÃO.");
        if (tituloPagina) tituloPagina.textContent = 'Cadastrar Nova Receita';
        if (btnSubmitForm) btnSubmitForm.textContent = 'Salvar Receita'; // Ou "Cadastrar Receita"
        if (ingredientesLista && ingredientesLista.children.length === 0) {
            console.log("Adicionando primeiro ingrediente para nova receita.");
            adicionarIngrediente(); 
        }
    }
}

async function carregarDadosReceitaParaEdicao(id) {
    console.log(`Carregando dados para receita ID: ${id}`);
    try {
        const response = await fetch(`${API_URL_NOVA_RECEITA}/receitas/${id}`);
        console.log("Resposta do fetch para carregar receita:", response);
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({ mensagem: 'Falha ao obter detalhes do erro.' }));
            console.error("Erro ao buscar receita:", response.status, errorData);
            throw new Error(errorData.mensagem || 'Falha ao carregar dados da receita para edição.');
        }
        const receita = await response.json();
        console.log("Dados da receita recebidos:", receita);

        if (!formReceita) {
            console.error("Elemento formReceita não encontrado no DOM.");
            return;
        }

        // Preencher campos do formulário
        document.getElementById('titulo').value = receita.titulo || '';
        document.getElementById('descricao').value = receita.descricao || '';
        document.getElementById('categoriaId').value = receita.categoriaId || '';
        document.getElementById('dificuldade').value = receita.dificuldade || '';
        document.getElementById('tempoPreparo').value = receita.tempoPreparo || '';
        document.getElementById('porcoes').value = receita.porcoes || '';
        document.getElementById('imagemUrl').value = receita.imagemUrl || '';
        document.getElementById('modoPreparo').value = receita.modoPreparo || '';
        console.log("Campos principais preenchidos.");

        if (ingredientesLista) {
            ingredientesLista.innerHTML = ''; 
            console.log("Lista de ingredientes limpa.");
            if (receita.ingredientes && receita.ingredientes.length > 0) {
                console.log("Adicionando ingredientes da receita:", receita.ingredientes);
                receita.ingredientes.forEach(ing => {
                    adicionarIngrediente(ing.nome, ing.quantidade ? String(ing.quantidade) : '', ing.observacao);
                });
            } else {
                console.log("Receita sem ingredientes, adicionando um campo vazio.");
                adicionarIngrediente(); 
            }
        } else {
            console.error("Elemento ingredientesLista não encontrado!");
        }
        console.log("Formulário preenchido para edição.");

    } catch (error) {
        console.error("Erro detalhado ao carregar dados da receita:", error);
        alert(`Não foi possível carregar os dados da receita para edição: ${error.message}`);
        if (tituloPagina) tituloPagina.textContent = 'Cadastrar Nova Receita';
        if (btnSubmitForm) btnSubmitForm.textContent = 'Salvar Receita';
        idReceitaParaEditar = null;
        if (formReceita) formReceita.reset();
        if (ingredientesLista) {
            ingredientesLista.innerHTML = '';
            adicionarIngrediente();
        }
    }
}

// Mostrar alerta para login
function mostrarLoginAlerta() {
    if (formReceita) formReceita.style.display = 'none';
    if (loginAlerta) loginAlerta.style.display = 'block';
}

// Adicionar novo ingrediente (modificado para aceitar valores iniciais)
function adicionarIngrediente(nome = '', quantidade = '', observacao = '') {
    console.log(`Adicionando ingrediente: Nome='${nome}', Quantidade='${quantidade}'`);
    if (!ingredientesLista) {
        console.error("Elemento ingredientesLista não encontrado ao tentar adicionar ingrediente.");
        return;
    }
    const novoIngrediente = document.createElement('div');
    novoIngrediente.className = 'ingrediente-item';
    
    novoIngrediente.innerHTML = `
        <input type="text" name="ingrediente" placeholder="Nome do ingrediente" value="${nome}" required>
        <input type="text" name="quantidade" placeholder="Quantidade" value="${quantidade}" required>
        <button type="button" class="btn btn-sm remover-ingrediente">Remover</button>
    `;
    
    // O event listener para remover é delegado agora, não precisa adicionar aqui.
    ingredientesLista.appendChild(novoIngrediente);
}

// Remover ingrediente
function removerIngrediente(event) {
    const ingredienteItem = event.target.closest('.ingrediente-item');
    if (!ingredienteItem) return;

    console.log("Tentando remover ingrediente.");
    if (ingredientesLista.children.length > 1 || (ingredientesLista.children.length === 1 && idReceitaParaEditar && ingredientesLista.children[0] !== ingredienteItem)) {
        ingredienteItem.remove();
        if (idReceitaParaEditar && ingredientesLista.children.length === 0) {
            console.log("Todos ingredientes removidos na edição, adicionando um novo vazio.");
            adicionarIngrediente();
        }
    } else if (ingredientesLista.children.length === 1 && !idReceitaParaEditar) {
        console.log("Limpando último ingrediente no modo de criação.");
        const nomeInput = ingredienteItem.querySelector('[name="ingrediente"]');
        const quantidadeInput = ingredienteItem.querySelector('[name="quantidade"]');
        if (nomeInput) nomeInput.value = '';
        if (quantidadeInput) quantidadeInput.value = '';
    }
}

// Salvar ou Atualizar receita
async function salvarReceita(event) {
    if (event) {
        event.preventDefault();
    }
    
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
        const titulo = document.getElementById('titulo').value;
        const descricao = document.getElementById('descricao').value;
        const categoriaId = parseInt(document.getElementById('categoriaId').value);
        const dificuldade = document.getElementById('dificuldade').value;
        const tempoPreparo = parseInt(document.getElementById('tempoPreparo').value);
        const porcoes = parseInt(document.getElementById('porcoes').value);
        const imagemUrl = document.getElementById('imagemUrl').value;
        const modoPreparo = document.getElementById('modoPreparo').value;
        
        const receitaPayload = {
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

        let response;
        let url;
        let method;

        if (idReceitaParaEditar) {
            url = `${API_URL_NOVA_RECEITA}/receitas/${idReceitaParaEditar}`;
            method = 'PUT';
            receitaPayload.id = parseInt(idReceitaParaEditar);
            console.log("Atualizando receita:", receitaPayload);
        } else {
            url = `${API_URL_NOVA_RECEITA}/receitas`;
            method = 'POST';
            console.log("Enviando nova receita:", receitaPayload);
        }
        
        response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(receitaPayload)
        });
        
        console.log("Resposta da API (salvar/atualizar):", response);
        
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({ mensagem: 'Falha ao obter detalhes do erro.'}));
            throw new Error(errorData.mensagem || (idReceitaParaEditar ? 'Erro ao atualizar receita' : 'Erro ao salvar receita'));
        }
        
        const receitaSalvaOuAtualizada = await response.json();
        console.log(idReceitaParaEditar ? "Receita atualizada:" : "Receita salva:", receitaSalvaOuAtualizada);
        
        alert(idReceitaParaEditar ? 'Receita atualizada com sucesso!' : 'Receita salva com sucesso!');
        window.location.href = `receita-detalhes.html?id=${receitaSalvaOuAtualizada.id}`;
    } catch (error) {
        console.error("Erro detalhado ao salvar/atualizar:", error);
        alert(`Erro: ${error.message}`);
    }
}

// Coletar ingredientes do formulário
function coletarIngredientes() {
    const ingredientes = [];
    const itens = ingredientesLista.querySelectorAll('.ingrediente-item');
    console.log(`Coletando ${itens.length} itens de ingredientes.`);
    
    itens.forEach(item => {
        const nomeInput = item.querySelector('[name="ingrediente"]');
        const quantidadeInput = item.querySelector('[name="quantidade"]');
        
        if (nomeInput && nomeInput.value.trim() && quantidadeInput && quantidadeInput.value.trim()) {
            const nome = nomeInput.value.trim();
            const quantidade = quantidadeInput.value.trim();
            console.log(`Ingrediente coletado: Nome='${nome}', Quantidade='${quantidade}'`);
            ingredientes.push({
                nome: nome,
                quantidade: quantidade 
            });
        }
    });
    
    console.log("Ingredientes finais coletados:", ingredientes);
    return ingredientes;
} 