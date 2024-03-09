const mongoose = require('mongoose');
const { Schema } = mongoose;

const soundDataSchema = new Schema({
    date: {
        type:String,
    },
    values: [Number] 
});

const userAccountSchema = new Schema({
    id: String,
    username: String,
    password: String,
    soundData: [soundDataSchema]
})
module.exports = mongoose.model("Account", userAccountSchema);
