package ec.com.smx.cfmx.ui.activity

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.DragEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.view.get
import ec.com.smx.cfmx.R
import ec.com.smx.cfmx.async.LoadModuleAsyncTask
import ec.com.smx.cfmx.async.LoadOptionsAsyncTask
import ec.com.smx.cfmx.async.SaveModuleAsyncTask
import ec.com.smx.cfmx.async.UpdateFavOptionsAsyncTask
import ec.com.smx.cfmx.async.listener.LoadModuleAsyncListener
import ec.com.smx.cfmx.async.listener.LoadOptionsAsyncListener
import ec.com.smx.cfmx.async.listener.SaveModuleAsyncListener
import ec.com.smx.cfmx.async.listener.UpdateOptionsAsyncListener
import ec.com.smx.cfmx.data.api.listener.MenuServiceListener
import ec.com.smx.cfmx.data.api.vo.response.ModuleResponse
import ec.com.smx.cfmx.data.persistence.AppSharedPreference
import ec.com.smx.cfmx.data.persistence.entity.Module
import ec.com.smx.cfmx.data.persistence.entity.Option
import ec.com.smx.cfmx.data.persistence.relation.ModuleWithOptions
import ec.com.smx.cfmx.repository.rest.AccountServiceRepository
import ec.com.smx.cfmx.repository.sharedpreference.FavRepository
import ec.com.smx.cfmx.repository.sharedpreference.LocalsRepository
import ec.com.smx.cfmx.ui.activity.base.BaseActivity
import ec.com.smx.cfmx.ui.adapter.ModuleAdapter
import ec.com.smx.cfmx.ui.listener.OptionItemListener
import ec.com.smx.cfmx.util.MapperUtil
import ec.com.smx.cfmx.util.ViewUtil
import ec.com.smx.cfmx.vo.BundleVO
import ec.com.smx.cfmx.vo.UserVO
import ec.com.smx.kcommons.util.UMessage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.item_module.*
import kotlinx.android.synthetic.main.item_option.view.*
import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.lang3.text.WordUtils
import androidx.core.view.isVisible
import ec.com.smx.cfmx.async.UpdateFavOptionsListAsyncTask
import kotlin.collections.ArrayList
import android.view.View.OnAttachStateChangeListener
import com.google.gson.GsonBuilder
import ec.com.smx.cfmx.constant.BundleConstants
import ec.com.smx.cfmx.data.api.exception.CustomException
import ec.com.smx.cfmx.data.api.listener.GetCorpTokenListener
import ec.com.smx.cfmx.data.api.listener.SetFavoriteOptionListener
import ec.com.smx.cfmx.data.api.vo.request.AddFavoriteRequest
import ec.com.smx.cfmx.data.api.vo.request.GetCorpTokenRequest
import ec.com.smx.cfmx.data.api.vo.request.RemoveFavoriteRequest
import ec.com.smx.cfmx.data.api.vo.response.DefaultResponse
import ec.com.smx.cfmx.data.api.vo.response.GetCorpTokenResponse
import ec.com.smx.cfmx.data.api.vo.response.LocalResponse
import ec.com.smx.cfmx.repository.rest.ModulesServiceRepository
import ec.com.smx.kcommons.util.UView
import java.net.ConnectException
import java.net.HttpURLConnection

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class MainActivity : BaseActivity(), LoadModuleAsyncListener, MenuServiceListener,
        SaveModuleAsyncListener, LoadOptionsAsyncListener, UpdateOptionsAsyncListener,
        OptionItemListener, SetFavoriteOptionListener, GetCorpTokenListener, View.OnDragListener {

    private var selectedOption: Option? = null
    private var adapter: ModuleAdapter? = null
    private var itemsToDeleteList = ArrayList<Int>()
    private lateinit var viewContent: View
    private lateinit var deleteItem: MenuItem
    private lateinit var chgPassItem: MenuItem
    private lateinit var exitItem: MenuItem
    private lateinit var searchItem: MenuItem
    private var moduleList = ArrayList<ModuleWithOptions>()
    private var moduleListBU = ArrayList<ModuleWithOptions>()
    private var isSearching = false
    private var currentLocal: LocalResponse? = null
    private var doubleBackToExitPressedOnce = false
    private var alertDialog : AlertDialog? = null

    private val queryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
            search(query.toUpperCase())
            return true
        }

        override fun onQueryTextChange(newText: String): Boolean {
            search(newText.toUpperCase())
            return true
        }
    }

    /** GET CORP TOKEN RESPONSE LISTENERS
     */
    override fun onGetCorpTokenResponse(response: GetCorpTokenResponse?, option: Option) {
        // dismiss progressbar
        dismissProgressBar()
        if(response?.token != null) {
            // init app
            this.launchApplication(option, response.token)
        }else{
            // show error message
            UMessage.showSnackBar(viewContent, applicationContext,
                    getString(R.string.error_get_corp_token), Snackbar.LENGTH_SHORT)
        }
    }

    override fun onGetCorpTokenFailure(t: Throwable) {
        // dismiss progressbar
        dismissProgressBar()
        // show error message
        UMessage.showSnackBar(viewContent, applicationContext,
                getString(R.string.error_get_corp_token), Snackbar.LENGTH_SHORT)
    }

    /** ADD FAVORITE RESPONSE LISTENERS
     */
    override fun onSetFavoriteOptionResponse(response: DefaultResponse.Response?,
                                             request: ArrayList<AddFavoriteRequest>) {
        if(response != null){
            if(!response.success!!){
                undoAddFavorite(request.first().codigoOpcion, getString(R.string.empty_response))
            }
        }else{
            undoAddFavorite(request.first().codigoOpcion, HttpURLConnection.HTTP_INTERNAL_ERROR.toString())
        }
    }

    override fun onSetFavoriteOptionFailure(t: Throwable,
                                            request: ArrayList<AddFavoriteRequest>) {
        // check valid token
        super.onServiceFailure(t)
        // check exception
        when(t){
            is CustomException -> undoAddFavorite(request.first().codigoOpcion, t.message!!)
            is ConnectException -> undoAddFavorite( request.first().codigoOpcion,
                                                    getString(R.string.no_internet))
            else -> undoAddFavorite(request.first().codigoOpcion,
                    getString(R.string.snackbar_error_store_favorite,HttpURLConnection.HTTP_INTERNAL_ERROR.toString()))
        }
    }

    /** REMOVE FAVORITE RESPONSE LISTENERS
     */
    override fun onRemoveFavoriteOptionResponse(response: DefaultResponse.Response?,
                                                request: ArrayList<RemoveFavoriteRequest>) {
        if(response != null){
            if(!response.success!!){
                undoRemoveFavorite(getString(R.string.empty_response))
            }
        }else{
            undoRemoveFavorite(getString(R.string.empty_response))
        }
    }

    override fun onRemoveFavoriteOptionFailure(t: Throwable,
                                               request: ArrayList<RemoveFavoriteRequest>) {
        // check valid token
        super.onServiceFailure(t)
        // check exception
        when(t){
            is CustomException -> undoRemoveFavorite(t.message!!)
            is ConnectException -> undoRemoveFavorite(getString(R.string.no_internet))
            else -> undoRemoveFavorite(HttpURLConnection.HTTP_INTERNAL_ERROR.toString())
        }
    }

    /** ADD FAVORITE VIEW LISTENERS
     */
    override fun onOptionItemClicked(v: View) {
        val parentView = v.parent
        // check if clicked item its in fav container or not
        if( parentView is LinearLayout && parentView.contentDescription != null &&
            parentView.contentDescription == getString(R.string.favorite_container)){

            // check if this item is selected
            if(v.deleteCheckbox.isVisible){
                uncheckItem(v)
                showEmptyFavMsg()
            }else{
                // check if almost one item is not checked
                if(itemsToDeleteList.isEmpty()){
                    //launchApplication(getOption(v))
                    getCorpTokenAsync(getOption(v))
                }else{
                    checkItem(v)
                    showEmptyFavMsg()
                }
            }

        }else{
            // view clicked is not in favorite container
            //launchApplication(getOption(v))
            getCorpTokenAsync(getOption(v))
        }
    }

    override fun onOptionItemLongClicked(v: View) {
        val parentView = v.parent
        // check if clicked item its in fav container or not
        if( parentView is LinearLayout && parentView.contentDescription != null &&
            parentView.contentDescription == getString(R.string.favorite_container)){

            // this view is in favorite container
            checkItem(v)
            // show delete button on toolbar
            showToolbarMenu(false)

        }else{
            // view long touched is not in favorite container
            val shadowBuilder = View.DragShadowBuilder(v)
            // We must use deprecated function with this minimum android version
            @Suppress("DEPRECATION")
            v.startDrag(null, shadowBuilder, v, 0)
        }
    }

    override fun onDrag(v: View?, event: DragEvent?): Boolean {
        val action = event!!.action
        if (action == DragEvent.ACTION_DROP) {
            val view = event.localState as View
            val option = getOptionFromView(view.optionContainer)

            // check this view are not in fav container
            if ( ! hasAlreadyBeenAdded(option!!) ) {
                // set it at favorites on view
                option.isFavorite = true
                // create new view with same parent option obj
                val copyView = ViewUtil.createOptionView(this, option, this)
                this.optionsContainer.addView(copyView)
                // update option with fav = true in DB
                this.updateOptionInDB(option)
                // update option with fav = true in pref
                FavRepository.addFavList(AppSharedPreference.getInstance(this).sharedPreferences,
                        option.optionId!!)
                // call ws to update option with fav = true in pref
                val list = ArrayList<AddFavoriteRequest>()
                val item = AddFavoriteRequest()
                item.codigoOpcion = option.optionId!!.toString()
                list.add(item)
                addFavoriteAsync(list)
                showEmptyFavMsg()
            }else{
                UMessage.showSnackBar(viewContent, applicationContext,
                        getString(R.string.snackbar_error_add_favorite), Snackbar.LENGTH_SHORT)
            }
        }
        return true
    }

    /** STORE DATA IN DB RESPONSE LISTENERS
     */
    override fun onLoadOptionsFinish(optionList: List<Option>, token: String) {
        if (this.selectedOption != null) {
            val mBundle = this.setupAppBundle(optionList, token)
            this.startApp(this.selectedOption!!.androidPackage!!, mBundle)
        }
    }

    override fun onSavedModuleFinish(modules: List<Module>) {
        // Create a pojo instance
        val modulesWithOptions = MapperUtil.mapToModuleWithOptions(modules)

        AppSharedPreference.getInstance(this).clearPreferenceFavorites()

        // set stored favorites
        this.setFavoritesOptions(modulesWithOptions)

        // set on list of modules
        this.updateAdapter(modulesWithOptions)

        this.loadingProgressBar.visibility = View.GONE
        this.alert_swipe_refresh_layout!!.isRefreshing = false
    }

    override fun onUpdateOptionsFail() {
        UMessage.showSnackBar(viewContent, applicationContext,
                getString(R.string.snackbar_error_db_store_favorite), Snackbar.LENGTH_SHORT)

    }

    /** GET MODULES & OPTIONS RESPONSE LISTENERS
     */
    override fun onMenuResponse(response: List<ModuleResponse>?) {
        // When it finish, update UI
        if (response!=null) {
            if(CollectionUtils.isNotEmpty(response)) {
                val modules = MapperUtil.mapToModule(response, this.currentUser.userId!!)

                val saveModuleAsyncTask = SaveModuleAsyncTask(this.appDatabase!!, this, modules,
                        this.currentUser.userId!!)
                saveModuleAsyncTask.execute()
            }
        } else {
            this.alert_swipe_refresh_layout!!.isRefreshing = false
            this.loadingProgressBar.visibility = View.GONE
            searchItem.isVisible = true
            UMessage.showSnackBar(viewContent, this,
                    getString(R.string.update_module_error),Snackbar.LENGTH_LONG)
        }
        searchItem.isVisible = true
    }

    override fun onMenuFailure(t: Throwable) {
        // check valid token
        super.onServiceFailure(t)

        this.alert_swipe_refresh_layout!!.isRefreshing = false
        this.loadingProgressBar.visibility = View.GONE
        val exceptionType = when(t){
            is CustomException -> t.message!!
            is ConnectException -> getString(R.string.snackbar_error_no_internet)
            else -> HttpURLConnection.HTTP_INTERNAL_ERROR.toString()
        }
        UMessage.showSnackBar(viewContent, this,
                getString(R.string.conection_error, exceptionType),Snackbar.LENGTH_LONG)
        searchItem.isVisible = true
    }

    override fun onLoadModuleFinish(list: List<ModuleWithOptions>) {
        // Update data from web service
        this.getModulesAsync()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.setupAdapter()
        this.initializeComponents()
        this.setPullAndRefresh()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }
            this.doubleBackToExitPressedOnce = true
            UMessage.showSnackBar(viewContent, this, R.string.main_back_again)
            val delayTime = resources.getInteger(R.integer.two_k)
            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, delayTime.toLong())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        val inflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        // Items
        deleteItem = menu.findItem(R.id.delete_option)
        //settingItem = menu.findItem(R.id.nav_settings)
        chgPassItem = menu.findItem(R.id.nav_change_password)
        exitItem = menu.findItem(R.id.nav_exit)
        searchItem = menu.findItem(R.id.action_search)

        // show menu & hide delete item
        showToolbarMenu(true)
        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        // Set listener
        searchView.setOnQueryTextListener(this.queryTextListener)
        // when close search bar
        searchView.addOnAttachStateChangeListener(object : OnAttachStateChangeListener {

            override fun onViewDetachedFromWindow(arg0: View) {
                // search was detached/closed
                moduleList.clear()
                moduleList.addAll(moduleListBU)
                adapter!!.notifyDataSetChanged()
                // show all favs
                for (i in 0 until optionsContainer.childCount) {
                    optionsContainer.getChildAt(i).visibility = View.VISIBLE
                }
                isSearching = false
                // show rest toolbar buttons
                chgPassItem.isVisible = true
                exitItem.isVisible = true
            }

            override fun onViewAttachedToWindow(arg0: View) {
                // hide rest toolbar buttons
                chgPassItem.isVisible = false
                exitItem.isVisible = false
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // Handle navigation view item clicks here.
        when (item!!.itemId) {
            R.id.nav_exit -> {
                val onClickListenerPositive =
                        DialogInterface.OnClickListener { _, _ ->
                    // accept logout
                    logout()
                }
                val onClickListenerNegative =
                        DialogInterface.OnClickListener { _, _ ->
                    // do nothing
                }
                UView.showGenericAlertDialog(this,
                        getString(R.string.log_out_title_alert),
                        getString(R.string.log_out_description_alert),
                        true,
                        getString(R.string.dialog_accept),
                        onClickListenerPositive,
                        getString(R.string.dialog_cancel),
                        onClickListenerNegative)
            }
            R.id.delete_option -> {
                val onClickListenerPositive =
                        DialogInterface.OnClickListener { _, _ ->
                    removeFavoriteOption(itemsToDeleteList)
                }
                val onClickListenerNegative =
                        DialogInterface.OnClickListener { _, _ ->
                    // do nothing
                }
                UView.showGenericAlertDialog(this, getString(R.string.delete_message_title),
                        getString(R.string.delete_message_subject),
                        true, getString(R.string.dialog_accept), onClickListenerPositive,
                        getString(R.string.dialog_cancel), onClickListenerNegative)
            }
            R.id.nav_change_password -> {
                val bundle = Bundle()
                bundle.putBoolean(BundleConstants.FROM_LOGIN_ACTIVITY_BUNDLE, false)
                this.startActivity(this, NewPasswordActivity::class.java, false, bundle)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setLocal(location: Location?) {
        if(location != null) {
            val pref = AppSharedPreference.getInstance(this).sharedPreferences
            // get nearest local
            currentLocal = LocalsRepository.getNearestLocal(pref, location)
            if (currentLocal != null) {
                // set location icon
                desktop_location_icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_location_on)!!)
                // set message
                val type = if (currentLocal!!.type != null) currentLocal!!.type else getString(R.string.empty)
                val name = if (currentLocal!!.name != null) currentLocal!!.name else getString(R.string.empty)
                val city = if (currentLocal!!.city != null) currentLocal!!.city else getString(R.string.empty)
                desktop_location.text = getString(R.string.local_location, type, name, city)
            } else {
                setDefaultLocation()
            }
        }else{
            setDefaultLocation()
        }
    }

    private fun setDefaultLocation(){
        val pref = AppSharedPreference.getInstance(this).sharedPreferences
        // in case user is out of rage -> local = default user local
        currentLocal = LocalsRepository.getUserDefaultLocal(pref)
        if (currentLocal != null) {
            // show user default local
            desktop_location.text = getString(R.string.local_location,
                    currentLocal!!.type, currentLocal!!.name, currentLocal!!.city)
        } else {
            // no local message
            desktop_location.text = getString(R.string.msg_no_local)
        }
        // set location icon
        desktop_location_icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_location_off_grey_24dp)!!)
    }

    private fun initializeComponents() {
        // set view for snackbar
        viewContent = findViewById(android.R.id.content)
        // Setup toolbar
        setSupportActionBar(toolbar)
        // Setup user data on toolbar
        this.toolbar.subtitle = WordUtils.capitalizeFully(this.currentUser.userCompleteName)
        // Setup UI listener
        this.itemContainer.setOnDragListener(this)
        // Load module and options
        this.loadDataFromDb()
        // set default location on init app
        setLocal(null)
    }

    private fun loadDataFromDb() {
        // Get modules async task
        val loadAsync = LoadModuleAsyncTask(this.appDatabase!!, this, this.currentUser.userId!!)
        // Execute it
        loadAsync.execute()
    }

    private fun getModulesAsync() {
        // Call ws to update modules
        ModulesServiceRepository.menu(this, this.currentUser.token!!, this)
    }

    private fun getCorpTokenAsync(option: Option?) {
        // set custom option
        this.selectedOption = option

        if(option!=null){
            // show progress bar
            showProgressBar()
            // set request
            val request = GetCorpTokenRequest()
            request.userId = currentUser.userId
            request.companyId = currentUser.companyId.toString()
            // call ws
            AccountServiceRepository.getCorpToken(this,
                    request, currentUser.token!!, this, option)
        }else{
            // show error message
            UMessage.showToast(this@MainActivity, getString(R.string.error_start_app_option))
        }
    }

    private fun showProgressBar(){
        alertDialog = UView.showLoadingAlertDialog(this,
                getString(R.string.loading_message), true)
    }

    private fun dismissProgressBar(){
        if(alertDialog!= null){
            UView.dismissAlertDialog(alertDialog)
        }
    }

    private fun setupAdapter() {
        this.adapter = ModuleAdapter(this, moduleList, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = this.adapter
    }

    private fun setPullAndRefresh(){
        // set pull&refresh
        this.alert_swipe_refresh_layout.setOnRefreshListener {
            // get alert ws
            if(isSearching){
                this.alert_swipe_refresh_layout!!.isRefreshing = false
            }else {
                searchItem.collapseActionView()
                searchItem.isVisible = false
                this.getModulesAsync()
            }
        }
    }

    private fun updateAdapter(list: List<ModuleWithOptions>) {
        // update data
        updatePersistenceModuleList(list)
        // update adapter
        this.adapter!!.notifyDataSetChanged()
        // hide progress bar
        this.alert_swipe_refresh_layout!!.isRefreshing = false
        this.loadingProgressBar.visibility = View.GONE

        // Validate if list its empty
        if (CollectionUtils.isNotEmpty(moduleList)) {
            this.empty_list_msg.visibility = View.GONE
            this.favLayout.visibility = View.VISIBLE
        }else{
            this.empty_list_msg.visibility = View.VISIBLE
            this.favLayout.visibility = View.GONE
        }
    }

    private fun setFavoritesOptions(list: List<ModuleWithOptions>){
        // reset fav container
        (this.optionsContainer as LinearLayout).removeAllViews()
        // set fav data on container
        for(module: ModuleWithOptions in list){
            if(module.options!=null) {
                loopModules(module)
            }
        }
        // set empty or not msg
        showEmptyFavMsg()
    }

    private fun loopModules(module: ModuleWithOptions) {
        for (option: Option in module.options!!) {
            if(option.isFavorite){
                val favView = ViewUtil.createOptionView(this, option, this)
                this.optionsContainer.addView(favView)

                FavRepository.addFavList(AppSharedPreference.getInstance(this).sharedPreferences,
                        option.optionId!!)
            }
        }
    }

    private fun setupAppBundle(optionList: List<Option>, token: String): Bundle {
        // Create a generic bundle for all applications
        val mBundle = Bundle()
        val bundleVO = BundleVO()
        val userVO = UserVO()
        val functionalities = ArrayList<String>()
        // Set user values
        userVO.code = this.currentUser.userId
        userVO.name = this.currentUser.userCompleteName
        userVO.profileId = this.currentUser.profileId
        userVO.companyId = this.currentUser.companyId.toString()

        if(currentLocal != null){
            bundleVO.localCode = currentLocal!!.id
            bundleVO.localRefCode = currentLocal!!.referenceCode
            bundleVO.localName = getString(R.string.local_location,
                    currentLocal!!.type, currentLocal!!.name, currentLocal!!.city)
            bundleVO.controllerServerAddress = currentLocal!!.ipLocal
            bundleVO.controllerServerPort = currentLocal!!.portLocal
        }

        bundleVO.corpToken = token
        bundleVO.viewCode = this.selectedOption!!.codigoVentana
        bundleVO.action = this.selectedOption!!.description
        for (option in optionList) {
            functionalities.add(option.description)
        }
        userVO.functionalities = functionalities
        bundleVO.user = userVO
        // Create a json
        val gson =GsonBuilder().serializeNulls().create()
        mBundle.putString(BundleConstants.DATA_EXTRA_BUNDLE, gson.toJson(bundleVO))
        return mBundle
    }

    private fun getOptionFromView(optionContainer: RelativeLayout): Option?{
        return if (optionContainer.tag is Option) {
            optionContainer.tag as Option
        }else{
            null
        }
    }

    private fun updateOptionInDB(option: Option) {
        val updateOptionsTask = UpdateFavOptionsAsyncTask(this.appDatabase!!, option,
                this.currentUser.userId!!, this)
        updateOptionsTask.execute()
    }

    private fun getOption(v: View): Option?{
        if (v.tag is Option) {
            val option = v.tag as Option
            val androidPackage = option.androidPackage
            return if (androidPackage!=null && androidPackage.isNotEmpty()) {
                this.selectedOption = option
                //this.launchApplication(option)
                option
            } else {
                null
            }
        }
        return null
    }

    private fun launchApplication(option: Option?, token: String?) {
        if(option != null && token != null) {
            val loadOptionsTask = LoadOptionsAsyncTask(this.appDatabase!!, option.moduleId,
                    this.currentUser.userId!!, this, token)
            loadOptionsTask.execute()
        }else{
            UMessage.showToast(this@MainActivity, getString(R.string.error_start_app_option))
            this.selectedOption = null
        }
    }

    private fun checkItem(v: View){
        val parentView = v.parent
        v.deleteCheckbox.visibility = View.VISIBLE
        v.logoImageView.alpha = BundleConstants.SELECTED_IMAGE_BLUR
        itemsToDeleteList.add((parentView as LinearLayout).indexOfChild(v))
    }

    private fun uncheckItem(v: View){
        val parentView = v.parent
        val index = (parentView as LinearLayout).indexOfChild(v)
        v.deleteCheckbox.visibility = View.GONE
        v.logoImageView.alpha = 1F
        // remove view from list
        for(item: Int in itemsToDeleteList){
            if(item == index){
                itemsToDeleteList.remove(item)
                break
            }
        }
        if(itemsToDeleteList.isEmpty()){
            showToolbarMenu(true)
        }
    }

    private fun removeFavoriteOption(toDeleteList: ArrayList<Int>) {
        val optionIdsList = ArrayList<Long>()
        toDeleteList.sortDescending()

        val optionsCodeList = ArrayList<RemoveFavoriteRequest>()

        for(item: Int in toDeleteList){
            // get fav option id to delete
            val optionView = (this.optionsContainer as LinearLayout)[item]
            val option = optionView.optionContainer.tag as Option

            val optionId = RemoveFavoriteRequest()
            optionId.codigoOpcion = option.optionId!!.toString()
            optionsCodeList.add(optionId)

            optionIdsList.add(option.optionId!!)
            // remove view
            (this.optionsContainer as LinearLayout).removeViewAt(item)
        }
        // delete from pref
        FavRepository.removeFav(AppSharedPreference.getInstance(this).sharedPreferences,
                toDeleteList)

        // set list to separated by commas string
        val listString = android.text.TextUtils.join(",", optionIdsList)
        // update in DB option favorite status
        val updateOptionsTask = UpdateFavOptionsListAsyncTask(this.appDatabase!!, listString,
                false)
        updateOptionsTask.execute()

        // call ws
        removeFavoriteAsync(optionsCodeList)

        toDeleteList.clear()
        showToolbarMenu(true)
        showEmptyFavMsg()
    }

    private fun hasAlreadyBeenAdded(option: Option) : Boolean{
        // search for coincidence on fav container
        for (i in 0 until this.optionsContainer.childCount) {
            val auxView = this.optionsContainer.getChildAt(i)
            val auxOption = auxView.optionContainer.tag as Option
            if(auxOption.optionId == option.optionId){
                return true
            }
        }
        return false
    }

    private fun showToolbarMenu(show: Boolean){
        deleteItem.isVisible = !show
        searchItem.isVisible = show
        chgPassItem.isVisible = show
        exitItem.isVisible = show
    }

    private fun showEmptyFavMsg(){
        // set empty msg
        if(this.optionsContainer.childCount==0){
            this.optionsContainerMsg.visibility = View.VISIBLE
        }else{
            this.optionsContainerMsg.visibility = View.GONE
        }
    }

    private fun updatePersistenceModuleList(list: List<ModuleWithOptions>){
        moduleList.clear()
        moduleListBU.clear()
        moduleList.addAll(list)
        moduleListBU.addAll(list)
    }

    private fun search(data: String){
        // search on adapter list
        val modules = ArrayList<ModuleWithOptions>()
        moduleList.clear()
        moduleList.addAll(moduleListBU)
        for(module: ModuleWithOptions in moduleList){
            if(module.options!=null) {
                loopOption(module, data, modules)
            }
        }
        // search on fav list
        for (i in 0 until this.optionsContainer.childCount) {
            val opt = this.optionsContainer.getChildAt(i).tag as Option
            this.optionsContainer.getChildAt(i).visibility =
            if(opt.name.toUpperCase().contains(data)){
                View.VISIBLE
            }else{
                View.GONE
            }
        }
        // set data
        moduleList.clear()
        moduleList.addAll(modules)
        adapter!!.notifyDataSetChanged()
        isSearching = data.isNotBlank()
    }

    private fun loopOption(module: ModuleWithOptions,
                           data: String,
                           modules: ArrayList<ModuleWithOptions>) {
        val options = ArrayList<Option>()
        for (option: Option in module.options!!){
            if(option.name.toUpperCase().contains(data)){
                options.add(option)
            }
        }
        if(options.isNotEmpty()){
            val m = ModuleWithOptions()
            m.module = module.module
            m.options = options
            modules.add(m)
        }
    }

    private fun addFavoriteAsync(request: ArrayList<AddFavoriteRequest>) {
        ModulesServiceRepository.addFavorite(this, request, this.currentUser.token!!, this)
    }

    private fun removeFavoriteAsync(request: ArrayList<RemoveFavoriteRequest>) {
        ModulesServiceRepository.removeFavorite(this, request, this.currentUser.token!!, this)
    }

    private fun undoAddFavorite(optionId: String, exceptionType: String) {
        // remove options added loop on fav container
        for (i in 0 until this.optionsContainer.childCount) {
            val auxView = this.optionsContainer.getChildAt(i)
            val auxOption = auxView.optionContainer.tag as Option
            if(auxOption.optionId.toString() == optionId){
                val toDeleteList = ArrayList<Int>()
                toDeleteList.add(i)
                removeFavoriteOption(toDeleteList)
                break
            }
        }
        // show snack bar
        UMessage.showSnackBar(viewContent, applicationContext, exceptionType, Snackbar.LENGTH_LONG)
    }

    private fun undoRemoveFavorite(exceptionType: String) {
        // show snack bar
        UMessage.showSnackBar(viewContent, applicationContext,
                getString(R.string.snackbar_error_store_favorite,exceptionType), Snackbar.LENGTH_LONG)
    }

    private fun logout(){
        // Clear shared preferences
        AppSharedPreference.getInstance(this).logoutClearPreference()
        // Call a ws to destroy spring security
        AccountServiceRepository.logout(this, this.currentUser.token!!)
        // Go to login screen
        this.startActivity(this, LoginActivity::class.java, true, null)
        // Finish this one
        finish()
    }

    override fun showError(){
        // show error message
        UMessage.showSnackBar(viewContent, applicationContext,
                getString(R.string.error_get_corp_token), Snackbar.LENGTH_SHORT)
    }
}
