/**
 * Created by moritz löser (moritz.loeser@prodyna.com) on 15.06.2015.
 */
var rootUrl = "http://localhost:8080/timetracker-backend";
var wsRootUrl = "ws://localhost:8080/timetracker-backend";
var logOutUrl = "http://invalid:invalid@localhost:8080/timetracker-backend/timetracker/user/all";

var app = angular.module('timetracker', ['ui.bootstrap', 'ngWebSocket']);

// renders current user available before others need it
angular.element(document).ready(function () {
    var initInjector = angular.injector(['ng']);
    var $http = initInjector.get('$http');
    $http.get(rootUrl + '/timetracker/user/current').then(function (response) {
        app.constant('currentUser', response.data);
        angular.bootstrap(document, ['timetracker']);
    });
});

app.controller('timetrackerCtrl', function ($scope, $http, currentUser, $filter, $websocket) {
    $scope.url = logOutUrl;
    $scope.currentUser = currentUser;

    $scope.isManager = ("MANAGER" == currentUser.role) || ("ADMIN" == currentUser.role);
    $scope.isAdmin = ("ADMIN" == currentUser.role);

    if ($scope.isManager) {


        $scope.allBookingsList = {};
        var allBookingsWs = $websocket(wsRootUrl + '/allbookings');
        allBookingsWs.onMessage(function(event){
            $scope.allBookingsList = JSON.parse(event.data);
        });

        var updateAllBookings = function () {
            //all bookings

            $http.get(rootUrl + '/timetracker/booking/all').success(function (response) {
                $scope.allBookingsList = response;
            })
        };
        updateAllBookings();

        //projects
        $scope.allProjectsList = {};
        var allProjectsWs = $websocket(wsRootUrl + '/allprojects');
        allProjectsWs.onMessage(function(event){
            $scope.allProjectsList = JSON.parse(event.data);
        });

        var updateAllProjects = function () {
            $http.get(rootUrl + '/timetracker/project/all').success(function (response) {
                $scope.allProjectsList = response;
            });
        };
        updateAllProjects();

        $scope.newProject = {};
        //new project
        $scope.submitCreateProject = function () {
            var data = $scope.newProject;
            $http.post(rootUrl + "/timetracker/project", data).success(
                function (answer, status) {
                    $scope.createProjectResult = status;
                }).error(function (answer, status) {
                    $scope.createProjectResult = status;
                });
        };

        //users projects (register users to projects)
        var updateAllUsersProjects = function () {
            $scope.allUsersProjectsList = {};
            $http.get(rootUrl + '/timetracker/usersprojects/all').success(function (response) {
                $scope.allUsersProjectsList = response;
            });
        };
        updateAllUsersProjects();

        $scope.allUsersList = {};
        var allUsersWs = $websocket(wsRootUrl + '/allusers');
        allUsersWs.onMessage(function(event){
            $scope.allUsersList = JSON.parse(event.data);
        });

        var updateAllUsers = function() {
            $http.get(rootUrl + '/timetracker/user/all').success(function(response) {
                for (var i = 0; i < response.length; ++i)
                    $scope.allUsersList[response[i].name] = response[i];
            });
        };
        updateAllUsers();

        var updateAllProjects = function(){
            $scope.allProjectsList = {};
            $http.get(rootUrl + '/timetracker/project/all').success(function(response) {
                for (var i = 0; i < response.length; ++i)
                    $scope.allProjectsList[response[i].name] = response[i];
            });
        };
        updateAllProjects();

        $scope.newUsersProject = {};
        $scope.submitCreateUsersProject = function(){
            var data = {
                "user" : $scope.allUsersList[$scope.newUsersProject.user],
                "project" : $scope.allProjectsList[$scope.newUsersProject.project]
            };
            $http.post(rootUrl + "/timetracker/usersprojects", data).success(
                function(answer, status) {
                    $scope.createUsersProjectResult = status;
                    updateAllUsersProjects();
                    updateUsersProjects();
                }).error(function(answer, status) {
                    $scope.createUsersProjectResult = status;
                });
        };

    }
    if ($scope.isAdmin) {
        //manage users
        $scope.newUser = {};
        $scope.submitCreateUser = function(){
            var data = $scope.newUser;
            $http.post(rootUrl + "/timetracker/user", data).success(
                function(answer, status) {
                    $scope.createUserResult = status;
                }).error(function(answer, status) {
                    $scope.createUserResult = status;
                });
        };

    }


    //my bookings
    //update my bookings list
    var updateMyBookings = function () {
        $scope.myBookingsList = [];
        $http.get(rootUrl + '/timetracker/booking/user/' + currentUser.id).success(function (response) {
            $scope.myBookingsList = response;
        });
    };
    //update on load
    updateMyBookings();

    //
    //list of projects for current user (usersprojects)
    var updateUsersProjects = function () {
        $scope.usersprojectsList = {};
        $http.get(rootUrl + '/timetracker/usersprojects/user/' + currentUser.id).success(function (response) {
            for (var i = 0; i < response.length; ++i)
                //map users project by projects name
                $scope.usersprojectsList[response[i].project.name] = response[i];
        })
    };
    //update on load
    updateUsersProjects();


    //opens date picker for start
    $scope.openStart = function ($event) {
        $event.preventDefault();
        $event.stopPropagation();
        $scope.openedStart = true;
    };
    //opens date picker for end
    $scope.openEnd = function ($event) {
        $event.preventDefault();
        $event.stopPropagation();
        $scope.openedEnd = true;
    };


    $scope.newBooking = {};
    //timepicker values must be initialized
    $scope.newBooking.startTime = new Date();
    $scope.newBooking.endTime = new Date();


    //create new booking
    $scope.submitCreateBooking = function () {
        //convert date, time string to epoch time
        var startDateF = $filter('date')($scope.newBooking.startDate, "MM dd, yyyy");
        var startTimeF = $filter('date')($scope.newBooking.startTime, "HH:mm:ss")
        var endDateF = $filter('date')($scope.newBooking.endDate, "MM dd, yyyy");
        var endTimeF = $filter('date')($scope.newBooking.endTime, "HH:mm:ss")
        var booking = {
            "usersProjects": $scope.usersprojectsList[$scope.newBooking.project],
            "start": new Date(startDateF + " " + startTimeF).getTime(),
            "end": new Date(endDateF + " " + endTimeF).getTime()
        };
        $http.post(rootUrl + "/timetracker/booking", booking).success(
            function (answer, status) {
                $scope.createBookingResult = status;
                updateMyBookings();
            }).error(function (answer, status) {
                $scope.createBookingResult = status;
            });
    }


});