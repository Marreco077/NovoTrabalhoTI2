document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('login-form');
    
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
});

async function handleLogin(e) {
    e.preventDefault();
    
    const email = document.getElementById('email').value;
    const senha = document.getElementById('senha').value;
    const errorMessage = document.getElementById('error-message');
    
    // Esconde mensagem de erro anterior
    errorMessage.style.display = 'none';
    
    try {
        const response = await fetch(`${API_BASE_URL}/usuarios/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, senha })
        });
        
        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.mensagem || 'Erro ao fazer login');
        }
        
        // Armazena o usuário no localStorage
        setUsuarioLogado(data);
        
        // Redireciona para a página inicial
        window.location.href = 'index.html';
    } catch (error) {
        errorMessage.textContent = error.message || 'Email ou senha incorretos';
        errorMessage.style.display = 'block';
        console.error('Erro no login:', error);
    }
} 