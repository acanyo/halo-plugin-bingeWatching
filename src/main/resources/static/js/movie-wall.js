$(document).ready(function() {
    initMovieWall();
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
    
    return $(`
        <div class="likcc-movie-card">
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