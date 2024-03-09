const express = require('express');
const router = express.Router();
const authController = require('../controllers/authController');

router.post("/signup",authController.signup)
router.post("/login",authController.login)
router.delete("/logOut",authController.logOut)
router.get("/isAuthorized",authController.authenticatedCheck)
router.post("/AddSoundData",authController.AddSoundData)
router.post("/retrieveSoundData",authController.retrieveSoundData)

module.exports=router;