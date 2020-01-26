package ivan.karpiuk.githubusers.repository

import ivan.karpiuk.githubusers.dto.User
import ivan.karpiuk.githubusers.dto.UserDetails
import retrofit2.Call

interface GithubUsersRepository {
    fun getGitHubUsers(pageSize: Int): Listing<User>
    fun getUserDetails(login: String?): Call<UserDetails>
}