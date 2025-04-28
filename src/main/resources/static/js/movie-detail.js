// handsome-bingeWatching 影视详情页精简交互脚本

document.addEventListener('DOMContentLoaded', function() {
    // 主题色动态设置（如有需要可扩展）
    if (window.styleConfig) {
        document.documentElement.style.setProperty('--theme-color', window.styleConfig.themeColor);
        document.documentElement.style.setProperty('--title-color', window.styleConfig.titleColor);
        document.documentElement.style.setProperty('--description-color', window.styleConfig.descriptionColor);
    }
    // 图片骨架屏
    handsomeBingeWatchingInitImgSkeleton();
    likccBingeWatchingInitQuotesCarousel();
});

// 返回上一页
function handsomeBingeWatchingGoBack() {
    window.history.back();
}

// 图片骨架屏动画
function handsomeBingeWatchingInitImgSkeleton() {
    document.querySelectorAll('.handsome-bingeWatching-img-skeleton').forEach(img => {
        img.addEventListener('load', function() {
            this.classList.add('animate__fadeIn');
            this.classList.remove('bg-gray-200');
        });
        img.addEventListener('error', function() {
            this.src = '/plugins/bingeWatching/assets/static/img/default-poster.png';
        });
    });
}

// 经典台词轮播
function likccBingeWatchingInitQuotesCarousel() {
    const lines = window.likccClassicLines || [];
    if (!lines.length) return;
    let idx = 0;
    const quoteBox = document.getElementById('likcc-bingeWatching-quotes-list');
    const prevBtn = document.getElementById('likcc-bingeWatching-quotes-prev');
    const nextBtn = document.getElementById('likcc-bingeWatching-quotes-next');
    let timer = null;

    function showQuote(i) {
        idx = (i + lines.length) % lines.length;
        quoteBox.style.opacity = 0;
        quoteBox.style.transform = 'translateY(20px)';
        setTimeout(() => {
            quoteBox.innerText = lines[idx];
            quoteBox.style.opacity = 1;
            quoteBox.style.transform = 'translateY(0)';
        }, 200);
    }

    function next() { showQuote(idx + 1); }
    function prev() { showQuote(idx - 1); }
    function auto() { timer = setInterval(next, 4000); }
    function stop() { clearInterval(timer); }

    if (prevBtn) prevBtn.onclick = prev;
    if (nextBtn) nextBtn.onclick = next;
    if (quoteBox) {
        quoteBox.onmouseenter = stop;
        quoteBox.onmouseleave = auto;
    }
    if (prevBtn) {
        prevBtn.onmouseenter = stop;
        prevBtn.onmouseleave = auto;
    }
    if (nextBtn) {
        nextBtn.onmouseenter = stop;
        nextBtn.onmouseleave = auto;
    }

    showQuote(0);
    auto();
} 