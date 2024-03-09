const authRoutes = require('./routes/authRoutes');
const DatabaseConnect= require('./config/database');
const bodyParser = require("body-parser")
const express = require("express")
const passport = require('passport');
require('./config/passport.js')(passport);

const session = require('express-session');



const app = express()
app.use(session({
    secret: 'secret', 
    resave: true,
    saveUninitialized: true,

}));
app.use(passport.initialize())
app.use(passport.session());

app.use(bodyParser.urlencoded({ extended: true }))
app.use(bodyParser.json());

PORT = process.env.PORT || 3000


app.use('/', authRoutes); 


DatabaseConnect().then
    (app.listen(PORT, () => {
        console.log(`Working in port ${PORT}`)
    }))

