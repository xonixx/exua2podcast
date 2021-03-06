function any2Podcast() {
    try {
        var podcastId = (location.href.match(/wall-\d+_\d+/) || {})[0];
        if (!podcastId) {
            alert('Not a wall post!');
            return;
        }

        var d = ['Debug:'];
        d.push('podcastId: ' + podcastId);

        var podcast = {items: []};

        var img = document.querySelector('img.thumb_map_img');
        if (img)
            podcast.img = img.src;
        var info = document.querySelector('.wi_body .pi_text');
        if (info) {
            var title = info.firstChild.nodeValue;
            d.push('title: ' + title);
            if (title.length > 40)
                title = title.substring(0, 40) + '...';
            podcast.title = title;
        }

        var audios = document.querySelectorAll('div.medias_audios_list div.ai_body');
        if (!audios.length) {
            alert('No audios!');
            return;
        }

        d.push('audios: ' + audios.length);

        for (var i = 0; i < audios.length; i++) {
            var audio = audios[i];
            var artist = audio.querySelector('.ai_artist').firstChild.nodeValue;
            var title1 = audio.querySelector('.ai_title').firstChild.nodeValue;
            var url = audio.querySelector('input[type="hidden"]').value;
            url = url.split('?')[0].replace('https:', 'http:');
            var name = artist + ' - ' + title1;
            podcast.items.push({name: name, url: url});
        }

        var xhr = new XMLHttpRequest();
        xhr.open('POST', 'https://exua2podcast.appspot.com/any2podcast?uid=' + podcastId);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.onload = function () {
            if (xhr.status === 200) {
                location.href = 'podcast://exua2podcast.appspot.com/any2podcast?uid=' + podcastId;
            } else alert('Error =(');
        };
        xhr.send(JSON.stringify(podcast));
        // alert(d.join('\n'))
    } catch (e) {
        alert('Error: ' + e);
    }
}

any2Podcast();