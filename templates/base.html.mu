<!doctype html>
<!--[if lt IE 7 ]> 
<html lang="en" class="no-js ie6"> 
<![endif]-->
<!--[if IE 7 ]>
<html lang="en" class="no-js ie7">
<![endif]-->
<!--[if IE 8 ]>
<html lang="en" class="no-js ie8">
<![endif]--><!--[if IE 9 ]>
<html lang="en" class="no-js ie9">
<![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--><html lang="en" class="no-js"><!--<![endif]-->
  <head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    {{#home}} <title> {{ title }}</title>{{/home}}
    <title>{{ title }} - Where's the MBTA?</title>
    <meta name="description" content="">
    <meta name="author" content="">
    <meta name="HandheldFriendly" content="True">
    <meta name="MobileOptimized" content="320">
    <meta name="viewport" content="width=device-width, target-densitydpi=160dpi, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta http-equiv="cleartype" content="on">
    <link rel="apple-touch-icon-precomposed" sizes="114x114" href="/static/apple-touch-icon-114x114-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="72x72" href="/static/apple-touch-icon-72x72-precomposed.png">
    <link rel="apple-touch-icon-precomposed" href="/static/apple-touch-icon-precomposed.png">
    <link rel="shortcut icon" href="/static/apple-touch-icon.png">
    <link rel="shortcut icon" href="/favicon.ico">
    <!--[if lt IE 9]>
    <script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
    <script src="/static/js/lib/modernizr-1.7.min.js"></script>
    <link rel="stylesheet" href="/static/css/style.css?v=37">
    <link rel="stylesheet" href="/static/css/font-awesome.css?v=1">
  </head>
  <body>
    <div id="container">
      <div id="wrapper">
        <header>
          <nav>
            {{{ bread-crumbs }}}
          </nav>
          <h1>{{ title }}</h1>
        </header>
        <section id="main">
          {{#predictions}}
          <div id="status-bad" class="status-bar"></div>
          <div id="status-good" class="status-bar"></div>
          {{/predictions}}

          {{#home}} {{> home.html }} {{/home}}
          {{#about}} {{> about.html }} {{/about}}

          {{{ main-content }}}
  
        </section>
        <section id="relevant-tweets">
          {{{ relevant-tweets }}}
        </section>
        <section id="nearby-stations"></section>
        <footer>
          <ul>
            <li{{#about}} class="active"{{/about}}><a href="/about">About</a></li>
            <li><a href="//twitter.com/wheresthembta" class="external">Twitter</a></li>
            <li><a href="//github.com/ashafa/wheresthembta" class="external">GitHub</a></li>
            <li> &copy; 2012 Where's the MBTA?</li>
          </ul>
        </footer>
      </div>
    </div>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
    <script>!window.jQuery && document.write(unescape('%3Cscript src="/static/js/lib/jquery-1.7.1.min.js"%3E%3C/script%3E'))</script>
    <script src="/socket.io/socket.io.js"></script>
    <script src="/static/js/script.js"></script>
    <script>
      var SERVER_TIME_DIFF = 0, PREDICTIONS = false;
      {{#predictions}}
      SERVER_TIME_DIFF = new Date().getTime() - {{ time }};
      PREDICTIONS = {{{ predictions-json }}};
      {{/predictions}}
    </script>
    <script>
      var _gaq=[['_setAccount','UA-28700129-1'],['_trackPageview']]; // Change UA-XXXXX-X to be your site's ID
      (function(d,t){var g=d.createElement(t),s=d.getElementsByTagName(t)[0];g.async=1;
      g.src=('https:'==location.protocol?'//ssl':'//www')+'.google-analytics.com/ga.js';
      s.parentNode.insertBefore(g,s)}(document,'script'));
    </script>
  </body>
</html>
