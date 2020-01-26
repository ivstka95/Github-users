package ivan.karpiuk.githubusers

import ivan.karpiuk.githubusers.api.GithubApi
import ivan.karpiuk.githubusers.repository.GithubUsersRepository
import ivan.karpiuk.githubusers.repository.GitHubUsersRepository
import java.util.concurrent.Executor
import java.util.concurrent.Executors

interface ServiceLocator {
    companion object {
        private val LOCK = Any()
        private var instance: ServiceLocator? = null
        fun instance(): ServiceLocator {
            synchronized(LOCK) {
                if (instance == null) {
                    instance =
                        DefaultServiceLocator()
                }
                return instance!!
            }
        }
    }

    fun getRepository(): GithubUsersRepository

    fun getNetworkExecutor(): Executor

    fun getGithubApi(): GithubApi
}

open class DefaultServiceLocator : ServiceLocator {

    // thread pool used for network requests
    @Suppress("PrivatePropertyName")
    private val NETWORK_IO = Executors.newFixedThreadPool(5)

    private val api by lazy {
        GithubApi.create()
    }

    override fun getRepository() =
        GitHubUsersRepository(
            githubApi = getGithubApi(),
            networkExecutor = getNetworkExecutor()
        )

    override fun getNetworkExecutor(): Executor = NETWORK_IO

    override fun getGithubApi(): GithubApi = api
}