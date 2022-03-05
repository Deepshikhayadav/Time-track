package com.deepshikhayadav.timetrack

import com.google.firebase.firestore.Exclude

class MyModel (

    @Exclude
    var id:String?=null,
    var email:String?=null,
    var firstActivity:String?=null,
    var secondActivity:String?=null,
    var total:String?=null,
)