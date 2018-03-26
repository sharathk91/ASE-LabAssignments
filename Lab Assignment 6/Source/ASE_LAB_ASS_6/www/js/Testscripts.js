angular.module('Validlogin', ['ionic']).controller('Validatelogin', function ($scope,$http) {
  $scope.temp = "";

  $scope.Valid = function (username,password) {

    $scope.uname = username;
    $scope.pwd = password;

    if($scope.uname == '' && $scope.pwd == '' )
    {

      $scope.temp = "username and password can not be empty";
    }

    else if($scope.uname == '')
    {

      $scope.temp = "username can not be empty";
    }

   else
   {

     $scope.temp = "password can not be empty";
   }

  }
});
