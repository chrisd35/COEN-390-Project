const LocalStrategy = require('passport-local').Strategy;
const Account= require("../models/Account")
const bcrypt = require('bcrypt');

const authUser = async (Username, password, done) => {
    
    try{
        const account= await Account.findOne({ username: Username }).exec()
        if (account) {
          bcrypt.compare(password, account.password).then(function (result) {
            if (result) {
              done(null, account )
  
            }
            else {
              done(null, false, { message: "Invalid password" })
  
            }
          })
        }
        else {
          done(null, false,{ message: "Invalid username" } )
        }
  
    }
      catch(error){
        done(error);
      }
       
       
       
  
    
  
  
  }
module.exports=(passport)=>{
    passport.serializeUser((user, done) => {
        done(null, user)
      })
      passport.deserializeUser((user, done) => {
        done(null, user)
      })
      passport.use(new LocalStrategy({
        usernameField: 'username',    
        passwordField: 'password'
      }, authUser))
}
