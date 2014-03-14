var cluster = require('cluster');

var workers = {};
if(cluster.isMaster){
    //var cpuCount = require('os').cpus().length/2;
    var cpuCount = 2;

    cluster.on('exit', function(worker){
        var originPID = worker.process.pid;

        delete workers[worker.process.pid];
        worker = cluster.fork();
        workers[worker.process.pid] = worker;

        console.log('Worker died! OriginId: ' + originPID + ', NewId: '+ worker.process.pid);
    });

    for(var i = 0; i < cpuCount; ++i){
        var worker = cluster.fork();
        workers[worker.process.pid] = worker;
        console.log('Worker ' + worker.process.pid + ' running!');
    }
}else{
    var app = require('./app');
    app.Run();
}

process.on('SIGTERM', function(){
    for(var pid in workers){
        process.kill(pid);
    }
    process.exit(0);
});