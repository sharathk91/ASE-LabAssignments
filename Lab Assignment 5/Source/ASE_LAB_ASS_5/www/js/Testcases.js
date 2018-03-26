 describe("Validatelogin", function() {
   var scope;

   beforeEach(angular.mock.module("Validlogin"));
   beforeEach(angular.mock.inject(function($rootScope, $controller) {
     scope = $rootScope.$new();
     $controller('Validatelogin', {$scope: scope});
   }));

   it("check if username and password is empty", function() {

     scope.Valid('','');
     expect(scope.temp).toEqual("username and password can not be empty");
   });


   it("check if username is empty", function() {

     scope.Valid('','sharath');
     expect(scope.temp).toEqual("username can not be empty");
   });

   it("check if  password is empty", function() {

     scope.Valid('sharath','');
     expect(scope.temp).toEqual("password can not be empty");
   });

});



