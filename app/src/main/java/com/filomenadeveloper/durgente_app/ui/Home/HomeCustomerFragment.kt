package com.filomenadeveloper.durgente_app.ui.Home

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import co.ceryle.radiorealbutton.RadioRealButtonGroup
import com.filomenadeveloper.durgente_app.Common
import com.filomenadeveloper.durgente_app.Model.Solicitation
import com.filomenadeveloper.durgente_app.R
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class HomeCustomerFragment : Fragment(), OnMapReadyCallback {

    private lateinit var vendaViewModel: VendaViewModel
    private lateinit var mMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mBringUpBottomLayout: LinearLayout
    private lateinit var mBottomSheet: View
    private var mBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationCallback: LocationCallback
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var mRequest : Button
    private lateinit var mRadioButton: RadioRealButtonGroup
    private lateinit var requestService: String

    //sistema online
    private lateinit var onlineRef: DatabaseReference
    private lateinit var correntUserRef: DatabaseReference
    private lateinit var correnRequestRef: DatabaseReference
    private lateinit var customerLocationRef: DatabaseReference
    private lateinit var geofire: GeoFire



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vendaViewModel = ViewModelProvider(this).get(VendaViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_main, container, false)

        correnRequestRef = FirebaseDatabase.getInstance().reference
        init()

        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

         mBringUpBottomLayout = root.findViewById(R.id.bringUpBottomLayout) as LinearLayout
        mBringUpBottomLayout.setOnClickListener {

            if (mBottomSheetBehavior!!.state != BottomSheetBehavior.STATE_EXPANDED)
                mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
            else
                mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        mBottomSheet = root.findViewById(R.id.bottomSheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet)
        (mBottomSheetBehavior as BottomSheetBehavior<View>).isHideable = false
        mBottomSheetBehavior!!.peekHeight = 120


        mRequest = root.findViewById(R.id.request)
        mRadioButton = root.findViewById(R.id.radioRealButtonGroup)
        mRadioButton.position = 0

        mRequest.setOnClickListener {

            when(mRadioButton.position){
                0 -> requestService = "116"
                1 -> requestService = "113"
                2 -> requestService = "115"
            }

            mRequest.text = "Chamando por $requestService"
            val model = Solicitation()
            model.orgao = requestService
            model.solicitant = FirebaseAuth.getInstance().currentUser.uid
            model.local = "Luanda"
            model.datah = getCurrentDate()
            correnRequestRef.child(Common.OCORRENCIA_REF).push().setValue(model).
            addOnSuccessListener {

            }.addOnFailureListener {

            }

        }

        return root
    }

    var previousRequestBol = true

    private val onlineValueEventListenr = object : ValueEventListener{
        override fun onCancelled(error: DatabaseError) {
            Snackbar.make(mapFragment.requireView(),error.message,Snackbar.LENGTH_SHORT).show()
        }

        override fun onDataChange(snapshot: DataSnapshot) {
         correntUserRef.onDisconnect().removeValue()
        }

    }

    override fun onDestroy() {
        fusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
    try {
    geofire.removeLocation(FirebaseAuth.getInstance().currentUser.uid)
    onlineRef.removeEventListener(onlineValueEventListenr)

    }catch (e: Exception){

   }

        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
    }


    @SuppressLint("MissingPermission", "UseRequireInsteadOfGet")
    private fun init() {

        onlineRef = FirebaseDatabase.getInstance().reference.child(".info/conectado")
        customerLocationRef = FirebaseDatabase.getInstance().reference.child(Common.CUSTOMER_LOCATION_REF)
        
        correntUserRef = FirebaseDatabase.getInstance().reference
                .child(Common.CUSTOMER_LOCATION_REF)
                .child(FirebaseAuth.getInstance().currentUser.uid)
        geofire = GeoFire(customerLocationRef)


        mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.fastestInterval = 3000
        mLocationRequest.interval = 5000
        mLocationRequest.smallestDisplacement = 10f

        mLocationCallback = object: LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)

                val newPos = LatLng(locationResult!!.lastLocation.latitude,locationResult!!.lastLocation.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPos,18f))

                //Atualizar a localizacao
                geofire.setLocation(FirebaseAuth.getInstance().currentUser.uid,
                        GeoLocation(locationResult!!.lastLocation.latitude,locationResult!!.lastLocation.longitude)
                ){ key: String?, error: DatabaseError? ->
                    if (error != null)
                        Snackbar.make(mapFragment.requireView(),error.message,Snackbar.LENGTH_SHORT).show()

                }

            }
        }


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context!!)
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest,mLocationCallback,Looper.myLooper())
    }

    fun getCurrentDate(): String {
        val data: Date = Calendar.getInstance().time
        val formatter: SimpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
        val tody: String = formatter.format(data)

        val currentDataTime: Calendar = Calendar.getInstance()
        val hora: SimpleDateFormat = SimpleDateFormat("hh:mm a")
        val currentTime: String = hora.format(currentDataTime.time)
        return "$tody $currentTime"
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!
        val success = googleMap.setMapStyle(MapStyleOptions(resources
                .getString(R.string.style_json)))

      Dexter.withContext(requireContext()).withPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
              .withListener(object : PermissionListener{
                  @SuppressLint("MissingPermission")
                  override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    mMap.isMyLocationEnabled = true
                    mMap.uiSettings.isMyLocationButtonEnabled = true
                      mMap.setOnMyLocationClickListener {
                          fusedLocationProviderClient.lastLocation
                                  .addOnFailureListener {  }.addOnSuccessListener {
                                      val useLng = LatLng(it.latitude, it.longitude)
                                      mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(useLng,18f))
                                  }
                          true
                      }

                      val locationButton = (mapFragment.requireView()!!.findViewById<View>("2".toInt()))
                      val params = locationButton.layoutParams as RelativeLayout.LayoutParams
                      params.addRule(RelativeLayout.ALIGN_TOP)
                      params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE)
                      params.bottomMargin = 50
                  }

                  override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, p1: PermissionToken?) {
                      TODO("Not yet implemented")
                  }

                  override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                      Toast.makeText(context,"Permissão"+p0!!.permissionName+"Nós Permite",Toast.LENGTH_LONG).show()
                  }

              }).check()
    }


}