package de.unihannover.se.tauben2.viewmodel

import android.content.Context
import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.model.entity.User

class UserViewModel(context: Context): BaseViewModel(context) {

    val users: LiveDataRes<List<User>> = repository.getUsers()

}