/**
 * Created by moritz löser (moritz.loeser@prodyna.com) on 15.06.2015.
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

app.controller('timetrackerCtrl', function($scope, $http, currentUser, $filter){
    $scope.url = logOutUrl;
    $scope.currentUser = currentUser;

    //my bookings
    //update my bookings list
    var updateMyBookings = function(){
        $scope.myBookingsList = [];
        $http.get(rootUrl + '/timetracker/booking/user/' + currentUser.id).success(function(response) {
            $scope.myBookingsList = response;
        });
    };
    //update on load
    updateMyBookings();
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

    //list of projects for current user (usersprojects)
    $scope.usersprojectsList = {};
    $http.get(rootUrl + '/timetracker/usersprojects/user/' + currentUser.id).success(function(response) {
        for (var i = 0; i < response.length; ++i)
            //map users project by projects name
            $scope.usersprojectsList[response[i].project.name] = response[i];
    });
    $scope.newBooking = {};
    $scope.newBooking.startTime = new Date();
    $scope.newBooking.endTime = new Date();



    //create new booking
    $scope.submitCreateBooking = function(){
        //convert date, time string to epoch time
        var startDateF = $filter('date')($scope.newBooking.startDate, "MM dd, yyyy");
        var startTimeF = $filter('date')($scope.newBooking.startTime,"HH:mm:ss")
        var endDateF = $filter('date')($scope.newBooking.endDate, "MM dd, yyyy");
        var endTimeF = $filter('date')($scope.newBooking.endTime,"HH:mm:ss")
        var booking = {
            "usersProjects" :  $scope.usersprojectsList[$scope.newBooking.project],
            "start" : new Date(startDateF + " " + startTimeF).getTime(),
            "end" : new Date(endDateF + " " + endTimeF).getTime()
        };
        $http.post(rootUrl + "/timetracker/booking", booking).success(
            function(answer, status) {
                $scope.createBookingResult = status;
                updateMyBookings();
            }).error(function(answer, status) {
                $scope.createBookingResult = status;
            });
    }
});