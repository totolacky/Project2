// Import package
var mongodb = require('mongodb');
var mongoose = require('mongoose');
var socketio = require('socket.io');
//var ObjectID = mongodb.ObjectID;
var express = require('express')
var bodyParser = require('body-parser')


// Create Express Service
var app = express();
app.use(bodyParser.json({limit:'50mb'}));
app.use(bodyParser.urlencoded({limit:'50mb',extended: true}));
app.use(express.json({ limit : '50mb' }));
app.use(express.urlencoded({ limit:'50mb', extended: true }));


// Create MongoDB Client
var MongoClient = mongodb.MongoClient;

// Connection URL
var url = 'mongodb://localhost:27017'

MongoClient.connect(url, {useUnifiedTopology: true}, function(err,client){
    if(err) console.log('Unable to connection to the mongoDB server.Error', err);
    else{        
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

        app.post('/initGallery', (request,response,next)=>{
            var post_data = request.body;

            var id = post_data.id;
            var idx = post_data.idx;

            var db = client.db('penstagram');

            // user 랜덤 선택 (id ㄴㄴ) -> photo 랜덤 선택
            //var query = {'_id':{$ne:id}};
            //var totalCnt = db.collection('user').find().count(function(err,number){

                //var skipSize = Math.floor(Math.random()*number);
                var selectedUser = db.collection('user').find({'_id':{$ne:id}}).toArray(function(err,selectedUser){

                    var userContactData = {
                        'facebookId': selectedUser[idx].facebookId,
                        'name': selectedUser[idx].name,
                        'status': selectedUser[idx].status,
                        'country_code': selectedUser[idx].country_code,
                        'profile_photo':selectedUser[idx].profile_photo,
                        'photos': selectedUser[idx].photos,
                        'friends': selectedUser[idx].friends,
                        'hashtag': selectedUser[idx].hashtag,
                    };
    
                    console.log(selectedUser[idx].photos)
                    console.log(userContactData.name)
    
                    var selectedPhoto=""
    
                    var photoArray = selectedUser[idx].photos;
                    if(photoArray != null){
                        //var randNum = Math.floor(Math.random()*photoArray.length);
                        selectedPhoto = photoArray[0] 
                        response.json({'selectedPhoto':selectedPhoto, 'userContactData':userContactData});
                        console.log('Send data to init gallery success'); 
                    }
                    else{
                        console.log('photoArray null');
                        response.json({'selectedPhoto':selectedPhoto, 'userContactData':userContactData});
                        console.log('Send data fail (selectedPhoto is null)'); 
                    }
 
                });


            //});

        });

        app.post('/register', (request,response,next)=>{
            var post_data = request.body;

            var facebookId = post_data.facebookId
            var name = post_data.name
            var status = post_data.status
            var country_code = post_data.country_code
            var profile_photo = post_data.profile_photo
            var photos = post_data.photos
            var friends = post_data.friends
            var hashtag = post_data.hashtag
            var chatroom = post_data.chatroom

            if(photos == null) { photos = [] }
            if(friends == null) { friends = [] }
            if(hashtag == null) { hashtag = [] }
            if(chatroom == null) { chatroom = [] }
            if(status == null) { status = "" }
            if(profile_photo == null) { profile_photo = "" }

            console.log(typeof photos)
            console.log(typeof friends)
            console.log(typeof hashtag)
            console.log(typeof chatroom)
            
            if(typeof photos == "string") {photos = [photos]}
            if(typeof friends == "string") {friends = [friends]}
            if(typeof hashtag == "string") {hashtag = [hashtag]}
            if(typeof chatroom == "string") {chatroom = [chatroom]}

            console.log(typeof photos)
            console.log(typeof friends)
            console.log(typeof hashtag)
            console.log(typeof chatroom)
        

            var insertJson = {
                'facebookId': facebookId,
                'name': name,
                'status': status,
                'country_code': country_code,
                'profile_photo': profile_photo,
                'photos': photos,
                'friends': friends,
                'hashtag': hashtag,
                'chatroom': chatroom
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

        app.post('/upload', (request,response,next)=>{
            var post_data = request.body;

            var id = post_data.id;
            var photo = post_data.photo;

            var db = client.db('penstagram');

            db.collection('user').find({'id':id},{'photos':1}).add(photo)
            response.json('add photo success');
            console.log('add photo success');
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

        app.post('/addFriendFb', (request,response,next)=>{
            var post_data = request.body;

            var myFbId = post_data.myFbId;
            var fbId = post_data.newFriendFbId;

            var db = client.db('penstagram');

            if (myFbId === fbId) {
                response.json('You cannot add yourself as a friend.')
                console.log('You cannot add yourself as a friend.')
            }
            else {
                db.collection('user').find({'facebookId':myFbId}).count(function(err,number){
                    if(number==0){
                        response.json('Matching account does not exist. Is this really your Id?');
                        console.log('Matching account does not exist. Is this really your Id?');
                    }
                    else{
                        db.collection('user').findOne({'facebookId':myFbId},function(err,user_me) {
                            db.collection('user').find({'facebookId':fbId}).count(function(err,n) {
                                if(n==0){
                                    response.json('Matching account does not exist. Is this an existing facebookId?');
                                    console.log('Matching account does not exist. Is this an existing facebookId?');
                                }
                                else{
                                    db.collection('user').findOne({'facebookId':fbId},function(err,user_friend){

                                        // You and your new friend are both registered
                                        console.log(user_me.friends)
                                        console.log(user_me._id)
                                        console.log(user_friend.friends)
                                        console.log(user_friend._id)
                                        var myFriends = user_me.friends
                                        var friendFriends = user_friend.friends

                                        if (myFriends.includes(user_friend._id.toString())) {
                                            response.json('You are already friends.')
                                            console.log('You are already friends.');
                                        }
                                        else {
                                            myFriends.push(""+user_friend._id)
                                            friendFriends.push(""+user_me._id)

                                            db.collection('user').updateOne({'facebookId':myFbId},{$set: {'friends':myFriends}},function(err,res){
                                                if(err) throw err
                                                console.log('updated')
                                            });
                                            db.collection('user').updateOne({'facebookId':fbId},{$set: {'friends':friendFriends}},function(err,res){
                                                if(err) throw err
                                                console.log('updated')
                                            });

                                            response.json('Success')
                                            console.log('Friend added: ' + user_me.name + ' and ' + user_friend.name);
                                        }
                                    });
                                }
                            });
                        });
                    }
                });
            }

        });

        app.post('/getFriends', (request,response,next)=>{
            var _id = request.body.id;

            var db = client.db('penstagram');

            // check exists email
            db.collection('user').find({'_id':mongoose.mongo.ObjectID(_id)}).count(function(err,number){
                if(number!=0){
                    // User is registered
                    db.collection('user').findOne({},function(error,res){
                        console.log(res.friends)
                        response.json(res.friends)
                        console.log('Friends sent.');
                    })
                }
                else{
                    // User is not registered
                    response.json('not registered');
                    console.log('You do not have an account('+_id+': false)');
                }
            })
        });

        app.post('/getContactSimple', (request,response,next)=>{
            console.log('aaaaaaaaaaa')

            var _id = request.body.id;
            console.log("getContactSimple called with id: "+_id)

            var db = client.db('penstagram');

            // check exists id
            db.collection('user').find({'_id':mongoose.mongo.ObjectID(_id)}).count(function(err,number){
                if(number!=0){
                    // User is registered
                    console.log('User is registered.');
                    db.collection('user').findOne({'_id':mongoose.mongo.ObjectID(_id)},function(error,res){
                        if(error) console.log(error)
                        else {
                            //console.log(res.friends)

                            console.log(res)
                            var resultJson = {
                                '_id': res._id,
                                'name': res.name,
                                'status': res.status,
                                'country_code': res.country_code,
                                'profile_photo': res.profile_photo
                            };

                            response.json(JSON.stringify(resultJson))
                            console.log(JSON.stringify(resultJson))
                            console.log('Friends sent.');
                        }
                    })
                }
                else{
                    // User is not registered
                    response.json('not registered');
                    console.log('You do not have an account('+_id+': false)');
                }
            })
        });

        // Start Web Server
        var server = app.listen(80, ()=>{
            console.log('Connected to MongoDB Server , WebService running on port 80');
        })

        // Set Socket Server
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







