/**
 * 
 */
var rootUrl = "http://localhost:8080/timetracker-backend";
var wsRootUrl = "ws://localhost:8080/timetracker-backend";
var logOutUrl = "http://invalid:invalid@localhost:8080/timetracker-backend/timetracker/user/all";

var app = angular.module('timetracker', [ 'ui.bootstrap' ]);

// renders current user available before others need it
angular.element(document).ready(function() {
	var initInjector = angular.injector([ 'ng' ]);
	var $http = initInjector.get('$http');
	$http.get(rootUrl + '/timetracker/user/current').then(function(response) {
		app.constant('currentUser', response.data);
		angular.bootstrap(document, [ 'timetracker' ]);
	});
});

app.controller('roleCtrl', function($scope, currentUser){
	$scope.isNotManager = !("MANAGER"==currentUser.role) && !("ADMIN"==currentUser.role);
	$scope.isNotAdmin = !("ADMIN"==currentUser.role);
});

app.controller('currentUserCtrl', function($scope, currentUser) {
	$scope.url = logOutUrl;
	$scope.currentUser = currentUser;
});

app.controller('createBookingCtrl', function($scope, $http, $filter, currentUser) {
	//list of projects for current user (usersprojects)
	$scope.projectsList = {};
	$http.get(rootUrl + '/timetracker/usersprojects/user/' + currentUser.id).success(function(response) {
		for (var i = 0; i < response.length; ++i)
			//map users project by projects name
			$scope.projectsList[response[i].project.name] = response[i];
	});
	
	//opens date picker for start
	$scope.openStart = function($event) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.openedStart = true;
	};
	//opens date picker for end
	$scope.openEnd = function($event) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.openedEnd = true;
	};
	
	$scope.submit = function(){
		//convert date, time string to epoch time
		var startDateF = $filter('date')($scope.startDate, "MM dd, yyyy");
		var startTimeF = $filter('date')($scope.startTime,"HH:mm:ss")
		var endDateF = $filter('date')($scope.endDate, "MM dd, yyyy");
		var endTimeF = $filter('date')($scope.endTime,"HH:mm:ss")
		var booking = {
			"usersProjects" :  $scope.projectsList[$scope.fields.project],
			"start" : new Date(startDateF + " " + startTimeF).getTime(),
			"end" : new Date(endDateF + " " + endTimeF).getTime()
		};
		$http.post(rootUrl + "/timetracker/booking", booking).success(
				function(answer, status) {
					$scope.result = status;
				}).error(function(answer, status) {
			$scope.result = status;
		});
	}
	
});

app.controller('myBookingsCtrl', function($scope, $http, currentUser){
	$scope.bookingsList = [];
	$http.get(rootUrl + '/timetracker/booking/user/' + currentUser.id).success(function(response) {
		$scope.bookingsList = response;
	});
});

app.controller('myProjectsCtrl', function($scope, $http, currentUser){
	$scope.projectsList = [];
	$http.get(rootUrl + '/timetracker/usersprojects/user/' + currentUser.id).success(function(response) {
		$scope.projectsList = response;
	});
});

app.controller('allBookingsCtrl', function($scope, $http, $timeout){
	$scope.bookingsList = [];
	var ws = new WebSocket(wsRootUrl + '/allbookings');
	ws.onmessage = function(message){
		$timeout(function(){$rootScope.$apply(function(){
			$scope.bookingsList = message.data;
			
		})}, 0);
		
	};
	$http.get(rootUrl + '/timetracker/booking/all').success(function(response) {
		$scope.bookingsList = response;
	});
});

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
