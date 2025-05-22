document.addEventListener('DOMContentLoaded', () => {
    const cadastroForm = document.getElementById('cadastro-form');
    
    if (cadastroForm) {
        cadastroForm.addEventListener('submit', handleCadastro);
    }
});

async function handleCadastro(e) {
    e.preventDefault();
    
    const nome = document.getElementById('nome').value;
    const email = document.getElementById('email').value;
    const senha = document.getElementById('senha').value;
    const confirmarSenha = document.getElementById('confirmar-senha').value;
    const errorMessage = document.getElementById('error-message');
    
    // Esconde mensagem de erro anterior
    errorMessage.style.display = 'none';
    
    // Validação básica
    if (senha !== confirmarSenha) {
        errorMessage.textContent = 'As senhas não coincidem';
        errorMessage.style.display = 'block';
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/usuarios`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ nome, email, senha })
        });
        
        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.mensagem || 'Erro ao cadastrar');
        }
        
        // Armazena o usuário no localStorage
        setUsuarioLogado(data);
        
        // Redireciona para a página inicial
        window.location.href = 'index.html';
    } catch (error) {
        errorMessage.textContent = error.message || 'Erro ao cadastrar. Tente novamente.';
        errorMessage.style.display = 'block';
        console.error('Erro no cadastro:', error);
    }
} 