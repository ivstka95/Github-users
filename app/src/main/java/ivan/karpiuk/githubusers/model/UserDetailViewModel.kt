package ivan.karpiuk.githubusers.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ivan.karpiuk.githubusers.repository.GithubUsersRepository
import ivan.karpiuk.githubusers.dto.UserDetails
import retrofit2.Call
import retrofit2.Response

class UserDetailViewModel(private val repo: GithubUsersRepository, private val login: String?) :
    ViewModel() {
    private val userDetails: MutableLiveData<UserDetails> by lazy {
        MutableLiveData<UserDetails>().also { loadUsers() }
    }
    private val error: MutableLiveData<Throwable> by lazy {
        MutableLiveData<Throwable>()
    }

    fun getUser() = userDetails

    fun getLoadingError() = error

    private fun loadUsers() {
        repo.getUserDetails(login).enqueue(
            object : retrofit2.Callback<UserDetails> {
                override fun onFailure(call: Call<UserDetails>, t: Throwable) {
                    error.value = t
                }

                override fun onResponse(call: Call<UserDetails>, response: Response<UserDetails>) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        userDetails.value = data
                    } else {
                        error.value = Throwable("Loading user details request failed")
                    }
                }
            }
        )
    }
}