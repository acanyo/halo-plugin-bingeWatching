document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.likcc-movie-card').forEach(function (card) {
        card.addEventListener('click', function () {
            const name = this.dataset.name;
            if (name) {
                window.location.href = '/movies/' + name;
            }
        });
    });
});

function initMovieWall() {
    $.get('/apis/api.bingewatching.lik.cc/v1alpha1/movies', function(data) {
        const movies = data.items;
        renderMovies(movies);
    });
}

function renderMovies(movies) {
    const $container = $('.likcc-movie-wall');
    $container.empty();

    movies.forEach(movie => {
        const $card = createMovieCard(movie);
        $container.append($card);
    });
}

function createMovieCard(movie) {
    const statusClass = getStatusClass(movie.spec.status);
    const statusText = getStatusText(movie.spec.status);
    
    const $card = $(`
        <div class="likcc-movie-card" data-name="${movie.metadata.name}">
            <img class="likcc-movie-poster" src="${movie.spec.vod_pic}" alt="${movie.spec.vod_name}">
            <div class="likcc-movie-info">
                <div class="likcc-movie-title">${movie.spec.vod_name}</div>
                <div class="likcc-movie-meta">
                    <div>类型：${movie.spec.type_name || '未知'}</div>
                    <div>评分：${movie.spec.vod_score || '暂无'}</div>
                    <div>已看：${movie.spec.seen || '0'}集</div>
                </div>
                <div class="likcc-movie-status ${statusClass}">${statusText}</div>
            </div>
        </div>
    `);

    return $card;
}

function getStatusClass(status) {
    switch(status) {
        case '观看中': return 'likcc-status-watching';
        case '完结': return 'likcc-status-finished';
        case '弃坑': return 'likcc-status-dropped';
        default: return '';
    }
}

function getStatusText(status) {
    switch(status) {
        case '观看中': return '追剧中';
        case '完结': return '已完结';
        case '弃坑': return '已弃坑';
        default: return '未知';
    }
}

// 页面加载状态
document.addEventListener('DOMContentLoaded', function() {
    const loadingSpinner = document.getElementById('loading-spinner');
    
    // 显示加载状态
    function showLoading() {
        if (loadingSpinner) {
            loadingSpinner.classList.remove('hidden');
            loadingSpinner.classList.add('active');
        }
    }
    
    // 隐藏加载状态
    function hideLoading() {
        if (loadingSpinner) {
            loadingSpinner.classList.add('hidden');
            loadingSpinner.classList.remove('active');
        }
    }
    
    // 处理页面切换
    function handlePageChange() {
        showLoading();
        // 平滑滚动到顶部
        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        });
    }
    
    // 监听分页按钮点击
    document.querySelectorAll('.likcc-pagination button').forEach(button => {
        button.addEventListener('click', handlePageChange);
    });
    
    // 监听搜索表单提交
    const searchForm = document.querySelector('form');
    if (searchForm) {
        searchForm.addEventListener('submit', function(e) {
            showLoading();
        });
    }
    
    // 图片加载完成后的处理
    document.querySelectorAll('.likcc-movie-card img').forEach(img => {
        img.addEventListener('load', function() {
            this.classList.add('loaded');
            // 添加淡入动画
            this.style.opacity = '1';
        });
    });
    
    // 添加卡片悬停效果
    document.querySelectorAll('.likcc-movie-card').forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-5px)';
            this.style.boxShadow = '0 10px 20px rgba(0, 0, 0, 0.1)';
        });
        
        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
            this.style.boxShadow = '0 4px 6px rgba(0, 0, 0, 0.1)';
        });
    });
    
    // 添加搜索框焦点效果
    const searchInput = document.getElementById('movie-search-input');
    if (searchInput) {
        searchInput.addEventListener('focus', function() {
            this.parentElement.style.transform = 'scale(1.02)';
        });
        
        searchInput.addEventListener('blur', function() {
            this.parentElement.style.transform = 'scale(1)';
        });
    }
    
    // 页面加载完成后隐藏加载状态
    window.addEventListener('load', hideLoading);
});

// 懒加载实现
(function() {
    const lazyImages = [].slice.call(document.querySelectorAll('img.lazy'));
    
    if ('IntersectionObserver' in window) {
        const lazyImageObserver = new IntersectionObserver(function(entries, observer) {
            entries.forEach(function(entry) {
                if (entry.isIntersecting) {
                    const lazyImage = entry.target;
                    lazyImage.src = lazyImage.dataset.src;
                    lazyImage.classList.remove('lazy');
                    lazyImageObserver.unobserve(lazyImage);
                    
                    // 添加加载完成动画
                    lazyImage.addEventListener('load', function() {
                        this.style.opacity = '1';
                        this.style.transition = 'opacity 0.3s ease';
                    });
                }
            });
        });
        
        lazyImages.forEach(function(lazyImage) {
            lazyImageObserver.observe(lazyImage);
        });
    }
})(); 