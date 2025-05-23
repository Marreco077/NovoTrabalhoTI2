document.addEventListener('DOMContentLoaded', () => {
    const themeToggleButton = document.getElementById('theme-toggle-button');
    const body = document.body;

    const lightIcon = '☀️'; 
    const darkIcon = '🌙';

    // Função para aplicar o tema e atualizar o ícone
    function applyTheme(theme) {
        if (theme === 'dark') {
            body.classList.add('dark-mode');
            if (themeToggleButton) themeToggleButton.textContent = lightIcon;
            localStorage.setItem('theme', 'dark');
        } else {
            body.classList.remove('dark-mode');
            if (themeToggleButton) themeToggleButton.textContent = darkIcon;
            localStorage.setItem('theme', 'light');
        }
    }

    // Função para alternar o tema
    function toggleTheme() {
        const currentTheme = localStorage.getItem('theme') || (body.classList.contains('dark-mode') ? 'dark' : 'light');
        const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
        applyTheme(newTheme);
    }

    // Aplicar tema ao carregar a página
    let preferredTheme = localStorage.getItem('theme');
    if (!preferredTheme) {
        // Se não há preferência salva, verifica a preferência do sistema
        if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
            preferredTheme = 'dark';
        } else {
            preferredTheme = 'light'; // Padrão para claro se nada for detectado
        }
    }
    applyTheme(preferredTheme);

    // Event listener para o botão
    if (themeToggleButton) {
        themeToggleButton.addEventListener('click', toggleTheme);
    }
}); 