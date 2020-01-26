package ivan.karpiuk.githubusers.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import ivan.karpiuk.githubusers.R
import ivan.karpiuk.githubusers.ServiceLocator
import ivan.karpiuk.githubusers.model.UserDetailViewModel
import ivan.karpiuk.githubusers.dto.UserDetails
import kotlinx.android.synthetic.main.activity_user_details.*

class UserDetailsActivity : AppCompatActivity() {

    private val model: UserDetailViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repo = ServiceLocator.instance()
                    .getRepository()
                @Suppress("UNCHECKED_CAST")
                return UserDetailViewModel(repo, intent.getStringExtra(KEY_USER_LOGIN)) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)
        setUpToolbar()
        startAnimation()
        setErrorListener()
        loadUserDetails()
    }

    private fun setUpToolbar() {
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = null
    }

    private fun startAnimation() {
        window.allowEnterTransitionOverlap = true
        Glide.with(this)
            .load(intent.getStringExtra(KEY_USER_AVATAR))
            .into(ivAvatar)
        ViewCompat.setTransitionName(ivAvatar, "ivAvatar")
    }

    private fun setErrorListener() {
        model.getLoadingError().observe(this, Observer<Throwable> { throwable ->
            tvGists.text = throwable.message
            title = intent.getStringExtra(KEY_USER_LOGIN)
            tvName.text = intent.getStringExtra(KEY_USER_LOGIN)
        })
    }

    private fun loadUserDetails() {
        model.getUser().observe(this, Observer<UserDetails> { user ->
            title = user.name
            Glide.with(this)
                .load(user.avatarUrl)
                .into(ivAvatar)
            tvName.text = user.name
            tvWebLink.text = user.htmlUrl
            tvWebLink.setOnClickListener {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(user.htmlUrl)
                    )
                )
            }
            tvRepos.text = getString(R.string.repos, user.publicRepos)
            tvGists.text = getString(R.string.gists, user.publicGists)
            tvFollowers.text = getString(R.string.followers, user.followers)
        })
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val KEY_USER_LOGIN = "login"
        const val KEY_USER_AVATAR = "avatar"
        fun intentFor(
            context: Context,
            login: String,
            avatarUrl: String
        ): Intent {
            val intent = Intent(context, UserDetailsActivity::class.java)
            intent.putExtra(KEY_USER_LOGIN, login)
            intent.putExtra(KEY_USER_AVATAR, avatarUrl)
            return intent
        }
    }
}
