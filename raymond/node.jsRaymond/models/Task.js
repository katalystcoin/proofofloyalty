var db = require('../dbconnection'); //reference of dbconnection.js
var Task = {
    getHeight: function(callback) {
        return db.query("Select COUNT(*) from lpos", callback);
    },
    addItem: function(Task, callback) {
        return db.query("Insert into lpos (hash,message,tx,type)values(?,?,?,?)", [Task.hash, Task.message, Task.tx, Task.type], callback);
    },
    checkIfUnique: function (Task, callback) {
        return db.query("Select COUNT(*) AS count from lpos where tx = ?",[Task.tx], callback);
    }
};
module.exports = Task;