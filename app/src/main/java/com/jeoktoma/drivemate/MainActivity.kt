package com.jeoktoma.drivemate

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import com.kakaomobility.knsdk.KNLanguageType
import java.security.MessageDigest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val intent = Intent(this, NavActivity::class.java)
//        startActivity(intent)
//        finish()
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.d("HashKey", "Hash Key: $hashKey")
            }
        } catch (e: Exception) {
            Log.e("HashKey", "Hash Key Error: ${e.toString()}")
        }

        checkPermission()
    }

    /**
     * GPS 위치 권한을 확인합니다.
     */
    fun checkPermission() {
        when {
            checkSelfPermission(
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED -> {
                // GPS 퍼미션 체크
            }

            else -> {
                // 길찾기 SDK 인증
                knsdkAuth()
            }
        }
    }

    /**
     * GPS 위치 권한을 요청합니다.
     */
    fun gpsPermissionCheck() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            1234)
    }

    /**
     * GPS 위치 권한 요청의 실패 여부를 확인합니다.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1234 -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // 다시 권한 요청하는 곳으로 돌아갑니다.
                    checkPermission()
                }
            }
        }
    }

    /**
     * 길찾기 SDK 인증을 진행합니다.
     */
    fun knsdkAuth() {
        KakaoNavi.knsdk.apply {
            initializeWithAppKey(
                aAppKey = "704f94e0ccd82aa85319a646ada05bcf",       // 카카오디벨로퍼스에서 부여 받은 앱 키
                aClientVersion = "1.9.4",                                               // 현재 앱의 클라이언트 버전
                aUserKey = "1157097",                                                  // 사용자 id
                aLangType = KNLanguageType.KNLanguageType_KOREAN,   // 언어 타입
                aCompletion = {

                    // Toast는 UI를 갱신하는 작업이기 때문에 UIThread에서 동작되도록 해야 합니다.
                    runOnUiThread {
                        if (it != null) {
                            Toast.makeText(applicationContext, "인증에 실패하였습니다", Toast.LENGTH_LONG).show()

                        } else {
                            Toast.makeText(applicationContext, "인증 성공하였습니다", Toast.LENGTH_LONG).show()

                            var intent = Intent(this@MainActivity, KakaoActivity::class.java)
                            this@MainActivity.startActivity(intent)
                        }
                    }
                })
        }
    }

}




