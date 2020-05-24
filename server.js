const express = require('express');
const app = express();
const port = process.env.PORT || 5000;
var request = require('request');
app.use((req, res, next) => {
  res.header('Access-Control-Allow-Origin', '*');
  next();
});

// console.log that your server is up and running
app.listen(port, () => console.log(`Listening on port ${port}`));

app.get('/news_guardian', (req, res) => {
	var p1 = req.query.param1;
	var url_str = "";
	if (p1==="" || p1===null || p1 ==="home")
	{
		url_str = 'https://content.guardianapis.com/search?api-key=XXXX&section=(sport|business|technology|politics)&show-blocks=all';
	}
	else 
	{
		p1 = p1.replace("sports", "sport");
		url_str = 'https://content.guardianapis.com/' + p1 + '?api-key=XXXX&show-blocks=all';
	}
	request(url_str, function(error, response, body) {
       	var json = JSON.parse(body);
       	console.log(json);
    res.json(json) 
    });
});
app.get('/article_guardian', (req, res) => {
	var p1 = req.query.param1;
	var url_str = 'https://content.guardianapis.com/'+p1+'?api-key=XXXX&show-blocks=all';
	request(url_str, function(error, response, body) {
       	var json = JSON.parse(body);
    res.json(json) 
    });
});
app.get('/search_guardian', (req, res) => {
	var p1 = req.query.param1;
	var url_str = 'https://content.guardianapis.com/search?q=' + p1 + '&api-key=XXXX&show-blocks=all';
	request(url_str, function(error, response, body) {
       	var json = JSON.parse(body);
    res.json(json) 
    });
});
app.get('/latest_guardian', (req, res) => {
	var url_str = 'https://content.guardianapis.com/search?order-by=newest&show-fields=starRating,headline,thumbnail,short-url&api-key=XXXX';
	request(url_str, function(error, response, body) {
    var json = JSON.parse(body);
    res.json(json) 
    });
});
app.get('/world_guardian', (req, res) => {
	var url_str = 'http://content.guardianapis.com/world?api-key=XXXX&show-blocks=all';
	request(url_str, function(error, response, body) {
    var json = JSON.parse(body);
    res.json(json) 
    });
});
app.get('/technology_guardian', (req, res) => {
	var url_str = 'http://content.guardianapis.com/technology?api-key=XXXX&show-blocks=all';
	request(url_str, function(error, response, body) {
    var json = JSON.parse(body);
    res.json(json) 
    });
});
app.get('/science_guardian', (req, res) => {
	var url_str = 'http://content.guardianapis.com/science?api-key=XXXX&show-blocks=all';
	request(url_str, function(error, response, body) {
    var json = JSON.parse(body);
    res.json(json) 
    });
});
app.get('/business_guardian', (req, res) => {
	var url_str = 'http://content.guardianapis.com/business?api-key=XXXX&show-blocks=all';
	request(url_str, function(error, response, body) {
    var json = JSON.parse(body);
    res.json(json) 
    });
});
app.get('/politics_guardian', (req, res) => {
	var url_str = 'http://content.guardianapis.com/politics?api-key=XXXX&show-blocks=all';
	request(url_str, function(error, response, body) {
    var json = JSON.parse(body);
    res.json(json) 
    });
});
app.get('/sports_guardian', (req, res) => {
	var url_str = 'http://content.guardianapis.com/sport?api-key=XXXX&show-blocks=all';
	request(url_str, function(error, response, body) {
    var json = JSON.parse(body);
    res.json(json) 
    });
});
const googleTrends = require('google-trends-api');
app.get("/trends", function(req, res) {
	var p1 = req.query.param1;
  googleTrends.interestOverTime({keyword: p1, startTime: new Date('2019-06-01')}, function(err, results) {
    if (err) console.log('Trends error!', err);
    else {
    	var json = JSON.parse(results);
    res.json(json)
    }
  });
});

