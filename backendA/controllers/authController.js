const Account = require("../models/Account");
const bcrypt = require('bcrypt');
const { v4: uuidv4 } = require('uuid');
const moment = require('moment');
const passport = require('passport');
const saltRounds = 10;
let userId;
// let userId="49ba77e3-4232-4d7d-8c14-8032f20d1a33";
let authenticationcheck;
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

        const userInfo = await newUser.save();
        console.log(userInfo)
        userId=userInfo.id
        authenticationcheck=true;
        return res.json({
            AccountCreated: true,
            Message: "Account Created",

        })

    }
    else {
        return res.json({
            AccountCreated: false,
            Message: "Account already exists",

        })
    }
}
exports.login = async (req, res, next) => {
    console.log("login api");
    passport.authenticate('local', (err, user, info) => {
        if (err) {
            console.error("Authentication error:", err.message);
            console.error(err.stack);
            return res.json({ message: "An error occurred during the authentication process", error: err });
        }
        if (!user) {
        
            // Authentication failed
            console.log("failed authentication");
            return res.json({ Authentication: req.isAuthenticated() , message: info.message });
        }
        // Manually establish the session
        req.login(user, (loginErr) => {
            if (loginErr) {
                console.log("Error");
                return res.json({ message: "Error establishing a session", error: loginErr });
            }
            // Successful authentication
            userId=user.id;
            authenticationcheck=true;
            console.log("SuccesfulAuthentication"+user.id);
            return res.json({ Authentication: authenticationcheck, message: "Logged in successfully" });
        });
    })(req, res, next);

    

}
exports.logOut = async(req, res, next)=>{
    req.logout(function (err) {

        if (err) { return next(err); }
        authenticationcheck=false;
        res.json({ Authentication: authenticationcheck,message: "Logged Out successfully"})
      }
      );


    
}
exports.authenticatedCheck=async(req, res, next)=>{

    console.log("Authentication Check "+req.isAuthenticated());
    res.json({ Authentication: authenticationcheck})

}

exports.getSoundData=async(req,res,next)=>{
    try {
    const {  date } = req.body; 
    const user = await Account.findOne({ id: userId});
        if (!user) {
            return res.status(404).json({ message: "User not found" });
        }
        const requestedDate =moment(new Date(date)).format("YYYY-MM-DD")

        const DataEntry = user.Data.find(entry => {
            const entryDate =moment(new Date(entry.date)).format("YYYY-MM-DD")
        
            console.log(entryDate+"  "+requestedDate)
            return entryDate === requestedDate;
        });

        if (DataEntry) {
            // If an entry exists for the requested date, return the values
            return res.json({ 
                message: "Sound data retrieved successfully",
                data: DataEntry.SoundLevel
            });
        } else {
            // If no entry for the requested date, return a message indicating no data was found
            
            return res.status(404).json({ message: "No sound data found for the specified date" });
        }
    }
    catch (error) {
        console.error("Error retrieving sound data:", error);
        return res.status(500).json({ message: "Error retrieving sound data", error: error.message });
    }

}

