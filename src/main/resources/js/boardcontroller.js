//controller and any services relating to the main boaard
plugin.controller("BoardController", function ($scope, autoRefresh, $rootScope) {
	$scope.projectName = "CDPipeline";
	$scope.results = autoRefresh.data;
	$rootScope.dataLoaded = false;
});

//polls a REST endpoint every five seconds and automatically refreshes
plugin.factory('autoRefresh', function ($http, $timeout, $rootScope) {
	var data = { resp: {}};
	var poller = function() {
		$http.get('?type=json').then( function(r) {
			data.resp = r.data;
			$rootScope.dataLoaded = true;
			$timeout(poller, 5000);
		});
	};
	poller();

	return {
		data: data
	};
});

// filter that puts element with key == null to the end of the list
plugin.filter("emptyToEnd", function () {
	return function (array, key) {
		if(!angular.isArray(array)) return;
        var present = array.filter(function (item) {
            return item[key];
        });
        var empty = array.filter(function (item) {
            return !item[key]
        });
		return present.concat(empty);
	};
});

// filter that puts in progress and queued projects to the top
plugin.filter("progressToFront", function () {
	return function (array, key) {
		if(!angular.isArray(array)) return;
        var inProgress = array.filter(function (item) {
            return item[key]["cdpipelineState"] == "CD_IN_PROGRESS";
        });
        var queued = array.filter(function (item) {
        	return item[key]["cdpipelineState"] == "CD_QUEUED";
        });
        var finished = array.filter(function (item) {
            return item[key]["cdpipelineState"] != "CD_IN_PROGRESS" && item[key]["cdpipelineState"] != "CD_QUEUED";
        });
		return inProgress.concat(queued).concat(finished);
	};
});

// The search button on the menu bar
plugin.filter('searchFor', keywordSearch);

// Helper method for the search button on the menu bar
function keywordSearch(){
    
	// All filters must return a function. The first parameter
	// is the data that is to be filtered, and the second is an
	// argument that may be passed with a colon (searchFor:searchString)
    
	return function(arr, searchString){
        
		if(!searchString){
			return arr;
		}
		var result = [];
		searchString = searchString.toLowerCase();
		// Using the forEach helper method to loop through the array
		angular.forEach(arr, function(item){
            if(item.projectName.toLowerCase().indexOf(searchString) !== -1 |
                item.planName.toLowerCase().indexOf(searchString) !== -1) {
                    result.push(item);
            }
            for (i = 0; i < item.contributors.length; i++) {
                if (item.contributors[i].username.toLowerCase().indexOf(searchString) !== -1 |
                     item.contributors[i].fullname.toLowerCase().indexOf(searchString) !== -1) {
                    result.push(item);
                }
            }
        });
        
		return result;
	};
}

// Filter that deals with edge case of the pipeline
// (when there's only one stage)
plugin.filter('pipelineWidth', function(){
  return function(input){
    if(input > 1) {
      return (1 / (input - 1));
    } else {
      return 0;
    }
  };
});

plugin.filter('percentageLimit', function(){
	return function(input){
		if(input <= 100) {
			return input;
		} else {
			return 100;
		}
	}
})

