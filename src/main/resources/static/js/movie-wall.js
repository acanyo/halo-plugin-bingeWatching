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
                        this.loadImage(img);
                        observer.unobserve(img);
                    }
                });
            }, {
                rootMargin: '50px 0px', // 提前50px开始加载
                threshold: 0.01 // 只要出现一点就开始加载
            });

            lazyImages.forEach(img => {
                imageObserver.observe(img);
                // 添加占位图
                img.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1 1"%3E%3C/svg%3E';
            });
        } else {
            // 降级处理：直接加载所有图片
            lazyImages.forEach(img => this.loadImage(img));
        }
    }

    loadImage(img) {
        const card = img.closest('.movie-card');
        if (!card) return;

        const loadingEl = card.querySelector('.loading-skeleton');
        if (loadingEl) loadingEl.style.display = 'flex';

        const actualImage = new Image();
        
        actualImage.onload = () => {
            img.src = img.dataset.src;
            img.classList.remove('lazy');
            img.classList.add('loaded');
            if (loadingEl) {
                loadingEl.style.opacity = '0';
                setTimeout(() => loadingEl.style.display = 'none', 300);
            }
        };

        actualImage.onerror = () => {
            img.src = this.getErrorPlaceholder();
            img.classList.remove('lazy');
            img.classList.add('error');
            if (loadingEl) loadingEl.style.display = 'none';
        };

        actualImage.src = img.dataset.src;
    }

    initEventListeners() {
        document.querySelectorAll('.movie-card').forEach(card => {
            // 移除点击事件，改为链接形式
            const name = card.dataset.name;
            if (name) {
                card.style.cursor = 'pointer';
                card.addEventListener('click', () => {
                    window.location.href = `/movies/${name}`;
                });
            }
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