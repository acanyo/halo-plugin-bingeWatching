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