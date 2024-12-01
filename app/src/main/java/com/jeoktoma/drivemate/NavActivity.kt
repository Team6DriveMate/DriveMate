package com.jeoktoma.drivemate

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import org.json.JSONObject


class NavActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var destinationInput: EditText
    private lateinit var searchResultsRecyclerView: RecyclerView
    private lateinit var startNavigationButton: Button // 길찾기 시작 버튼

    private var isPlaceSelected = false  // 장소가 선택되었는지 여부

    private val searchResults = mutableListOf<Pair<String, String>>() // 장소 이름과 도로명 주소 검색결과
    private val marker = Marker()

    private var searchJob: Job? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val CLIENT_ID = "ZMkVEifsKqqFiy0dkKGY" // 네이버 클라이언트 ID
        private const val CLIENT_SECRET = "YZ5Qyp4FNm" // 네이버 클라이언트 시크릿
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav)

        // 목적지 입력 창
        destinationInput = findViewById(R.id.destination_input)
        // 길찾기 버튼
        startNavigationButton = findViewById(R.id.start_navigation_button)

        searchResultsRecyclerView = findViewById(R.id.search_results_recycler_view)
        searchResultsRecyclerView.layoutManager = LinearLayoutManager(this)
        searchResultsRecyclerView.adapter = SearchResultsAdapter(searchResults) { selectedItem ->
            // 사용자가 검색 결과 중 하나를 클릭했을 때 처리하는 로직 (예: 지도에 마커 추가)
            destinationInput.setText(selectedItem.first) // 장소 이름을 입력 칸에 자동완성

            marker.map = null   //마커 제거

            // 도로명 주소로 좌표 API 호출
            CoroutineScope(Dispatchers.Main).launch {
                val coordResponse =
                    performPathService(selectedItem.second, baseContext)  // 도로명 주소를 사용하여 좌표 요청
                if (coordResponse != null) {
                    // 좌표 정보를 성공적으로 받아온 경우 처리
                    val latitude = coordResponse.lat
                    val longitude = coordResponse.lng
                    //지도에 마커 표시
                    marker.position = LatLng(latitude, longitude)
                    marker.captionText = selectedItem.first
                    marker.captionTextSize = 16f
                    marker.icon = MarkerIcons.BLACK
                    marker.iconTintColor = Color.parseColor("#92A3FD")
                    marker.isHideCollidedSymbols = true
                    marker.map = naverMap

                    //카메라 위치 변경
                    naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(latitude,longitude)))

                    isPlaceSelected = true  // 장소가 선택되었으면 true

                    searchResults.clear() // 검색 결과 초기화
                    searchResultsRecyclerView.adapter?.notifyDataSetChanged()
                    searchResultsRecyclerView.visibility = View.GONE

                    // 길찾기 시작 버튼을 보이게 설정
                    startNavigationButton.visibility = View.VISIBLE

                    // 포커스 해제
                    destinationInput.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(destinationInput.windowToken, 0)
                }
            }

        }

        // 지도 Fragment 설정
        val fm = supportFragmentManager
        var mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance()
            fm.beginTransaction().add(R.id.map_fragment, mapFragment).commit()
        }

        mapFragment?.getMapAsync(this)

        // FusedLocationSource 초기화
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        // 목적지 입력 처리 (키보드 입력이 들어올 때마다 Debounce 처리)
        destinationInput.addTextChangedListener { editable ->
            val destination = editable?.toString() ?: ""

            startNavigationButton.visibility = View.GONE
            searchResultsRecyclerView.visibility = View.VISIBLE

            // 기존의 검색 작업이 있으면 취소하고 새로운 작업 시작 (Debounce 처리)
            searchJob?.cancel()
            searchJob = CoroutineScope(Dispatchers.Main).launch {
                delay(300) // 0.3초 동안 추가 입력이 없으면 검색 수행
                if (destination.isNotEmpty()) {
                    searchLocation(destination) // POI 검색 수행
                }
            }
        }


        // 길찾기 시작 버튼 클릭 시 MapActivity로 이동
        startNavigationButton.setOnClickListener {
            val cur_lat = locationSource.lastLocation?.latitude
            val cur_lng = locationSource.lastLocation?.longitude

            val intent = Intent(this@NavActivity, MapActivity::class.java)

            // 필요시 좌표나 기타 데이터를 전달할 수 있습니다.
            intent.putExtra("start_lat", cur_lat)
            intent.putExtra("start_lng", cur_lng)
            intent.putExtra("latitude", marker.position.latitude)
            intent.putExtra("longitude", marker.position.longitude)

            startActivity(intent)  // MapActivity로 전환
            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource

        val uiSettings = naverMap.uiSettings
        //uiSettings.isLocationButtonEnabled = true

        naverMap.locationTrackingMode = LocationTrackingMode.Follow
        naverMap.locationTrackingMode = LocationTrackingMode.NoFollow
    }

    // 장소 검색 함수 (네이버 검색 API 사용)
    private fun searchLocation(keyword: String) {
        val url = "https://openapi.naver.com/v1/search/local.json?query=$keyword&display=5"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .addHeader("X-Naver-Client-Id", CLIENT_ID)
            .addHeader("X-Naver-Client-Secret", CLIENT_SECRET)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@NavActivity, "검색 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val jsonObject = JSONObject(responseBody ?: "")
                    val items = jsonObject.getJSONArray("items")

                    runOnUiThread {
                        searchResults.clear() // 이전 검색 결과 초기화

                        for (i in 0 until items.length()) {
                            val item = items.getJSONObject(i)
                            var title = item.getString("title").replace(Regex("<.*?>"), "") // 모든 HTML 태그 제거
                            val roadAddress = item.getString("roadAddress") // 도로명 주소
                            searchResults.add(Pair(title, roadAddress)) // 장소 이름과 도로명 주소를 함께 저장
                        }

                        searchResultsRecyclerView.adapter?.notifyDataSetChanged() // RecyclerView 업데이트
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@NavActivity, "검색 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}


class SearchResultsAdapter(
    private val searchResults: List<Pair<String, String>>,  // 장소 이름과 도로명 주소를 함께 저장하는 리스트 (Pair)
    private val onItemClick: (Pair<String, String>) -> Unit  // 클릭 이벤트 처리 콜백 함수 정의 (Pair로 전달)
) : RecyclerView.Adapter<SearchResultsAdapter.SearchResultViewHolder>() {

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val resultPair = searchResults[position]
        holder.resultText.text = "${resultPair.first}" // 검색 결과 표시

        holder.itemView.setOnClickListener {
            onItemClick(resultPair)  // 아이템 클릭 시 콜백 호출하여 처리
        }
    }

    class SearchResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val resultText: TextView = view.findViewById(R.id.result_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_result_item, parent, false)

        return SearchResultViewHolder(view)
    }

    override fun getItemCount(): Int {
        return searchResults.size
    }
}