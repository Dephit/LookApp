package com.sergeenko.lookapp.fragments

import android.content.Context
import android.content.Intent
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.isProgressActive
import com.github.razir.progressbutton.showProgress
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.sergeenko.lookapp.MainNavigation
import com.sergeenko.lookapp.R
import com.sergeenko.lookapp.databinding.AuthFragmentBinding
import com.sergeenko.lookapp.models.SocialResponse
import com.sergeenko.lookapp.viewModels.AuthViewModel
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*



@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AuthFragment : BaseFragment<AuthFragmentBinding>() {

    private val RC_SIGN_IN: Int = 412
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions
    private lateinit var fbCallbackManager: CallbackManager
    var showVk = false

    override val viewModel: AuthViewModel by navGraphViewModels(R.id.main_navigation) {
        defaultViewModelProviderFactory
    }

    private fun initGoogle() {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode("513283860171-s3r32tb8o809ihh7nctoinqnm2plic67.apps.googleusercontent.com")
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    override fun setListeners() {
        with(binding){
            val loginManager = LoginManager.getInstance();

            registerFBLoginCallback(loginManager)

            facebookAuth.setOnClickListener {
                disableButtons()
                //facebookAuth.showPinkProgress()
                loginManager.logInWithReadPermissions(this@AuthFragment, listOf("public_profile", "email"));
            }

            googleAuth.setOnClickListener {
                disableButtons()
                        //googleAuth.showPinkProgress()
                val signInIntent: Intent = mGoogleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }

            vkAuth.setOnClickListener {
                disableButtons()
                showVk = true
                //vkAuth.showPinkProgress()
                (requireActivity() as MainNavigation).vkLogin(manageVkResponse())
            }
            setSpannableText(binding.authWithPhone, R.string.auth_with_phone)
        }
    }



    override fun <T> manageSuccess(obj: T?) {
        if(obj is SocialResponse){
            disableButtons()
            startMainActivity(obj, R.id.action_authFragment_to_registerLoginFragment)
            viewModel.restoreState()
        }else{
            enableButtons()
        }

    }

    override fun <T> manageError(error: T?) {
        if(error is String){
            showToast(error)
        }
        binding.googleAuth.isEnabled = true
        binding.facebookAuth.isEnabled = true
        binding.vkAuth.isEnabled = true
        super.manageError(error)
    }

    private fun registerFBLoginCallback(loginManager: LoginManager) {
        loginManager.registerCallback(fbCallbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                binding.facebookAuth.hideProgress(R.string.facebook_text)
                disableButtons()
                viewModel.logWithFacebook(loginResult)
            }

            override fun onCancel() {
                enableButtons()
                binding.facebookAuth.hideProgress(R.string.facebook_text)
            }

            override fun onError(exception: FacebookException) {
                enableButtons()
                binding.facebookAuth.hideProgress(R.string.facebook_text)
            }
        })
    }


    override fun onResume() {
        super.onResume()
        if(showVk){
            enableButtons()
            showVk = false
            binding.vkAuth.hideProgress(R.string.vk_text)
        }
    }

    fun enableButtons(){
        binding.googleAuth.isEnabled = true
        binding.facebookAuth.isEnabled = true
        binding.vkAuth.isEnabled = true
        binding.authWithPhone.isEnabled = true
    }

    fun disableButtons(){
        binding.googleAuth.isEnabled = false
        binding.facebookAuth.isEnabled = false
        binding.vkAuth.isEnabled = false
        binding.authWithPhone.isEnabled = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        manageGoogleResponse(requestCode, resultCode, data)
        fbCallbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun manageGoogleResponse(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }else{
            enableButtons()
        }
    }

    private fun manageVkResponse(): VKAuthCallback {
        return object: VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                binding.vkAuth.hideProgress(R.string.vk_text)
                disableButtons()
                viewModel.logWithVk(token)
            }

            override fun onLoginFailed(errorCode: Int) {
                enableButtons()
                binding.vkAuth.hideProgress(R.string.vk_text)
            }
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        viewModel.logWithGoogle(task, /*requireContext()*/)
        binding.googleAuth.hideProgress(R.string.google_text)
    }

    private fun setSpannableText(heightText: TextView, textID: Int) {
        val str = getString(textID)
        val spannable = SpannableString(str)

        val fc = ForegroundColorSpan(getColor(requireContext(), R.color.pink_pressed))

        val indexStart = str.indexOf("\n")
        val indexEnd = str.length

        spannable.setSpan(fc, indexStart, indexEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        heightText.text = spannable
        heightText.movementMethod = object : LinkMovementMethod() {

            override fun onTouchEvent(textView: TextView?, spannable: Spannable, event: MotionEvent): Boolean {
                if (event.action == MotionEvent.ACTION_DOWN) {
                    spannable.setSpan(ForegroundColorSpan(getColor(requireContext(), R.color.black_100)), indexStart, indexEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                } else {
                    spannable.setSpan(ForegroundColorSpan(getColor(requireContext(), R.color.pink_pressed)), indexStart, indexEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                if(event.action == MotionEvent.ACTION_UP && textView?.isPressed == true) {
                    logInWithPhone()
                }
                return true
            }
        }
    }




    private fun logInWithPhone() {
        try {
            findNavController().navigate(R.id.action_authFragment_to_phoneAuthFragment)
        }catch (e: Exception){

        }
    }

    override fun bind(inflater: LayoutInflater): AuthFragmentBinding {
        fbCallbackManager = CallbackManager.Factory.create();
        initGoogle()
        return AuthFragmentBinding.inflate(inflater)
    }

}

fun Button.showPinkProgress() {
    showProgress{
        progressColor = getColor(context, R.color.pink)
    }
}

fun Button.showWhiteProgress() {
    showProgress{
        progressColor = getColor(context, R.color.white)
    }
}

fun getColor(requireContext: Context, id: Int): Int {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        requireContext.getColor(id)
    }else
        requireContext.resources.getColor(id)
}