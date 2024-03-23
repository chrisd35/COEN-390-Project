const mongoose = require('mongoose');
const { Schema } = mongoose;

const soundDataSchema = new Schema({
    date: {
        type:String,
    },
    CO2: [Number],
    VOC :[Number],
    SoundLevel:[Number],
});


const userAccountSchema = new Schema({
    id: String,
    username: String,
    password: String,
    Data: [soundDataSchema]
})
module.exports = mongoose.model("Account", userAccountSchema);
