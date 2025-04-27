class MovieWall {
    constructor() {
        this.init();
    }

    init() {
        this.initLazyLoading();
        this.initEventListeners();
        this.initSearchHandlers();
    }

    initLazyLoading() {
        const lazyImages = document.querySelectorAll('img.lazy');
        
        if ('IntersectionObserver' in window) {
            const imageObserver = new IntersectionObserver((entries, observer) => {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        const img = entry.target;
                        this.loadImage(img, observer);
                    }
                });
            }, {
                rootMargin: '50px'
            });

            lazyImages.forEach(img => imageObserver.observe(img));
        }
    }

    loadImage(img, observer) {
        const card = img.closest('.movie-card');
        const tempImage = new Image();

        tempImage.onload = () => {
            img.src = img.dataset.src;
            img.classList.remove('lazy');
            card.classList.remove('loading');
            observer.unobserve(img);
        };

        tempImage.onerror = () => {
            img.src = this.getErrorPlaceholder();
            card.classList.remove('loading');
            observer.unobserve(img);
        };

        tempImage.src = img.dataset.src;
    }

    initEventListeners() {
        // 电影卡片点击
        document.querySelectorAll('.movie-card').forEach(card => {
            card.addEventListener('click', () => {
                const name = card.dataset.name;
                if (name) {
                    window.location.href = `/movies/${name}`;
                }
            });
        });
    }

    initSearchHandlers() {
        // 搜索按钮点击事件
        const searchButtons = document.querySelectorAll('button');
        searchButtons.forEach(button => {
            button.addEventListener('click', (e) => {
                const input = e.target.closest('.relative')?.querySelector('input');
                if (input) {
                    this.handleSearch(input.value);
                }
            });
        });

        // 搜索输入框回车事件
        const searchInputs = document.querySelectorAll('input[type="text"]');
        searchInputs.forEach(input => {
            input.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') {
                    this.handleSearch(e.target.value);
                }
            });
        });
    }

    handleSearch(value) {
        const keyword = value.trim();
        if (keyword) {
            window.location.href = `/movies?keyword=${encodeURIComponent(keyword)}`;
        }
    }

    getErrorPlaceholder() {
        return 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjMwMCIgdmlld0JveD0iMCAwIDIwMCAzMDAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PHJlY3Qgd2lkdGg9IjIwMCIgaGVpZ2h0PSIzMDAiIGZpbGw9IiNmNWY1ZjUiLz48dGV4dCB4PSI1MCUiIHk9IjUwJSIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjE0IiBmaWxsPSIjYWFhIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBkeT0iLjNlbSI+5Zu+54mH5Yqg6L295aSx6LSlPC90ZXh0Pjwvc3ZnPg==';
    }
}

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', () => {
    window.movieWall = new MovieWall();
}); 