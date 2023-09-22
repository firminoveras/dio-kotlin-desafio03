package br.com.igorbag.githubsearch.ui

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.domain.Repository
import br.com.igorbag.githubsearch.ui.adapter.RepositoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    lateinit var adapter: RepositoryAdapter
    lateinit var nomeUsuario: EditText
    lateinit var btnConfirmar: Button
    lateinit var listaRepositories: RecyclerView
    lateinit var githubApi: GitHubService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        setupListeners()
        showUserName()
        setupRetrofit()
        getAllReposByUserName()
    }

    fun setupView() {
        nomeUsuario = findViewById(R.id.et_nome_usuario)
        btnConfirmar = findViewById(R.id.btn_confirmar)
        listaRepositories = findViewById(R.id.rv_lista_repositories)
    }

    private fun setupListeners() {
        btnConfirmar.setOnClickListener {
            saveUserLocal()
            getAllReposByUserName()
        }
    }


    private fun saveUserLocal() {
        getSharedPreferences("br.com.igorbag.githubsearch.ui", MODE_PRIVATE)
            .edit()
            .putString("lastUser", nomeUsuario.text.toString())
            .apply()
    }

    private fun showUserName() {
        nomeUsuario.setText(getSharedPreferences("br.com.igorbag.githubsearch.ui", MODE_PRIVATE).getString("lastUser", ""))
    }

    fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        this.githubApi = retrofit.create(GitHubService::class.java)
    }

    fun getAllReposByUserName() {
        githubApi.getAllRepositoriesByUser(nomeUsuario.text.toString()).enqueue(object : Callback<List<Repository>> {
            override fun onResponse(call: Call<List<Repository>>, response: Response<List<Repository>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        setupAdapter(it)
                    }
                } else {
                    nomeUsuario.error = "Não foi possível encontrar o usuário"
                    setupAdapter(arrayListOf())
                }
            }

            override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Houve um problema ao se conectar com a API do Github, tente mais tarde.", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun setupAdapter(list: List<Repository>) {
        adapter = RepositoryAdapter(list)
        listaRepositories.adapter = adapter
        adapter.repositoryClickListener = {
            openBrowser(it.htmlUrl)
        }
        adapter.repositoryShareCLickListener = {
            shareRepositoryLink(it.htmlUrl)
        }
    }


    fun shareRepositoryLink(urlRepository: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlRepository)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    fun openBrowser(urlRepository: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(urlRepository)))
    }

}