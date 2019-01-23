package de.unihannover.se.tauben2.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.Observer
import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.model.AbsentLiveData
import de.unihannover.se.tauben2.model.Auth
import de.unihannover.se.tauben2.model.UserRegistrationToken
import de.unihannover.se.tauben2.model.database.entity.User
import de.unihannover.se.tauben2.model.network.Resource

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
    fun getGuestPhone() = repository.getGuestPhone()
    fun setGuestPhone(phone: String) {
        repository.setGuestPhone(phone)
    }

    fun register(user: User) = repository.register(user)
    fun login(user: User) = repository.login(user)
    fun logout() = repository.logout()
    fun updatePermissions(username: String, auth: Auth) = repository.updatePermissions(username, auth)
    fun deleteUser(user: User) = repository.deleteUser(user)

    fun updateUser(user: User) = repository.updateUser(user)
    fun updateRegistrationToken(username: String, userRegistrationToken: UserRegistrationToken) = repository.updateRegistrationToken(username, userRegistrationToken)

    fun reloadUsersFromServer(successFunction : () -> Any) {
        val result = repository.getUsers()
        result.observeForever(object : Observer<Resource<List<User>>> {
            override fun onChanged(t: Resource<List<User>>?) {
                if(t?.status?.isSuccessful() == true) {
                    successFunction()
                    result.removeObserver(this)
                }
                if(t?.hasError() == true)
                    result.removeObserver(this)

            }

        })
    }
}