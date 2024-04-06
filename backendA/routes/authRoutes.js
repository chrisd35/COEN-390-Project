const express = require('express');
const router = express.Router();
const authController = require('../controllers/authController');


router.post("/signup",authController.signup)
router.post("/login",authController.login)
router.delete("/logOut",authController.logOut)
router.get("/isAuthorized",authController.authenticatedCheck)
router.post("/AddSoundData",authController.AddSoundData)
router.post("/retrieveSoundData",authController.getSoundData)
router.post("/AddVOCdata",authController.AddVOCData)
router.post("/AddCO2Data",authController.AddCO2Data)
router.post("/retrieveCO2Data",authController.getCO2Data)
router.post("/retrieveVOCData",authController.getVOCData)
router.post("/setSoundThreshold",authController.setSoundThreshold)
router.get("/getSoundThreshold",authController.getSoundThreshold)
//erase
router.post("/addData",authController.addData)
// router.post("/addDataD",DataController.addData)
router.get("/getAllDates",authController.getDates)

router.post("/forgotpassword",authController.forgotPassword)
router.post("/resetpassword",authController.resetPassword)

module.exports=router;