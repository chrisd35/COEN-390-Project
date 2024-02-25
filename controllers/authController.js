const Account = require("../models/Account");
const bcrypt = require('bcrypt');
const { v4: uuidv4 } = require('uuid');
const passport = require('passport');
const saltRounds = 10;

exports.signup = async (req, res) => {
    const UserAccount = await Account.findOne({ username: req.body.username }).exec()

    if (!UserAccount) {
        const hashedPass = await bcrypt.hash(req.body.password, saltRounds)

        const newUser = Account(
            {
                id: uuidv4(),
                username: req.body.username,
                password: hashedPass,
            }
        )

        newUser.save();

        return res.json({
            AcountCreated: true,
            Message: "Account Created",

        })

    }
    else {
        return res.json({
            AcountCreated: false,
            Message: "Account already exists",

        })
    }
}
exports.login = async (req, res, next) => {
    passport.authenticate('local', (err, user, info) => {
        if (err) {
            console.error("Authentication error:", err.message);
            console.error(err.stack);
            return res.status(500).json({ message: "An error occurred during the authentication process", error: err });
        }
        if (!user) {
            // Authentication failed
            return res.status(401).json({ Authentication: req.isAuthenticated() , message: info.message });
        }
        // Manually establish the session
        req.login(user, (loginErr) => {
            if (loginErr) {
                return res.status(500).json({ message: "Error establishing a session", error: loginErr });
            }
            // Successful authentication
            return res.json({ Authentication: req.isAuthenticated(), message: "Logged in successfully" });
        });
    })(req, res, next);

    

}
exports.logOut = async(req, res, next)=>{
    req.logout(function (err) {

        if (err) { return next(err); }
        res.json({ Authentication: req.isAuthenticated(),message: "Logged Out successfully"})
      }
      );


}
exports.authenticatedCheck=async(req, res, next)=>{
    res.json({ Authentication: req.isAuthenticated()})

}