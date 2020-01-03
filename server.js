// Import package
var mongodb = require('mongodb');
var ObjectID = mongodb.ObjectID;
var crypto = require('crypto')
var express = require('express')
var bodyParser = require('body-parser')


// Create Express Service
var app = express();
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

// Create MongoDB Client
var MongoClient = mongodb.MongoClient;

// Connection URL
var url = 'mongodb://localhost:27017'

MongoClient.connect(url, {useUnifiedTopology: true}, function(err,client){
    if(err) console.log('Unable to connection to the mongoDB server.Error', err);
    else{
        // Register
        app.post('/register', (request,response,next)=>{
            var post_data = request.body;

            var name = post_data.name;
            var email = post_data.email;

            var insertJson = {
                'email' : email,
                'name' : name
            };

            var db = client.db('edmtdevnodejs');

            // check exists email
            db.collection('user').find({'email':email}).count(function(err,number){
                if(number!=0){
                    response.json('Email already exists');
                    console.log('Email already exists');
                }
                else{
                    // insert data
                    db.collection('user').insertOne(insertJson,function(error,res){
                        response.json('Registration success');
                        console.log('Registration success');
                    })
                }
            })
        });

        app.post('/login', (request,response,next)=>{
            var post_data = request.body;

            var email = post_data.email;

            var db = client.db('edmtdevnodejs');

            // check exists email
            db.collection('user').find({'email':email}).count(function(err,number){
                if(number==0){
                    response.json('Email not exists');
                    console.log('Email not exists');
                }
                else{
                    // insert data
                    db.collection('user').findOne({'email':email},function(err,user){
                        response.json('Login success');
                        console.log('Login success');
                    })
                }
            })
        });

        // Start Web Server
        app.listen(80, ()=>{
            console.log('Connected to MongoDB Server , WebService running on port 80');
        })
    }
})







