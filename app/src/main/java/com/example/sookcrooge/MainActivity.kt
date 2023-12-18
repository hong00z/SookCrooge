package com.example.sookcrooge

import android.R
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.sookcrooge.databinding.AccountListBinding
import com.example.sookcrooge.databinding.ActivityMainBinding
import com.example.sookcrooge.databinding.NavigationHeaderBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.storage
import com.navercorp.nid.NaverIdLoginSDK
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date


class MainActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    val db = Firebase.firestore
    lateinit var filePath: String
    var initTime = 0L
    lateinit var binding: ActivityMainBinding
    lateinit var viewHeader: View
    lateinit var navViewHeaderBinding: NavigationHeaderBinding
    lateinit var userUID: String
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.profile.setOnClickListener{
            val navDrawer = binding.drawer
            if(!navDrawer.isDrawerOpen(GravityCompat.START)) navDrawer.openDrawer(GravityCompat.START);
            else navDrawer.closeDrawer(GravityCompat.END)
        }
        val currentTime=System.currentTimeMillis()
        val currentYear= SimpleDateFormat("yyyy").format(currentTime)
        val currentMonth= SimpleDateFormat("MM").format(currentTime)
        val testDate = Date(currentYear.toInt()-1900, currentMonth.toInt()-1, 1)
        val firstDayOfMonth = Timestamp(testDate)
        val bestSave = mutableListOf<accountItem>()
        val worstSpend= mutableListOf<accountItem>()
        var tempBestSave: accountItem? =null
        var tempWorstSpend:accountItem? = null
        var maxSmile=0
        var maxSmileName=""
        var maxAngry=0
        var maxAngryName=""
        db.collection("users").get().addOnSuccessListener {documents->
            for (document in documents)
            {
                db.collection("users").document(document.id).collection("accountBook").whereGreaterThanOrEqualTo("date",firstDayOfMonth).get().addOnSuccessListener{
                    for (item in it)
                    {
                        if (item.data?.get("type").toString()=="spend" && item.data?.get("angry").toString().toInt() > maxAngry)
                        {
                            maxAngryName=document.data?.get("nickname").toString()
                            maxAngry=item.data?.get("angry").toString().toInt()

                            var timeStamp = item.data["date"] as Timestamp
                            val timeStampLong=timeStamp.seconds*1000+32400000
                            val month= SimpleDateFormat("MM").format(timeStampLong)
                            val day= SimpleDateFormat("dd").format(timeStampLong)
                            tempWorstSpend=(accountItem(item.id, item.data["name"].toString(), item.data["cost"].toString().toInt(), month, day,
                                item.data["type"].toString(), item.data["angry"].toString().toInt(), item.data["smile"].toString().toInt(), item.data["tag"].toString()))
                        }
                        if (item.data?.get("type").toString()=="save" && item.data?.get("smile").toString().toInt() > maxSmile)
                        {
                            maxSmileName=document.data?.get("nickname").toString()
                            maxSmile=item.data?.get("smile").toString().toInt()
                            var timeStamp = item.data["date"] as Timestamp
                            val timeStampLong=timeStamp.seconds*1000+32400000
                            val month= SimpleDateFormat("MM").format(timeStampLong)
                            val day= SimpleDateFormat("dd").format(timeStampLong)
                            tempBestSave=(accountItem(item.id, item.data["name"].toString(), item.data["cost"].toString().toInt(), month, day,
                                item.data["type"].toString(), item.data["angry"].toString().toInt(), item.data["smile"].toString().toInt(), item.data["tag"].toString()))
                        }

                    }
                    if (tempWorstSpend!=null)
                    {
                        if (worstSpend.size==1)
                        {
                            worstSpend.removeAt(0)
                        }
                        worstSpend.add(tempWorstSpend!!)
                        binding.worstSpend.layoutManager = LinearLayoutManager(this)
                        binding.worstSpend.adapter = OthersAccountAdapter(worstSpend)
                        binding.worstSpendUserName.text = maxAngryName+" 님의 "
                        (binding.worstSpend.adapter as OthersAccountAdapter).setItemClickListener(object: OthersAccountAdapter.OnItemClickListener{
                            override fun onSmileClick(binding: AccountListBinding, data: accountItem) {
                                return
                            }
                            override fun onAngryClick(binding: AccountListBinding, data: accountItem)
                            {
                                return
                            }
                        })
                    }
                    if (tempBestSave !=null)
                    {
                        if (bestSave.size==1)
                        {
                            bestSave.removeAt(0)
                        }
                        bestSave.add(tempBestSave!!)
                        binding.bestSave.layoutManager = LinearLayoutManager(this)
                        binding.bestSave.adapter = OthersAccountAdapter(bestSave)
                        binding.bestSaveUserName.text = maxSmileName+" 님의 "
                        (binding.bestSave.adapter as OthersAccountAdapter).setItemClickListener(object: OthersAccountAdapter.OnItemClickListener{
                            override fun onSmileClick(binding: AccountListBinding, data: accountItem) {
                                return
                            }
                            override fun onAngryClick(binding: AccountListBinding, data: accountItem)
                            {
                                return
                            }
                        })
                    }
                }
            }

        }

        viewHeader = binding.mainDrawerView.getHeaderView(0)
        navViewHeaderBinding = NavigationHeaderBinding.bind(viewHeader)

        val requestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode === android.app.Activity.RESULT_OK) {
                Glide.with(getApplicationContext()).load(it.data?.data).apply(RequestOptions().override(200, 200))
                    .centerCrop().into(navViewHeaderBinding.profileImage)

                val cursor = contentResolver.query(it.data?.data as Uri,
                    arrayOf<String>(MediaStore.Images.Media.DATA), null, null, null
                )
                cursor?.moveToFirst().let {
                    filePath = cursor?.getString(0) as String
                }

                var userUID:String
                if (loginInformation?.loginType == loginUser.naverLogin)
                {
                    userUID=loginInformation.currentLoginUser!!.uid
                }
                else
                {
                    userUID=Firebase.auth.currentUser!!.uid
                }
                val storageRef = com.google.firebase.Firebase.storage.reference
                val imgRef = storageRef.child("images/${userUID}.png")
                val file = Uri.fromFile(File(filePath))
                imgRef.putFile(file)
                var map= mutableMapOf<String,Any>()
                map["photoURL"] ="images/${userUID}.png"
                db.collection("users").document(userUID).update(map)
            }

        }

        navViewHeaderBinding.changeProfile.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )
            requestLauncher.launch(intent)

        }

        binding.logout.setOnClickListener{
            if (loginInformation.loginType==loginUser.naverLogin)
            {
                NaverIdLoginSDK.logout()
            }
            else if (loginInformation.loginType==loginUser.email)
            {
                Firebase.auth.signOut()
            }
            val intent = Intent(this, InitialActivity::class.java)
            startActivity(intent)
        }

        binding.editProfile.setOnClickListener{
            val intent = Intent(this, EditProfile::class.java)
            startActivity(intent)
        }

        binding.accountBook.setOnClickListener{
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }

        binding.chatting.setOnClickListener{
            val intent=Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }

        db.collection("users").whereEqualTo("uid", loginInformation.currentLoginUser!!.uid).get().addOnSuccessListener{
            if (it != null)
            {
                navViewHeaderBinding.nickname.text= it.documents[0].data?.get("nickname").toString()
                val photoURL=it.documents[0].data?.get("photoURL").toString()
                if (photoURL!= "null") {
                    val imgRef = com.google.firebase.Firebase.storage.reference.child(photoURL)
                    imgRef.downloadUrl.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Glide.with(this).load(task.result)
                                .into(navViewHeaderBinding.profileImage)
                        }
                    }
                }
            }

        }
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(System.currentTimeMillis() - initTime > 3000){
                Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르면 \n 종료됩니다.", Toast.LENGTH_SHORT).show()
                initTime = System.currentTimeMillis()
                return true //false면 back버튼 기능 수행.
            }
            else
            {
                ActivityCompat.finishAffinity(this)
                System.runFinalization()
                System.exit(0)
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}