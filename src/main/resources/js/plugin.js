var plugin = angular.module("CDPipeline", ['ui.bootstrap', 'ngAnimate']);

//used to prevent IE caching which blocks live updates
plugin.config(['$httpProvider', function($httpProvider) {
    if (!$httpProvider.defaults.headers.get) {
        $httpProvider.defaults.headers.get = {};    
    }
    $httpProvider.defaults.headers.get['If-Modified-Since'] = '0';
}]);