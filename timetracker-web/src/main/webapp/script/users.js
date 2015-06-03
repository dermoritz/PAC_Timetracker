/**
 * 
 */
var app = angular.module('timetracker', []);

app.controller('usersController', function($scope, $http) {
	$scope.usersList = [];
	$http.get('http://localhost:8080/timetracker-backend/timetracker/user/all')
			.success(function(response) {
				$scope.usersList = response;
			});
}

);

app.controller('createUserController', function($scope, $http){
    $scope.submit=function(){
        var data = $scope.fields;
        $scope.result = "?";
        $http.post("http://localhost:8080/timetracker-backend/timetracker/user",data)
            .success(function(answer,status){
            	$scope.result = status;
            })
            .error(function(answer,status){
            	$scope.result = status;
            });
    }
}
);