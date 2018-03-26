angular.module('googleocr', ['ionic','ngCordova']).controller('googleocrcontrol', function($scope,$http,$ionicActionSheet, $ionicLoading, $ionicPlatform, $cordovaCamera)
{

  var basevalue="";


  $scope.upload = function() {
    var options = {
      quality : 75,
      destinationType : Camera.DestinationType.DATA_URL,
      sourceType : Camera.PictureSourceType.CAMERA,
      allowEdit : true,
      encodingType: Camera.EncodingType.JPEG,
      popoverOptions: CameraPopoverOptions,
      targetWidth: 300,
      targetHeight: 300,
      saveToPhotoAlbum: false
    };
    $cordovaCamera.getPicture(options).then(function(imageData) {
      var image = document.getElementById('capture');
      image.src = "data:image/jpeg;base64," + imageData;
      basevalue = imageData;
      alert("Image has been uploaded");
      document.getElementById("Analyse").style.removeProperty("display");
      document.getElementById("capture").style.removeProperty("display");

    }, function(error) {
      console.error(error);
      alert(error);
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
      targetWidth: 300,
      targetHeight: 300,
      saveToPhotoAlbum: false
    };
    $cordovaCamera.getPicture(options).then(function(imageData) {
      var image = document.getElementById('capture');
      image.src = "data:image/jpeg;base64," + imageData;
      alert("Image has been uploaded");
      basevalue = imageData;
      document.getElementById("Analyse").style.removeProperty("display");
      document.getElementById("capture").style.removeProperty("display");

    }, function(error) {
      console.error(error);
    });
  }


  $scope.GetText = function(){

    var appkey = "AIzaSyAAK1nhtcsaMro-SmeTiyCO7-QyjyHLGfU";
    var appurl = "https://vision.googleapis.com/v1/images:annotate?key="+appkey;
    var request = {
      "requests": [
        {
          "image": {
            "content": basevalue
          },
          "features": [
            {
              "type": "DOCUMENT_TEXT_DETECTION"
            }
          ]
        }
      ]
    };
    $http({
      url: appurl,
      method: 'POST',
      data: request,
      contentType: 'application/json'

    }).success(function(data){
      document.getElementById("sourcetext").style.removeProperty("display");
      converted_text = data.responses[0].fullTextAnnotation.text
      document.getElementById("sourcetext").innerHTML = data.responses[0].fullTextAnnotation.text;
      alert(data.responses[0].fullTextAnnotation.text);

      }).error(function(){
      alert("error");
    });
  }




});
