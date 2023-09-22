package br.com.igorbag.githubsearch.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.domain.Repository

class RepositoryAdapter(private val repositories: List<Repository>) :
    RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {

    var repositoryClickListener: (Repository) -> Unit = {}
    var repositoryShareCLickListener: (Repository) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.repository_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = repositories[position].name
        holder.shareButton.setOnClickListener {
            repositoryShareCLickListener(repositories[position])
        }
        holder.mainLayout.setOnClickListener {
            repositoryClickListener(repositories[position])
        }
    }

    override fun getItemCount(): Int = repositories.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView
        var shareButton: ImageView
        var mainLayout: CardView
        init {
            title = view.findViewById(R.id.tv_title)
            shareButton = view.findViewById(R.id.iv_share)
            mainLayout = view.findViewById(R.id.cv_repository)
        }
    }
}


