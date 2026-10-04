package com.latihan.kurirdirectoryapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.latihan.kurirdirectoryapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var pbLoading: ProgressBar
    private lateinit var tvError: TextView
    private lateinit var tvEmptyState: TextView
    private lateinit var rvUsers: RecyclerView

    private lateinit var adapterKurir: UserAdapter
    private val masterDataKurir = mutableListOf<UserResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupRecyclerView()
        fetchDataFromApi()
        setupSearchListener()
    }

    private fun initViews() {
        etSearch = findViewById(R.id.editTextSearchName)
        pbLoading = findViewById(R.id.progressBarLoading)
        tvError = findViewById(R.id.textViewErrorMessage)
        tvEmptyState = findViewById(R.id.textViewEmpty)
        rvUsers = findViewById(R.id.recyclerViewUsers)
    }

    private fun setupRecyclerView() {
        rvUsers.layoutManager = LinearLayoutManager(this)
        adapterKurir = UserAdapter(emptyList())
        rvUsers.adapter = adapterKurir
    }

    private fun fetchDataFromApi() {
        pbLoading.visibility = View.VISIBLE
        tvError.visibility = View.GONE
        tvEmptyState.visibility = View.GONE

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val res = ApiClient.apiService.getAllUsers()
                withContext(Dispatchers.Main) {
                    pbLoading.visibility = View.GONE
                    if (res.isSuccessful && res.body() != null) {
                        masterDataKurir.clear()
                        masterDataKurir.addAll(res.body()!!)
                        adapterKurir.updateData(masterDataKurir)
                    } else {
                        tvError.text = "Gagal mengambil data dari server (${res.code()})"
                        tvError.visibility = View.VISIBLE
                    }
                }
            } catch (err: Exception) {
                withContext(Dispatchers.Main) {
                    pbLoading.visibility = View.GONE
                    tvError.text = "Gagal terhubung: ${err.localizedMessage ?: "Gangguan Jaringan"}"
                    tvError.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupSearchListener() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                performSearch(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun performSearch(keyword: String) {
        if (keyword.isEmpty()) {
            adapterKurir.updateData(masterDataKurir)
            tvEmptyState.visibility = View.GONE
            rvUsers.visibility = View.VISIBLE
        } else {
            val filteredList = masterDataKurir.filter { item ->
                item.name.contains(keyword, ignoreCase = true) ||
                        item.company.companyName.contains(keyword, ignoreCase = true)
            }

            adapterKurir.updateData(filteredList)

            if (filteredList.isEmpty()) {
                tvEmptyState.visibility = View.VISIBLE
                rvUsers.visibility = View.GONE
            } else {
                tvEmptyState.visibility = View.GONE
                rvUsers.visibility = View.VISIBLE
            }
        }
    }
}