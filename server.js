// Import package
var mongodb = require('mongodb');
var socketio = require('socket.io');
//var ObjectID = mongodb.ObjectID;
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
        app.post('/registerUser', (request,response,next)=>{
            var post_data = request.body;

            var name = post_data.name;
            var id = post_data.id;

            var insertJson = {
                'id' : id,
                'name' : name
            };

            var db = client.db('penstagram');

            // check exists email
            db.collection('user').find({'id':id}).count(function(err,number){
                if(number!=0){
                    response.json('You already have the account');
                    console.log('You already have the account');
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

            var id = post_data.id;

            var db = client.db('penstagram');

            // check exists id
            db.collection('user').find({'id':id}).count(function(err,number){
                if(number==0){
                    response.json('Matching account not exists');
                    console.log('Matching account not exists');
                }
                else{
                    db.collection('user').findOne({'id':id},function(err,user){
                        response.json({'facebookId':id});
                        console.log('Login success');
                    })
                }
            })
        });

        // app.post('/initGallery', (request,response,next)=>{
        //     var post_data = request.body;

        //     var id = post_data.id;

        //     var db = client.db('penstagram');

        //     // Pair<Bitmap(String),ContactData>
        //     // user 랜덤 선택 (id ㄴㄴ) -> photo 랜덤 선택
        //     var query = {'id':{$ne:id}};
        //     var totalCnt = db.collection('user').count();
        //     var skipSize = Math.floor(Math.random()*totalCnt);
        //     var selectUser = db.collection('user').find(query).skip(skipSize).limit();

        //     var projection = {'photos':1,'_id':0};
        //     var photoCnt = selectUser.find(projection).body().count();
        //     var randNum = Math.floor(Math.random()*photoCnt);
        //     var selectedPhoto = selectUser.find(projection).body()[randNum];

        //     var userContactData = {selectedUser}

        //     response.json({Pair(selectedPhoto, userContactData)});
        //     console.log('Send data to init gallery success');

        // });

        app.post('/register', (request,response,next)=>{
            var post_data = request.body;

            var insertJson = {
                'facebookId': post_data.facebookId,
                'name': post_data.name,
                'status': post_data.status,
                'country_code': post_data.country_cod,
                'profile_photo': post_data.profile_photo,
                'photos': post_data.photos,
                'friends': post_data.friends,
                'hashtag': post_data.hashtag
            };

            var db = client.db('penstagram');

            // check exists email
            db.collection('user').find({'facebookId':post_data.facebookId}).count(function(err,number){
                if(number!=0){
                    response.json('You already have the account');
                    console.log('You already have the account');
                }
                else{
                    // insert data
                    db.collection('user').insertOne(insertJson,function(error,res){
                        console.log(res.ops[0].name)
                        response.json(res.ops[0]._id);
                        console.log('Registration success');
                    })
                }
            })
        });

        app.post('/checkRegistered', (request,response,next)=>{
            var facebookId = request.body.facebookId;

            var db = client.db('penstagram');

            // check exists email
            db.collection('user').find({'facebookId':facebookId}).count(function(err,number){
                if(number!=0){
                    // User is registered
                    db.collection('user').findOne({},function(error,res){
                        console.log(res._id)
                        response.json(res._id)
                        console.log('You have an account. Your id is '+res._id);
                    })
                }
                else{
                    // User is not registered
                    response.json('not registered');
                    console.log('You do not have an account('+facebookId+': false)');
                }
            })
        });


        // Start Web Server
        var server = app.listen(80, ()=>{
            console.log('Connected to MongoDB Server , WebService running on port 80');
        })

        // 소켓 서버를 생성한다.
        var io = socketio.listen(server);
        io.sockets.on('connection', function (socket){
            console.log('Socket ID : ' + socket.id + ', Connect');
            socket.on('clientMessage', function(data){
                console.log('Client Message : ' + data);

                var message = {
                    msg : 'server',
                    data : 'data'
                };
                socket.emit('serverMessage', message);
            });
        });
    }
})







