package com.gowittgroup.smartassist.util

import com.gowittgroup.smartassistlib.models.authentication.User

object Session {
    var currentUser: User? = null
    var subscriptionStatus: Boolean = false
    var userHasClosedTheBanner: Boolean = false
}
