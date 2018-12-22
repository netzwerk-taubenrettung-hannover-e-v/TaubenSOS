package de.unihannover.se.tauben2.viewmodel

import android.content.Context
import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.model.database.entity.User

class UserViewModel(context: Context) : BaseViewModel(context) {

    val users: LiveDataRes<List<User>> = repository.getUsers()
    fun register(user: User) = repository.register(user)
    fun login(user: User) = repository.login(user)
    fun logout() = repository.logout()
    fun updatePermissions(user: User) = repository.updatePermissions(user)
}