exports.AddSoundData=async(req, res, next)=>{
    try {
      
        const { values } = req.body;
        console.log(req.body);
        // Use the server's current date
        // const currentDate = new Date();
        // currentDate.setHours(0, 0, 0, 0);
        const currentDate =moment(new Date()).format("YYYY-MM-DD")
console.log(currentDate)
        const user = await Account.findOne({ id: userId });
        if (!user) {
            return res.json({ message: "User not found" });
        }

        // Try to find the sound data entry for the server's current date
        const soundDataEntry = user.Data.find(entry => moment(entry.date).format("YYYY-MM-DD") === currentDate);

        if (soundDataEntry) {
            // If an entry exists for today, update the values
            soundDataEntry.SoundLevel.push(...values);
        } else {
            // If no entry for today, add a new one with the current date
            user.Data.push({ date: currentDate, VOC: [], CO2: [], SoundLevel: values });
        }

        await user.save(); // Save the updated document
        return res.json({ message: "Data updated successfully" });
    } catch (error) {
        console.error("Error updating Data:", error);
        return res.json({ message: "Error updating Data", error: error.message });
    }
}
exports.AddVOCData = async (req, res, next) => {
    try {
        const { values } = req.body;
        const currentDate = moment(new Date()).format("YYYY-MM-DD");

        const user = await Account.findOne({ id: userId });
        if (!user) {
            return res.json({ message: "User not found" });
        }

        // Find the entry for the current date
        const dataEntry = user.Data.find(entry => moment(entry.date).format("YYYY-MM-DD") === currentDate);

        if (dataEntry) {
            // Update the existing entry
            dataEntry.VOC.push(...values);
        } else {
            // If no entry for today, add a new one
            user.Data.push({ date: currentDate, VOC: values, CO2: [], SoundLevel: [] });
        }

        await user.save(); // Save the updated document
        return res.json({ message: "VOC data updated successfully" });
    } catch (error) {
        console.error("Error updating VOC Data:", error);
        return res.json({ message: "Error updating VOC Data", error: error.message });
    }
};
exports.getVOCData=async(req,res,next)=>{
    try {
    const {  date } = req.body; 
    const user = await Account.findOne({ id: userId});
        if (!user) {
            return res.status(404).json({ message: "User not found" });
        }
        const requestedDate =moment(new Date(date)).format("YYYY-MM-DD")

        const DataEntry = user.Data.find(entry => {
            const entryDate =moment(new Date(entry.date)).format("YYYY-MM-DD")
        
            console.log(entryDate+"  "+requestedDate)
            return entryDate === requestedDate;
        });

        if (DataEntry) {
            // If an entry exists for the requested date, return the values
            return res.json({ 
                message: "VOC data retrieved successfully",
                data: DataEntry.VOC
            });
        } else {
            // If no entry for the requested date, return a message indicating no data was found
            
            return res.status(404).json({ message: "No VOC data found for the specified date" });
        }
    }
    catch (error) {
        console.error("Error retrieving VOC data:", error);
        return res.status(500).json({ message: "Error retrieving VOC data", error: error.message });
    }
}
exports.getCO2Data=async(req,res,next)=>{
    try {
    const {  date } = req.body; 
    const user = await Account.findOne({ id: userId});
        if (!user) {
            return res.status(404).json({ message: "User not found" });
        }
        const requestedDate =moment(new Date(date)).format("YYYY-MM-DD")
        
        const DataEntry = user.Data.find(entry => {
            const entryDate =moment(new Date(entry.date)).format("YYYY-MM-DD")
        
            console.log(entryDate+"  "+requestedDate)
            return entryDate === requestedDate;
        });

        if (DataEntry) {
            // If an entry exists for the requested date, return the values
            return res.json({ 
                message: "CO2 data retrieved successfully",
                data: DataEntry.CO2
            });
        } else {
            // If no entry for the requested date, return a message indicating no data was found
            
            return res.status(404).json({ message: "No CO2 data found for the specified date" });
        }
    }
    catch (error) {
        console.error("Error retrieving CO2 data:", error);
        return res.status(500).json({ message: "Error retrieving CO2 data", error: error.message });
    }
}
exports.AddCO2Data = async (req, res, next) => {
    try {
        const {values } = req.body;
        const currentDate = moment(new Date()).format("YYYY-MM-DD");

        const user = await Account.findOne({ id: userId });
        if (!user) {
            return res.json({ message: "User not found" });
        }

        // Find the entry for the current date
        const dataEntry = user.Data.find(entry => moment(entry.date).format("YYYY-MM-DD") === currentDate);

        if (dataEntry) {
            // Update the existing entry
            dataEntry.CO2.push(...values);
        } else {
            // If no entry for today, add a new one
            user.Data.push({ date: currentDate, VOC:[] , CO2: values, SoundLevel: [] });
        }

        await user.save(); // Save the updated document
        return res.json({ message: "CO2 data updated successfully" });
    } catch (error) {
        console.error("Error updating CO2 Data:", error);
        return res.json({ message: "Error updating VOC Data", error: error.message });
    }
};
