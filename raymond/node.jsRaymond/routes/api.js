var express = require('express');
var Base58 = require('base-58');
var Task = require('../models/Task');
var textEncoding = require('text-encoding');
var TextDecoder = textEncoding.TextDecoder;
var router = express.Router();
var api = 'http://localhost.eu:6869/';
var assetId = null;
var amount = 1;
var recipient = '3PBA4kzzWgzRCbRZhKaYk6ihrWJdKmEYnEi';



router.get('/height', function (req, res) {
    Task.getHeight(function (err, rows) {
        if (err) {
            res.json(err);
        } else {
            res.json(rows);
        }

    })
});

router.post('/', function (req, res) {
    var request = require('request');
    request(api + 'transactions/info/' + req.body.tx, function (error, response, body) {
        var objBody = JSON.parse(body);
        if (!error && response.statusCode == 200) {
            if (objBody.type == 4 && objBody.assetId === assetId) {
                if (objBody.amount < amount) {
                    console.log({"error": "Please increase fee."});

                    res.json({"error": "Please increase fee."});
                } else {
                    if (objBody.recipient == recipient || req.body.type == 5) {
                        Task.checkIfUnique(req.body, function (err, count) {
                            if (err) {
                                res.json(err);
                            } else {
                                if (count[0].count == 0) {
                                    var stringAttach = Base58.decode(objBody.attachment);
                                    var string = new TextDecoder("utf-8").decode(stringAttach);
                                    if (string === req.body.hash) {
                                        request.post({
                                            url: api + 'utils/hash/secure',
                                            body: req.body.message
                                        }, function (error, response, body) {
                                            var objBody2 = JSON.parse(body);
                                            if (objBody2.hash === req.body.hash && string === objBody2.hash) {
                                                Task.addItem(req.body, function (err) {
                                                    if (err) {

                                                        res.json(err);
                                                    } else {

                                                        res.json(req.body);
                                                    }
                                                });
                                            } else {
                                                console.log({"error": "Hashes are wrong calculated."});

                                                res.json({"error": "Hashes are wrong calculated."});
                                            }
                                        });

                                    } else {
                                        console.log({"error": "Hashes are wrong."});

                                        res.json({"error": "Hashes are wrong."});
                                    }

                                } else {
                                    console.log({"error": "Tx already used."});

                                    res.json({"error": "Tx already used."});
                                }

                            }
                        });
                    } else {
                        console.log({"error": "Tx has a wrong recipient."});

                        res.json({"error": "Tx has a wrong recipient."});
                    }

                }

            } else {
                console.log({"error": "Not the correct asset was used."});
                res.json({"error": "Not the correct asset was used."});
            }
        }

        if (objBody.status === "error") {
            console.log(objBody);
            res.json(objBody);
        }


    });

});


module.exports = router;
