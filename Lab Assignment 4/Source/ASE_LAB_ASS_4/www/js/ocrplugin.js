
angular.module('starter', ['ionic','ngCordova']).controller('CaptureCtrl', function($scope, $ionicActionSheet, $ionicLoading, $ionicPlatform, $cordovaCamera) {

  $scope.testOcrad = function(){

      OCRAD(document.getElementById("pic"), function(text){

        console.log(text);
        alert(text);
      });
    }

  $scope.upload = function() {
    var options = {
      quality : 75,
      destinationType : Camera.DestinationType.DATA_URL,
      sourceType : Camera.PictureSourceType.CAMERA,
      allowEdit : true,
      encodingType: Camera.EncodingType.JPEG,
      popoverOptions: CameraPopoverOptions,
      targetWidth: 500,
      targetHeight: 500,
      saveToPhotoAlbum: false
    };
    $cordovaCamera.getPicture(options).then(function(imageData) {
      var image = document.getElementById('pic');
      image.src = "data:image/jpeg;base64," + imageData;
        alert("Image has been uploaded");
      document.getElementById("Analyse").style.removeProperty("display");

    }, function(error) {
      console.error(error);
    });
  }

  $scope.choose = function() {
    var options = {
      quality : 75,
      destinationType : Camera.DestinationType.DATA_URL,
      sourceType : Camera.PictureSourceType.PHOTOLIBRARY,
      allowEdit : true,
      encodingType: Camera.EncodingType.JPEG,
      popoverOptions: CameraPopoverOptions,
      targetWidth: 500,
      targetHeight: 500,
      saveToPhotoAlbum: false
    };
    $cordovaCamera.getPicture(options).then(function(imageData) {
      var image = document.getElementById('pic');
      image.src = "data:image/jpeg;base64," + imageData;
      alert("Image has been uploaded");
      document.getElementById("Analyse").style.removeProperty("display");

    }, function(error) {
      console.error(error);
    });
  }




  });
