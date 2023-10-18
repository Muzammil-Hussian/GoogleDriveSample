@file:Suppress("DEPRECATION")

package com.google.drive.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.drive.R
import com.google.drive.adapter.DriveAdapter
import com.google.drive.databinding.ActivityMainBinding
import com.google.drive.extension.beGone
import com.google.drive.extension.beGoneIf
import com.google.drive.extension.beVisible
import com.google.drive.extension.beVisibleIf
import com.google.drive.extension.showToast
import com.google.drive.utils.DriveServiceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: DriveAdapter
    private lateinit var mDriveServiceHelper: DriveServiceHelper

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        when {
            result.resultCode == Activity.RESULT_OK && result.data != null -> handleSignInResult(result.data!!)
            else -> showToast(getString(R.string.failed_to_login))
        }
    }
    private val isUserSinged: GoogleSignInAccount? by lazy { GoogleSignIn.getLastSignedInAccount(this@MainActivity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAdapter()
        clickListeners()
    }

    private fun setupAdapter() {
        adapter = DriveAdapter(this) { file, position ->
            lifecycleScope.launch(Dispatchers.IO) {
                val list = mDriveServiceHelper.getFolderFileList(folderId = file.id)
                withContext(Dispatchers.Main) {
                    binding.noFileFound.root.beGoneIf(list.isNotEmpty())
                    binding.progressBar.beGone()
                    adapter.submitList(list)
                }
            }
        }
        binding.recyclerView.adapter = adapter
    }

    private fun clickListeners() {
        with(binding) {
            signInWithGoogle.setOnClickListener {
                binding.progressBar.beVisibleIf(isUserSinged == null)
                binding.noFileFound.root.beVisibleIf(isUserSinged == null)
                if (isUserSinged == null) requestSignIn()
                else {
                    if (checkForGooglePermissions()) {
                        getLastSignedInGoogleAccount()
                    }
                }
            }
            signOut.setOnClickListener { signOut() }
        }
    }

    override fun onStart() {
        super.onStart()
        getLastSignedInGoogleAccount()
    }

    private fun requestSignIn() = startForResult.launch(googleSignInClient().signInIntent)

    private fun signOut() {
        googleSignInClient().signOut()
        binding.signInWithGoogle.visibility = View.VISIBLE
        binding.signOut.visibility = View.GONE
        binding.noFileFound.root.beVisible()
        adapter.submitList(arrayListOf())
    }

    private fun googleSignInClient(): GoogleSignInClient {
        val signInOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE), Scope(DriveScopes.DRIVE))
            .build()
        return GoogleSignIn.getClient(this, signInOptions)
    }

    private fun handleSignInResult(result: Intent) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
            .addOnSuccessListener { googleAccount: GoogleSignInAccount ->
                binding.signOut.beVisible()
                binding.signInWithGoogle.beGone()
                binding.progressBar.beVisible()

                // Use the authenticated account to sign in to the Drive service.
                val credential = GoogleAccountCredential.usingOAuth2(this@MainActivity, setOf(Scopes.DRIVE_FULL))
                credential.selectedAccount = googleAccount.account

                if (checkForGooglePermissions()) {
                    // get Drive Instance
                    val drive = Drive.Builder(AndroidHttp.newCompatibleTransport(), JacksonFactory.getDefaultInstance(), credential)
                        .setApplicationName(getString(R.string.app_name))
                        .build()

                    mDriveServiceHelper = DriveServiceHelper(drive)

                    lifecycleScope.launch(Dispatchers.IO) {
                        val list = mDriveServiceHelper.getFolderFileList("root")
                        withContext(Dispatchers.Main) {
                            binding.noFileFound.root.beGoneIf(list.isNotEmpty())
                            binding.progressBar.beGone()
                            adapter.submitList(list)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                binding.signInWithGoogle.visibility = View.VISIBLE
                binding.signOut.visibility = View.GONE
                exception.printStackTrace()
            }
    }

    private fun checkForGooglePermissions(): Boolean {
        return try {
            return if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this@MainActivity), Scope(Scopes.DRIVE_FULL), Scope(Scopes.PROFILE))) {
                GoogleSignIn.requestPermissions(this, 669, GoogleSignIn.getLastSignedInAccount(this@MainActivity), Scope(Scopes.DRIVE_FULL), Scope(Scopes.PROFILE))
                false
            } else true
        } catch (e: Exception) {
            false
        }
    }

    private fun getLastSignedInGoogleAccount() {
        GoogleSignIn.getLastSignedInAccount(this)?.let { googleAccount ->
            binding.noFileFound.root.beGone()
            // get credentials
            val credential = GoogleAccountCredential.usingOAuth2(this, listOf(DriveScopes.DRIVE, DriveScopes.DRIVE_FILE))
            credential.selectedAccount = googleAccount.account!!

            // get Drive Instance
            val drive = Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
            ).setApplicationName(getString(R.string.app_name)).build()

            mDriveServiceHelper = DriveServiceHelper(drive)

            if (checkForGooglePermissions()) {
                binding.signOut.visibility = View.VISIBLE
                binding.signInWithGoogle.visibility = View.GONE


                lifecycleScope.launch(Dispatchers.IO) {
                    val list = mDriveServiceHelper.getFolderFileList("root")
                    withContext(Dispatchers.Main) {
                        binding.progressBar.visibility = View.GONE
                        binding.noFileFound.root.beGoneIf(list.isNotEmpty())
                        adapter.submitList(list)
                    }
                }
            }
        }
    }

}