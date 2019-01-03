package de.unihannover.se.tauben2.viewmodel

import android.content.Context
import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.model.AbsentLiveData
import de.unihannover.se.tauben2.model.Auth
import de.unihannover.se.tauben2.model.database.entity.User

class UserViewModel(context: Context) : BaseViewModel(context) {

    val owner: LiveDataRes<User>
    val users: LiveDataRes<List<User>> = repository.getUsers()

    init {
        val username = getOwnerUsername()
        owner = if(username != null)
            repository.getUser(username)
        else
            AbsentLiveData.create()
    }

    fun getOwnerUsername() = repository.getOwnerUsername()

    fun register(user: User) = repository.register(user)
    fun login(user: User) = repository.login(user)
    fun logout() = repository.logout()
    fun updatePermissions(auth: Auth) = repository.updatePermissions(auth)
}