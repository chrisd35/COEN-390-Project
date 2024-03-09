require('dotenv').config();
const mongoose = require('mongoose');
const DatabaseConnect = async () => {
    try {
        const conn = await mongoose.connect(process.env.MONGODBBKEY);
        console.log(`MongoDB Connected: ${conn.connection.host}`)


    } catch (error) {
        console.log(error)
        process.exit(1)
    }
}
module.exports = DatabaseConnect;