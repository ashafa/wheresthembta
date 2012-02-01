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
    <link rel="shortcut icon" href="apple-touch-icon.png">
    <link rel="shortcut icon" href="favicon.ico">
    <!--[if lt IE 9]>
    <script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
    <script src="/static/js/lib/modernizr-1.7.min.js"></script>
    <link rel="stylesheet" href="/static/css/style.css?v=3">
    
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
          <div id="status-bad" class="status-bar"></div>
          <div id="status-good" class="status-bar"></div>
          
          {{#home}} {{> home.html }} {{/home}}
          {{#about}} {{> about.html }} {{/about}}
          {{#terms}} {{> terms.html }} {{/terms}}
          {{#privacy}} {{> privacy.html }} {{/privacy}}

          {{{ main-content }}}
  
        </section>
        <section id="nearby-stations"></section>
        <footer>
          <ul>
            <li{{#about}} class="active"{{/about}}><a href="/about">About</a></li>
            <li{{#terms}} class="active"{{/terms}}><a href="/terms">Terms</a></li>
            <li{{#privacy}} class="active"{{/privacy}}><a href="/privacy">Privacy</a></li>
            <li><a href="http://twitter.com/wheresthembta" target="_blank">@wheresthembta</a></li>
            <li> &copy; 2012 Where's the MBTA?</li>
          </ul>
        </footer>
      </div>
    </div>
    <script>
      var PREDICTIONS = false;
      {{#predictions}}
      PREDICTIONS = {{{ predictions-json }}};
      {{/predictions}}
    </script>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
    <script>!window.jQuery && document.write(unescape('%3Cscript src="/static/js/lib/jquery-1.7.1.min.js"%3E%3C/script%3E'))</script>
    <script src="/static/js/lib/innershiv.min.js"></script>
    <script src="/static/js/script.js"></script>
    <script>
      var _gaq=[['_setAccount','UA-28700129-1'],['_trackPageview']]; // Change UA-XXXXX-X to be your site's ID
      (function(d,t){var g=d.createElement(t),s=d.getElementsByTagName(t)[0];g.async=1;
      g.src=('https:'==location.protocol?'//ssl':'//www')+'.google-analytics.com/ga.js';
      s.parentNode.insertBefore(g,s)}(document,'script'));
    </script>
  </body>
</html>
