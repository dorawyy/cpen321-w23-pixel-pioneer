// Firebase Cloud Messaging
var admin = require("firebase-admin");

const moment = require('moment');
// TODO: change file name to notification.js
// This function is finished with the help of ChatGPT, especially for the usage of "moment"
function calculateTTLOnSeconds(dateToNotify) {
//    // Parse the "dateToNotify" string into a Date object using moment.js
//    dateToNotifyStr = dateToNotifyStr + " 00:00"
//    const parsedDateToNotify = moment(dateToNotifyStr, 'YYYY-MM-DD HH:mm');
    // Get the current date and time
    const currentDateAndTime = moment();
    // Calculate the time difference in seconds
    const timeDifferenceInSeconds = dateToNotify.diff(currentDateAndTime, 'seconds');
    return timeDifferenceInSeconds;
}
async function sendNotification(registrationToken, dateToNotify) {
    //  const time_to_live = Number(calculateTTLOnSeconds(dateToNotify));
  const messageBody = "New event has been added for date " + dateToNotify;
  var message = {
    notification: {
      title: "New event registered!",
      body: messageBody,
    },
    token: registrationToken
  };

  await admin
    .messaging()
    .send(message)
    .then(async function (response) {
      console.log("Successfully sent with response: ", response);
    })
    .catch(async function (err) {
      console.log("Something went wrong: " + err);
    });
}

module.exports = { sendNotification };