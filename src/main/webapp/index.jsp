<%@ page contentType="text/html; charset=UTF-8" %>
<title>Exua2podcast!</title>
<h1>Exua2podcast!</h1>

<a href="shortcut.jsp?javascript:void(location.href='podcast://exua2podcast.appspot.com/xspf2podcast?xspf=http://www.ex.ua'+$('a[href$=xspf]').attr('href')+'&img='+($('img[src$=&quot;?1600&quot;]').attr('src')||''))">
Перейдіть, щоб встановити шорткат для Safari.
</a>
<br/>
<%--<a href="/shortcutVk.jsp?javascript:void(location.href='podcast://exua2podcast.appspot.com/vk2podcast?vk='+location.href.match(/wall-\d+_\d+/)[0])">
VK Shortcut
</a>--%>
<a href="shortcutVk.jsp?javascript:void(<%=
"(function(){" +
 "if(!window.any2Podcast){" +
    "var s=document.createElement('script');" +
    "s.type='application/javascript';" +
    "s.src='https://exua2podcast.appspot.com/injectVk.js?'+new Date().getTime();" +
    "document.body.appendChild(s)" +
 "}else any2Podcast()" +
"})()"
%>)">
VK Shortcut
</a>
