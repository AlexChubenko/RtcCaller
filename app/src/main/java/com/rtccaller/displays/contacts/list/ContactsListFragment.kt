package com.rtccaller.displays.contacts.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.rtccaller.R
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class ContactsListFragment : Fragment(){

    private lateinit var rootView: View
    private var contactsListAdapterPagerAdapter: ContactsListAdapter? = null
    private var mContactsListViewModel: ContactsListViewModel? = null

    @set:Inject
    var viewModelFactory: ViewModelProvider.Factory? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_contacts_list, container, false)
        setHasOptionsMenu(true)
//        checkRequiredPermissions()
        //todo keep this experimental
        tuneViewModel()
        initContactsRecyclerAdapterPager()
//        tuneSwipeRefreshLayout(rootView!!)
        return rootView
    }

    private fun tuneViewModel() {
        mContactsListViewModel = ViewModelProviders
            .of(this, viewModelFactory).get(ContactsListViewModel::class.java)

        startObservation()
    }

    private fun startObservation(){
//        //todo keep this experimental
//        mContactsListViewModel.cityNodesListLiveData.observe(this,  Observer<List<CityNode>> {
//            Log.d(TAG, "aChub mCitiesForecastSummaryViewModel.cityNodesListLiveData.observe")
//            it.forEach{ Log.d(TAG, it.city.name)}
//            renewViewPager(it)
//        })
//        mContactsListViewModel.errorMutableLiveData.observe(this, Observer<Error> { this.actOnFailure(it) })
//        mContactsListViewModel.searchedCityPersistanceListLiveData.observe(this, Observer<List<CityPersistance>> {
//            Log.d(TAG, "aChub mCitiesForecastSummaryViewModel.searchedCityPersistanceListLiveData.observe")
//            this.updateDropDownList(it)
//        })
    }

    private fun initContactsRecyclerAdapterPager() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    companion object{
        private val TAG = ContactsListFragment::class.java.simpleName
    }
}