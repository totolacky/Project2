// Import package
var mongodb = require('mongodb')
var ObjectID = mongodb.ObjectID
var crypto = require('crypto')
var express = require('express')
var bodyParser = require('body-parser')
var SocketIO = require('socket.io')

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
        // // Start Web Server
        // var server = app.listen(80, ()=>{
        //     console.log('Connected to MongoDB Server , WebService running on port 80');
        // })

        var server = app.listen(80, ()=>{
            console.log('Connected to MongoDB Server , WebService running on port 80');
        })
        var io = require('socket.io').listen(server)

        //이 배열은 누가 chatroom에 있는지를 보여줍니다.
        var whoIsOn= [];

        //이 서버에서는 어떤 클라이언트가 connection event를 발생시키는 것인지 듣고 있습니다.
        // callback 으로 넘겨지는 socket에는 현재 클라이언트와 연결되어있는 socket 관련 정보들이 다 들어있습니다.
        io.sockets.on('connection',function (socket){

            console.log('connected!')

            var nickname = ``

            //일단 socket.on('login') 이라는 것은 클라이언트가 login 이라는 이벤트를 발생시키면
            //어떤 콜백 함수를 작동시킬 것인지 설정하는 것입니다.
            socket.on('login',function(data){
                console.log(`${data} has entered chatroom! ---------------------`)
                whoIsOn.push(data) //
                nickname = data

                // 아래와 같이 하면 그냥 String 으로 넘어가므로 쉽게 파싱을 할 수 있습니다.
                // 그냥 넘기면 JSONArray로 넘어가서 복잡해집니다.
                var whoIsOnJson = `${whoIsOn}`
                console.log(whoIsOnJson)

                //io.emit 과 socket.emit과 다른 점은 io는 서버에 연결된 모든 소켓에 보내는 것이고
                // socket.emit은 현재 그 소켓에만 보내는 것입니다.

                io.emit('newUser',whoIsOnJson)
            })

            socket.on('say',function(data){
                console.log(`${nickname} : ${data}`)



                socket.emit('myMsg',data)
                socket.broadcast.emit('newMsg',data) // socket.broadcast.emit은 현재 소켓이외의 서버에 연결된 모든 소켓에 보내는 것.
            })

            socket.on('disconnect',function(){
                console.log(`${nickname} has left this chatroom ------------------------  `)
            })

            socket.on('logout',function(){

                //Delete user in the whoIsOn Arryay
                whoIsOn.splice(whoIsOn.indexOf(nickname),1);
                var data = {
                    whoIsOn: whoIsOn,
                    disconnected : nickname
                }
                socket.emit('logout',data)
            })

        })

    }
})







