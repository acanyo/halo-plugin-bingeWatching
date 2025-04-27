// 页面加载完成后执行
document.addEventListener('DOMContentLoaded', function() {
    initializeImageHandling();
    initializeScrollEffects();
});

// 图片处理初始化
function initializeImageHandling() {
    const posterImage = document.querySelector('.likcc-handsome-poster img');
    if (posterImage) {
        // 图片加载成功处理
        posterImage.addEventListener('load', function() {
            this.classList.add('loaded');
            this.style.animation = 'fadeIn 0.5s ease forwards';
        });

        // 图片加载失败处理
        posterImage.addEventListener('error', function() {
            this.src = '/images/default-poster.jpg';
            this.classList.add('loaded');
        });
    }
}

// 滚动效果初始化
function initializeScrollEffects() {
    const nav = document.querySelector('.likcc-handsome-nav');
    let lastScrollTop = 0;

    window.addEventListener('scroll', function() {
        const scrollTop = window.pageYOffset || document.documentElement.scrollTop;

        // 导航栏显示/隐藏逻辑
        if (scrollTop > lastScrollTop) {
            // 向下滚动
            nav.style.transform = 'translateY(-100%)';
        } else {
            // 向上滚动
            nav.style.transform = 'translateY(0)';
        }

        lastScrollTop = scrollTop;
    });
}

// 返回列表页
function goBack() {
    window.location.href = '/movies';
}

// 动态设置主题色
function setThemeColor(color) {
    document.documentElement.style.setProperty('--likcc-handsome-theme-color', color);
}

// 动态设置标题色
function setTitleColor(color) {
    document.documentElement.style.setProperty('--likcc-handsome-title-color', color);
}

// 动态设置描述文字色
function setDescriptionColor(color) {
    document.documentElement.style.setProperty('--likcc-handsome-description-color', color);
} 