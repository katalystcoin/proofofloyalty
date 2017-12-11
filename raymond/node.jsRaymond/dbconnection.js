var mysql = require('mysql');
var connection = mysql.createPool({
    host: '.eu',
    user: '',
    password: '',
    database: ''
});
module.exports = connection;