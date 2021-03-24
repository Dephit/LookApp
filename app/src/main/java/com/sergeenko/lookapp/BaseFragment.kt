package com.sergeenko.lookapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.sergeenko.lookapp.models.SocialResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


abstract class BaseFragment<T : ViewBinding>: Fragment(), FragmentInterface<T> {

    lateinit var binding: T
    abstract val viewModel: BaseViewModelInterface

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = bind(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        hideKeyboard()
    }

    fun showKeyBoard(view: View){
        val keyboard = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        keyboard!!.showSoftInput(view, 0)
    }

    fun showToast(s: String?) {
        Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()
    }

    fun startMainActivity(obj: SocialResponse, action: Int) {
        if(BuildConfig.IS_ALWAYS_REG){
            findNavController().navigate(action)
        }else{
            if(obj.data.new_user){
                findNavController().navigate(action)
            }else {
                lifecycleScope.launch(IO) {
                    viewModel.fetchBrands()
                    startActivity(Intent(context, MenuActivity::class.java))
                    requireActivity().finish()
                }
            }
        }
    }

    abstract fun bind(inflater: LayoutInflater) : T

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        restoreState(savedInstanceState)
        setListeners()
        observe()
    }

    override fun observe() {
        lifecycleScope.launchWhenStarted {
            viewModel.getState().collect{
                if(it is ModelState.Loading)
                    manageLoading(true)
                else
                    manageLoading(false)
                if(it is ModelState.Error<*>)
                    manageError(it.obj)
                else
                    manageError(false)
                if(it is ModelState.Success<*>){
                    manageSuccess(it.obj)
                }
                observeTo(it)
            }
        }
    }

    override fun observeTo(modelState: ModelState?) {

    }

    override fun restoreState(savedInstanceState: Bundle?) {}

    override fun setListeners(){

    }

    inline fun withBinding(block: T.() -> Unit) {
        block(binding)
    }

    override fun manageLoading(b: Boolean) {

    }

    override fun <T> manageSuccess(obj: T?) {

    }

    override fun <T> manageError(error: T?) {
        if(error is String) {
            showToast(error)
        }
        manageError(true)
    }

    override fun manageError(bool: Boolean) {
    }

    open fun hideKeyboard() {
        val imm: InputMethodManager =
            activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view: View? = activity?.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
