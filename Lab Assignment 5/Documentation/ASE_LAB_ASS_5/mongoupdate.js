/**
 * Created by user on 23/10/2016.
 */
var MongoClient = require('mongodb').MongoClient;
var assert = require('assert');
var bodyParser = require("body-parser");
var express = require('express');
var cors = require('cors');
var app = express();
var url = 'mongodb://root:1234@ds227565.mlab.com:27565/aseproject';
app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
var name="";
app.post('/login', function (req, res) {
    name = req.body.email;
    //console.log(name);
})

app.post('/register', function (req, res) {
  MongoClient.connect(url, function(err, db) {
    if(err)
    {
      res.write("Failed, Error while connecting to Database");
      res.end();
    }
    insertDocument(db, req.body, function() {
      res.write("Successfully inserted");
      res.end();
    });
  });
})
var insertDocument = function(db, data, callback) {
  var dbo = db.db("aseproject");
  dbo.collection('users').insertOne( data, function(err, result) {
    if(err)
    {
      res.write("Registration Failed, Error While Registering");
      res.end();
    }
    console.log("Inserted following details into Mongo DB collection.");
    console.log("Email : "+data.email);
    console.log("Password : "+data.pwd);
    callback();
  });
};




app.post('/update', function (req, res) {
  MongoClient.connect(url, function(err, db) {
    if(err)
    {
      res.write("Failed, Error while connecting to Database");
      res.end();
    }
    updateDocument(db, req.body, function() {
      res.write("Successfully updated");
      res.end();
    });
  });
})
var updateDocument = function(db, data, callback) {
  var dbo = db.db("aseproject");
  var myquery = { email: name };
  dbo.collection('users').updateOne(myquery ,data, function(err, result) {
    if(err)
    {
      res.write("Registration Failed, Error While Registering");
      res.end();
    }
    console.log("Updated results into Mongo DB collection.");

    callback();
  });
};
var server = app.listen(8081,function () {
  var host = server.address().address
  var port = server.address().port

  console.log("Example app listening at http://%s:%s", host, port)
})
