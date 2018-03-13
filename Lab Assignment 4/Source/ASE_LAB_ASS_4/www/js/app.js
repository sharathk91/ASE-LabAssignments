var loginapp=angular.module("login", ["ionic","ngCordova","firebase"]);

loginapp.controller("login", function($scope, $state, $firebaseAuth,$window) {

    var fbAuth = $firebaseAuth();

    $scope.login = function(username, password) {

        fbAuth.$signInWithEmailAndPassword(username,password).then(function(authData) {

          $window.location.href = "Home.html";
        }).catch(function(error) {
            console.error("ERROR: " + error);
        });

    }

    $scope.register = function(username, password,$window) {
        fbAuth.$createUserWithEmailAndPassword(username,password).then(function(userData) {
            return fbAuth.$signInWithEmailAndPassword(username,password);
        }).then(function(authData) {

          $window.location.href ="Home.html";
        }).catch(function(error) {
            console.error("ERROR: " + error);
        });
    }

});




