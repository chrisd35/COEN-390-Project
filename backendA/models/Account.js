const mongoose = require('mongoose');
const { Schema } = mongoose;

const accessTimeSchema = new Schema({
    hour: Number,
    minute: Number,
    second: Number
});

const soundDataSchema = new Schema({
    date: {
        type:String,
    },
    CO2: [Number],
    VOC :[Number],
    SoundLevel:[Number],
    CO2AccessTime: [accessTimeSchema],  // Correct field name
    VOCAccessTime: [accessTimeSchema],  // Correct field name
    SoundAccessTime: [accessTimeSchema] // Correct syntax and field name
    
});


const userAccountSchema = new Schema({
    id: String,
    username: String,
    password: String,
    Data: [soundDataSchema],
    SoundThreshold:Number
})
module.exports = mongoose.model("Account", userAccountSchema);
