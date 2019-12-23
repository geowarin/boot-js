const Bundler = require('parcel');
const express = require('express');
const proxy = require('http-proxy-middleware');
const history = require('connect-history-api-fallback');

const bundler = new Bundler('index.html');
const app = express();

app.use(history());
app.use(proxy('/api', {target: 'http://localhost:8080', changeOrigin: true}));

app.use(bundler.middleware());

app.listen(3000, 'localhost', (err) => {
    if (err) {
        console.log(err);
        return;
    }

    console.log('Listening at http://localhost:3000');
});
