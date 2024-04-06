const Account = require("../models/Account");
const bcrypt = require('bcrypt');
const { v4: uuidv4 } = require('uuid');
const moment = require('moment');
const passport = require('passport');
const saltRounds = 10;
let userId;

let authenticationcheck;
exports.signup = async (req, res) => {
    const UserAccount = await Account.findOne({ username: req.body.username }).exec()
    if (!UserAccount) {
        const hashedPass = await bcrypt.hash(req.body.password, saltRounds)

        const newUser = new Account({
            id: uuidv4(),
            username: req.body.username,
            password: hashedPass,
            Data: [{
                date: moment().format("YYYY-MM-DD"), // Use current date or another default date
                CO2: [],
                VOC: [],
                SoundLevel: [],
                CO2AccessTime: [],
                VOCAccessTime: [],
                SoundAccessTime: []
            }],
            SoundThreshold: 0 // or another default value
        });

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
                data: DataEntry.SoundLevel,
                DataAccessTime:DataEntry.SoundAccessTime
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
                data: DataEntry.VOC,
                DataAccessTime:DataEntry.VOCAccessTime
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
                data: DataEntry.CO2,
                DataAccessTime:DataEntry.CO2AccessTime
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

// exports.getCO2Data=async(req,res,next)=>{
//     try {
//     const {  date } = req.body; 
//     const user = await Account.findOne({ id: userId});
//         if (!user) {
//             return res.status(404).json({ message: "User not found" });
//         }
//         const requestedDate =moment(new Date(date)).format("YYYY-MM-DD")
        
//         const DataEntry = user.Data.find(entry => {
//             const entryDate =moment(new Date(entry.date)).format("YYYY-MM-DD")
        
//             console.log(entryDate+"  "+requestedDate)
//             return entryDate === requestedDate;
//         });

//         if (DataEntry) {
//             // If an entry exists for the requested date, return the values
//             return res.json({ 
//                 message: "CO2 data retrieved successfully",
//                 data: DataEntry.CO2,
//                 DataAccessTime:DataEntry.CO2AccessTime
                
//             });
//         } else {
//             // If no entry for the requested date, return a message indicating no data was found
            
//             return res.status(404).json({ message: "No CO2 data found for the specified date" });
//         }
//     }
//     catch (error) {
//         console.error("Error retrieving CO2 data:", error);
//         return res.status(500).json({ message: "Error retrieving CO2 data", error: error.message });
//     }
// }
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
exports.setSoundThreshold=async (req,res,next)=>{
    try{
    const {thresholdValue } = req.body;
    const user = await Account.findOne({ id: userId });
        if (!user) {
            return res.json({ message: "User not found" });
        }
        else{
        user.SoundThreshold=thresholdValue;
        await user.save()
        return res.json({ isDataStored:true, message: `Threshold has been set to ${thresholdValue} dB` })
        }
    }
    catch(error){
        return res.json({isDataStored:false, message: "Error storing Threshold Data", error: error.message });
    }
    
}
exports.getSoundThreshold=async (req,res,next)=>{
    try{
    
    const user = await Account.findOne({ id: userId });
        if (!user) {
            return res.json({ message: "User not found" });
        }
        else if(user.SoundThreshold){
        return res.json({dataExist:true, data:user.SoundThreshold })
        }
        else if(!user.SoundThreshold){
            return res.json({dataExist:false, data:60 })
            }
       
    }
    catch(error){
        return res.json({isDataRetrieved:false, message: "Error storing Threshold Data", error: error.message });
    }
    
}
exports.addData = async (req, res, next) => {
    try {
        const { soundValues, vocValues, co2Values, soundTime,  vocTime,co2Time } = req.body;
        console.log(req.body)
        const currentDate = moment(new Date()).format("YYYY-MM-DD");

        const user = await Account.findOne({ id: userId });
        if (!user) {
            return res.status(404).json({ message: "User not found" });
        }

        // Find or create the entry for the current date
        let dataEntry = user.Data.find(entry => moment(entry.date).format("YYYY-MM-DD") === currentDate);
        if (!dataEntry) {
            dataEntry = {
                date: currentDate,
                SoundLevel: [],
                VOC: [],
                CO2: [],
                SoundAccessTime: [],
                VOCAccessTime: [],
                CO2AccessTime: []
            };
            user.Data.push(dataEntry);
        }

        // Update the existing entries
        const soundMinLength = Math.min(soundValues.length, soundTime.length);
        dataEntry.SoundLevel.push(...soundValues.slice(0, soundMinLength));
        dataEntry.SoundAccessTime.push(...soundTime.slice(0, soundMinLength));

        const vocMinLength = Math.min(vocValues.length, vocTime.length);
        dataEntry.VOC.push(...vocValues.slice(0, vocMinLength));
        dataEntry.VOCAccessTime.push(...vocTime.slice(0, vocMinLength));

        const co2MinLength = Math.min(co2Values.length, co2Time.length);
        dataEntry.CO2.push(...co2Values.slice(0, co2MinLength));
        dataEntry.CO2AccessTime.push(...co2Time.slice(0, co2MinLength));

        let currentInfo=await user.save(); // Save the updated document
        console.log(currentInfo);
        return res.json({ message: "Data updated successfully" });
    } catch (error) {
        console.error("Error updating data:", error);
        return res.status(500).json({ message: "Error updating data", error: error.message });
    }
};

exports.getDates = async (req, res, next) => {
    try {
      
     

        const user = await Account.findOne({ id: userId });
        if (!user) {
            return res.status(404).json({ message: "User not found" });
        }
        const dates = user.Data.map(dataEntry => dataEntry.date);

        return res.status(200).json({ dates });
        
        
      
    }
 catch (error) {
    console.error('Error getting dates:', error);
    return res.status(500).json({ message: "Error fetching dates" });
}
};