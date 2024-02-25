const mongoose = require('mongoose');
const { Schema } = mongoose;
const userAccountSchema = new Schema({
    id: String,
    username: String,
    password: String,
});
module.exports= mongoose.model("Account", userAccountSchema)