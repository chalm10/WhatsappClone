const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();
 // Create and Deploy Your First Cloud Functions
 // https://firebase.google.com/docs/functions/write-firebase-functions
exports.sendNotification = functions.database.ref('/messages/{chatId}/{msgId}')
    .onCreate((snapshot , context) => {
        const chat_id = context.params.chatId;
        const messageFrom = snapshot.val().senderId;
        const message = snapshot.val().msg;
        const user_id = chat_id.replace(messageFrom,'');

        let name;
        let photo;
        admin.firestore().collection("users").doc(messageFrom).get()
        .then( user => {
            name = user.data().name;
            photo = user.data().thumbImageUrl;
            console.log(name);
            console.log(photo);
            return true
        }).catch(error =>{
            console.log(error)
        })

        const status = admin.firestore().collection("users").doc(user_id).get()
        .then( user => {
            const token = user.data().deviceToken;
            console.log(token);
            const payload = {
                notification : {
                    title : name,
                    body : message,
                    icon : photo
                    }
                }
            return admin.messaging().sendToDevice(token , payload);

        }).catch( error=>{
            console.log(error);
        })

//        return status;

    })


