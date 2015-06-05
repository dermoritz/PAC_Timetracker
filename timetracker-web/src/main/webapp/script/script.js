/**
 * 
 */
var rootUrl = "http://localhost:8080/timetracker-backend";

var currentUser = null;

var app = angular.module('timetracker', [ 'ui.bootstrap' ]);

app.controller('createBookingCtrl', function($scope, $http, $filter) {
	$scope.openStart = function($event) {
		$event.preventDefault();
		$event.stopPropagation();

		$scope.openedStart = true;
	};
	$scope.openEnd = function($event) {
		$event.preventDefault();
		$event.stopPropagation();

		$scope.openedEnd = true;
	};
//	var startTime;
//	var endTime;
//	$scope.timeChanged = function(){
//		endTime = $scope.endTime; 
//		startTime = $scope.startTime;
//	}
	
	$scope.submit = function(){
		var startDateF = $filter('date')($scope.startDate, "MM dd, yyyy");
		var startTimeF = $filter('date')($scope.startTime,"HH:mm:ss")
		var end = $scope.endDate;
		$scope.result = new Date(startDateF+" "+startTimeF).getTime();
	}
	
});

app.controller('currentUser', function($scope, $http) {
	$scope.currentUser = null;
	$http.get(rootUrl + '/timetracker/user/current').success(
			function(response) {
				$scope.currentUser = response;
				currentUser = response;
			});
}

);

app.controller('usersController', function($scope, $http) {
	$scope.usersList = [];
	$http.get(rootUrl + '/timetracker/user/all').success(function(response) {
		$scope.usersList = response;
	});
}

);

app.controller('createUserController', function($scope, $http) {
	$scope.submit = function() {
		var data = $scope.fields;
		$scope.result = "?";
		$http.post(rootUrl + "/timetracker/user", data).success(
				function(answer, status) {
					$scope.result = status;
				}).error(function(answer, status) {
			$scope.result = status;
		});
	}
});

app.controller('createProjectController', function($scope, $http) {
	$scope.submit = function() {
		var data = $scope.fields;
		$scope.result = "?";
		$http.post(rootUrl + "/timetracker/project", data).success(
				function(answer, status) {
					$scope.result = status;
				}).error(function(answer, status) {
			$scope.result = status;
		});
	}
});

app.controller('allProjectsController', function($scope, $http) {
	$scope.projectsList = [];
	$http.get(rootUrl + '/timetracker/project/all').success(function(response) {
		$scope.projectsList = response;
	});
});

app.controller('registerUserToProjectController', function($scope, $http) {
	$scope.projectsList = {};
	$http.get(rootUrl + '/timetracker/project/all').success(function(response) {
		for (var i = 0; i < response.length; ++i)
			$scope.projectsList[response[i].name] = response[i];
	});
	$scope.usersList = {};
	$http.get(rootUrl + '/timetracker/user/all').success(function(response) {
		for (var i = 0; i < response.length; ++i)
			$scope.usersList[response[i].name] = response[i];
	});
	$scope.submit = function() {
		var data = {
			"user" : $scope.usersList[$scope.fields.user],
			"project" : $scope.projectsList[$scope.fields.project]
		};

		$scope.result = "?";
		$http.post(rootUrl + "/timetracker/usersprojects", data).success(
				function(answer, status) {
					$scope.result = status;
				}).error(function(answer, status) {
			$scope.result = status;
		});
	}
});

app.controller('usersProjectsController', function($scope, $http) {
	$scope.upList = [];
	$http.get(rootUrl + '/timetracker/usersprojects/all').success(
			function(response) {
				$scope.upList = response;
			});
});
