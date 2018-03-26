var loginapp=angular.module("login", ["ionic","ngCordova","firebase"]);

loginapp.run(function ($http) {
  // Sends this header with any AJAX request
  $http.defaults.headers.common['Access-Control-Allow-Origin'] = '*';
  // Send this header only in post requests. Specifies you are sending a JSON object
  $http.defaults.headers.post['dataType'] = 'json'
});

loginapp.controller("login", function($scope, $state, $firebaseAuth,$window,$http) {

    var fbAuth = $firebaseAuth();

    $scope.login = function(username, password) {

        fbAuth.$signInWithEmailAndPassword(username,password).then(function(authData) {

          $window.location.href = "Home.html";
        }).catch(function(error) {
            alert("Invalid Credentials");
            console.error("ERROR: " + error);
        });

      $scope.dataname = {
        'email' : username
      };
      var config = {
        headers : {
          'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8;'
        }
      }
      var req = $http.post('http://127.0.0.1:8081/login',$scope.dataname);

    }

    $scope.register = function(username, password) {
        fbAuth.$createUserWithEmailAndPassword(username,password).then(function(userData) {

          alert("You Are Now Registered with Mongo DB");
        }).catch(function(error) {
            console.error("ERROR: " + error);
        });

      $scope.dataParams = {
        'email' : username,
        'pwd' : password
      };
      var config = {
        headers : {
          'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8;'
        }
      }
      var req = $http.post('http://127.0.0.1:8081/register',$scope.dataParams);
      req.success(function(data, status, headers, config) {
        $scope.message = data;
        console.log(data);
      });
      req.error(function(data, status, headers, config) {
        alert( "failure message: " + JSON.stringify({data: data}));
      });

    }

});




