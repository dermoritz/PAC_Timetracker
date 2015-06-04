/**
 * 
 */
var rootUrl = "http://localhost:8080/timetracker-backend";

var currentUser = null;

var app = angular.module('timetracker', []);

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
	$scope.projectsList = [];
	$http.get(rootUrl + '/timetracker/project/all').success(function(response) {
		$scope.projectsList = response;
	});
	$scope.usersList = [];
	$http.get(rootUrl + '/timetracker/user/all').success(function(response) {
		$scope.usersList = response;
	});
	$scope.submit = function(){
		var data = {
					"user" : $scope.user,
					"project" : $scope.project
					};
		
		$scope.result ="?";
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
	$http.get(rootUrl + '/timetracker/usersprojects/all').success(function(response) {
		$scope.upList = response;
	});
});
