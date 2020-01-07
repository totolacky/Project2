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

// Connected Sockets
var clients = []

MongoClient.connect(url, {useUnifiedTopology: true}, function(err,client){
    if(err) console.log('Unable to connection to the mongoDB server.Error', err);
    else{
        // HTTP requests

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

        app.post('/userNumber', (request,response,next)=>{

            var db = client.db('penstagram');

            // check exists id
            db.collection('user').find().count(function(err,number){
                response.json(number)
                console.log('number:'+number)
            })
        });

        app.post('/getGalleryItem', (request,response,next)=>{
            var post_data = request.body;

            var id = post_data.id;
            var idx = post_data.idx;

            var db = client.db('penstagram');

            // user 랜덤 선택 (id ㄴㄴ) -> photo 랜덤 선택
            //var query = {'_id':{$ne:id}};
            //var totalCnt = db.collection('user').find().count(function(err,number){

                //var skipSize = Math.floor(Math.random()*number);
            var selectedUser = db.collection('user').find().toArray(function(err,selectedUser){

                var userContactData = {
                    '_id': selectedUser[idx]._id,
                    'facebookId': selectedUser[idx].facebookId,
                    'name': selectedUser[idx].name,
                    'status': selectedUser[idx].status,
                    'country_code': selectedUser[idx].country_code,
                    'profile_photo':selectedUser[idx].profile_photo,
                    'photos': selectedUser[idx].photos,
                    'friends': selectedUser[idx].friends,
                    'hashtag': selectedUser[idx].hashtag,
                };
                // console.log(userContactData._id)
                // console.log(id)
                // console.log(userContactData.name)

                var selectedPhoto=""

                if(id === userContactData._id){
                    console.log('same id (cannot get my own photo)')
                }
                else{
                    var photoArray = selectedUser[idx].photos;
                    if(photoArray != []){
                        //var randNum = Math.floor(Math.random()*photoArray.length);
                        selectedPhoto = photoArray[0]
                        console.log('Send data to init gallery success');
                    }
                    else{
                        console.log('photoArray null');
                    }
                }
                response.json({'selectedPhoto':selectedPhoto, 'userContactData':userContactData});

            });
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
                'chatroom': chatroom,
                'chat_people': []
            };

            var db = client.db('penstagram');

            // check exists email
            db.collection('user').find({'facebookId':post_data.facebookId}).count(function(err,number){
                if(number!=0){
                    response.json('You already have an account');
                    console.log('You already have an account');
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

            db.collection('user').findOne({'_id':mongoose.mongo.ObjectID(id)},function(err,user_me){
                console.log(user_me.name)
                console.log(user_me.photos)
                var myPhotos = user_me.photos
                myPhotos.push(""+photo)

                db.collection('user').updateOne({'_id':mongoose.mongo.ObjectID(id)},{$set:{'photos':myPhotos}},function(err,res){
                    //if(err) throw err
                   // console.log('updated')

                    response.json('add photo success');
                    console.log('add photo success');
                });
            });

        });

        app.post('/checkRegistered', (request,response,next)=>{
            var facebookId = request.body.facebookId;

            var db = client.db('penstagram');

            // check exists email
            db.collection('user').find({'facebookId':facebookId}).count(function(err,number){
                if(number!=0){
                    // User is registered
                    db.collection('user').findOne({'facebookId':facebookId},function(error,res){
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

        app.post('/addFriend', (request,response,next)=>{
            var post_data = request.body;

            var myId = post_data.myId;
            var Id = post_data.newFriendId;

            var db = client.db('penstagram');

            if (myId === Id) {
                response.json('You cannot add yourself as a friend.')
                console.log('You cannot add yourself as a friend.')
            }
            else {
                db.collection('user').find({'_id':mongoose.mongo.ObjectID(myId)}).count(function(err,number){
                    if(number==0){
                        response.json('Matching account does not exist. Is this really your Id?');
                        console.log('Matching account does not exist. Is this really your Id?');
                    }
                    else{
                        db.collection('user').findOne({'_id':mongoose.mongo.ObjectID(myId)},function(err,user_me) {
                            console.log('???????????????'+Id)
                            db.collection('user').find({'_id':mongoose.mongo.ObjectID(Id)}).count(function(err,n) {
                                if(n==0){
                                    response.json('Matching account does not exist. Is this an existing facebookId?');
                                    console.log('Matching account does not exist. Is this an existing facebookId?');
                                }
                                else{
                                    db.collection('user').findOne({'_id':mongoose.mongo.ObjectID(Id)},function(err,user_friend){

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

                                            db.collection('user').updateOne({'_id':mongoose.mongo.ObjectID(myId)},{$set: {'friends':myFriends}},function(err,res){
                                                if(err) throw err
                                                console.log('updated')
                                            });
                                            db.collection('user').updateOne({'_id':mongoose.mongo.ObjectID(Id)},{$set: {'friends':friendFriends}},function(err,res){
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
                    db.collection('user').findOne({'_id':mongoose.mongo.ObjectID(_id)},function(error,res){
                        //console.log(res)
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
            //console.log('aaaaaaaaaaa')

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

                            //console.log(res)
                            var resultJson = {
                                '_id': res._id,
                                'name': res.name,
                                'status': res.status,
                                'country_code': res.country_code,
                                'profile_photo': res.profile_photo
                            };

                            response.json(JSON.stringify(resultJson))
                            //console.log(JSON.stringify(resultJson))
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

        app.post('/getContactNum', (request,response,next)=>{
            var _id = request.body.id;

            var db = client.db('penstagram');

            // check exists email
            db.collection('user').find({'_id':mongoose.mongo.ObjectID(_id)}).count(function(err,number){
                if(number!=0){
                    // User is registered
                    db.collection('user').findOne({'_id':mongoose.mongo.ObjectID(_id)},function(error,res){
                        //console.log(res)
                        response.json(res.friends.length)
                        console.log('Friend number sent.');
                    })
                }
                else{
                    // User is not registered
                    response.json('not registered');
                    console.log('You do not have an account('+_id+': false)');
                }
            })
        });

        app.post('/getChatrooms', (request,response,next)=>{
            var _id = request.body.id;

            var db = client.db('penstagram');

            // check exists email
            db.collection('user').find({'_id':mongoose.mongo.ObjectID(_id)}).count(function(err,number){
                if(number!=0){
                    // User is registered
                    db.collection('user').findOne({'_id':mongoose.mongo.ObjectID(_id)},function(error,res){
                        //console.log(res.chatroom)
                        response.json(res.chatroom)
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

        app.post('/getChatroom', (request,response,next)=>{

            var chatroom_id = request.body.id;
            console.log("getChatroom called with id: "+chatroom_id)

            var db = client.db('penstagram');

            // check exists id
            db.collection('chatroom').find({'_id':mongoose.mongo.ObjectID(chatroom_id)}).count(function(err,number){
                if(number!=0){
                    // Chatroom exists
                    console.log('Chatroom exists.');
                    db.collection('chatroom').findOne({'_id':mongoose.mongo.ObjectID(chatroom_id)},function(error,res){
                        if(error) console.log(error)
                        else {
                            //console.log(res.friends)

                            //console.log(res)
                            var resultJson = {
                                'chatroom_id': res.chatroom_id,
                                'chatroom_name': res.chatroom_name,
                                'last_chat': res.last_chat,
                                'chatroom_image': res.chatroom_image,
                                'people': res.people,
                                'chat':res.chat
                            };

                            response.json(JSON.stringify(resultJson))
                            //console.log(JSON.stringify(resultJson))
                            console.log('Chatroom sent.');
                        }
                    })
                }
                else{
                    // Chatroom does not exist
                    response.json('no such chatroom');
                    console.log('no such chatroom');
                }
            })
        });

        app.post('/createChatroom', (request,response,next)=>{
            var myId = request.body.myId;
            var yourId = request.body.yourId;

            var db = client.db('penstagram');

            // Check if both users exist
            db.collection('user').findOne({'_id':mongoose.mongo.ObjectID(myId)}, function(error,user_me){
                if (err) console.log('error in createChatroom: no user_me')
                else {
                    db.collection('user').findOne({'_id':mongoose.mongo.ObjectID(yourId)}, function(error,user_you){
                        if (err) console.log('error in createChatroom: no user_you')
                        else {
                            // When both users exist
                            if(user_me.chat_people.includes(yourId)) {
                                var index= user_me.chat_people.indexOf(yourId)
                                var chatroomId = user_me.chatroom[index]
                                response.json(chatroomId)
                                console.log('Chatroom exists. Responded with chatroom id '+chatroomId)
                            } else {
                                var insertJson = {
                                    'chatroom_name': "",
                                    'last_chat': "",
                                    'chatroom_image':"",
                                    'people':[myId,yourId],
                                    'chat':[]
                                };
                                db.collection('chatroom').insertOne(insertJson,function(error,res){
                                    // When both users exist
                                    console.log("add chatroom: "+user_me.name+" and "+user_you.name)

                                    var chatroomId = res.ops[0]._id

                                    user_me.chatroom.push(chatroomId.toString())
                                    user_you.chatroom.push(chatroomId.toString())

                                    user_me.chat_people.push(user_you._id.toString())
                                    user_you.chat_people.push(user_me._id.toString())

                                    // Update DB
                                    db.collection('user').updateOne({'_id':mongoose.mongo.ObjectID(myId)},{$set: {'chatroom':user_me.chatroom, 'chat_people':user_me.chat_people}},function(err,res){
                                        if(err) throw err
                                        console.log('my DB updated')
                                    });

                                    db.collection('user').updateOne({'_id':mongoose.mongo.ObjectID(yourId)},{$set: {'chatroom':user_you.chatroom, 'chat_people':user_you.chat_people}},function(err,res){
                                        if(err) throw err
                                        console.log('my DB updated')
                                    });

                                    response.json(chatroomId);
                                    console.log('Chatroom created with id: '+chatroomId);
                                })
                            }
                        }
                    })
                }
            })
        });

        app.post('/getChatLog', (request,response,next)=>{

            var chatroom_id = request.body.id;
            console.log("getChatroom called with id: "+chatroom_id)

            var db = client.db('penstagram');

            // check exists id
            db.collection('chatroom').findOne({'_id':mongoose.mongo.ObjectID(chatroom_id)}, function(err,res){
                if(err) {
                    console.log("error: no such chatroom exists")
                    response.json('chatroom does not exist')
                }
                else {
                    // Chatroom exists
                    console.log('Chatroom exists.');
                    console.log(res.chat.length)

                    // Stringify
                    var result = []
                    for (i=0; i<res.chat.length; i++) {
                        //console.log(JSON.stringify(res.chat[i]))
                        result.push(JSON.stringify(res.chat[i]))
                    }

                    //console.log(result)
                    response.json(result)
                    console.log('Chat log sent.')
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

            var myId;
            var chatroomId;
            var sendList;

            // Link socket event handlers
            socket.on('clientConnect',function(data){
                var thisClient = new Object()
                thisClient.myId = data.myId
                thisClient.chatroomId = data.chatroomId
                thisClient.people = data.people
                thisClient.socketId = socket.id
                clients.push(thisClient)

                myId = data.myId
                chatroomId = data.chatroomId
                sendList = data.people

                //console.log("client connected with sendList: "+sendList)
            })

            socket.on('disconnect',function(){
                console.log('Socket ID : ' + socket.id + ', Disconnect')

                for (i=0; i<clients.length; i++) {
                    if (clients[i].myId === myId) {
                        clients.splice(i,1)
                        break
                    }
                }

                console.log('current client list: '+clients)

            })

            socket.on('clientMessage', function(data){
                console.log('Client Message : ' + JSON.stringify(data));

                var sendData = {
                    'id': myId,
                    'script': data.script,
                    'date_time': data.date_time
                }

                // Send message to other clients

                //console.log(sendList)
                console.log(clients)

                for (j=0; j<sendList.length; j++) {
                    sendId = sendList[j]
                    console.log('sendId: '+sendId)
                    if (sendId === myId) {
                        console.log('No need to send to yourself.')
                        continue
                    }

                    var obj = null

                    for (i=0; i<clients.length; i++) {
                        if (clients[i].myId === sendId) {
                            obj = clients[i]
                            break
                        }
                    }

                    if (obj == null) {
                        console.log('the socket you are looking for is not in the client list')
                        continue
                    }
                    var sendId = obj.socketId

                    io.to(sendId).emit('serverMessage',sendData)
                    console.log('New chat sent to another user.')
                }

                var db = client.db('penstagram');

                // Save message in DB
                db.collection('chatroom').findOne({'_id': mongoose.mongo.ObjectID(chatroomId)},function(err,res){
                    if (err) console.log("Error: chatroom does not exist")
                    else {
                        var chat = res.chat
                        chat.push(sendData)
                        db.collection('chatroom').updateOne({'_id': mongoose.mongo.ObjectID(chatroomId)},{$set: {'chat':chat, 'last_chat':data.script}},function(err,res){
                            if(err) throw err
                            console.log('New chat updated')
                        });
                    }
                })
            });
        });
    }
})
