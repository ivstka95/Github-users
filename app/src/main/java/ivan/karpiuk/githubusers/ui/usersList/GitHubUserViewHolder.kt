package ivan.karpiuk.githubusers.ui.usersList

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import ivan.karpiuk.githubusers.R
import ivan.karpiuk.githubusers.dto.User

class GitHubUserViewHolder(
    view: View,
    onClickListener: (View, User?) -> Unit
) :
    RecyclerView.ViewHolder(view) {
    private val tvLogin: TextView = view.findViewById(R.id.tvLogin)
    private val ivAvatar: ImageView = view.findViewById(R.id.ivAvatar)
    private var user: User? = null


    init {
        view.setOnClickListener {
            onClickListener(view, user)
        }
    }

    fun bind(user: User?) {
        this.user = user
        tvLogin.text = user?.login ?: "loading"
        ivAvatar.visibility = View.VISIBLE
        Glide.with(ivAvatar)
            .load(user?.avatarUrl)
            .centerCrop()
            .placeholder(R.mipmap.ic_launcher)
            .into(ivAvatar)
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onClickListener: (View, User?) -> Unit
        ): GitHubUserViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.github_user_item, parent, false)
            return GitHubUserViewHolder(
                view,
                onClickListener
            )
        }
    }
}