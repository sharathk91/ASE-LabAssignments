
var app = angular.module('myhome',['naif.base64']);
var predicted_text = "";
app.run(function ($http) {
  // Sends this header with any AJAX request
  $http.defaults.headers.common['Access-Control-Allow-Origin'] = '*';
  // Send this header only in post requests. Specifies you are sending a JSON object
  $http.defaults.headers.post['dataType'] = 'json'
});

app.controller('home',function($scope,$http,$window){


  $scope.imageUpload = function(element){
    var reader = new FileReader();
    reader.onload = $scope.imageIsLoaded;
    reader.readAsDataURL(element.files[0]);
  }

  $scope.imageIsLoaded = function(e){
    $scope.$apply(function() {

      document.getElementById("img").src = e.target.result;
      document.getElementById("img").style.removeProperty("display");
    });
  }

  $scope.faceUpload = function(element){
    var reader = new FileReader();
    reader.onload = $scope.faceIsLoaded;
    reader.readAsDataURL(element.files[0]);
  }

  $scope.faceIsLoaded = function(e){
    $scope.$apply(function() {

      document.getElementById("face").src = e.target.result;
      document.getElementById("face").style.removeProperty("display");
    });
  }



  $scope.upload = function(file) {

    var fname = file.base64;
    var itemList = "";
    const app = new Clarifai.App({apiKey: 'c180af2d90164e15b1170fc75f85af25'});
    app.models.predict(Clarifai.GENERAL_MODEL, {base64: fname}).then(

      function(response) {
        console.log(response);
        console.log(response.outputs[0].data.concepts[0].name);
        console.log(response.outputs[0].data.concepts.length);

        for (var i = 0; i < 10;i++) {
           var temp = response.outputs[0].data.concepts[i].name;
           itemList = itemList+ temp +"<br/>";
          predicted_text = predicted_text + " "+ temp;

        }
        document.getElementById("notes").innerHTML =itemList;

        $scope.dataparam = { $set: { 'API' : "Clarifai", 'Predicted_Text': predicted_text }};
        var config = {
          headers : {
            'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8;'
          }
        }
        var req = $http.post('http://127.0.0.1:8081/update',$scope.dataparam);
        req.success(function(data, status, headers, config) {
          $scope.message = data;
          console.log(data);
        });
        req.error(function(data, status, headers, config) {
          alert( "failure message: " + JSON.stringify({data: data}));
        });

        alert("The following details are updated in Mongo DB");
      }

    );

  }

});
