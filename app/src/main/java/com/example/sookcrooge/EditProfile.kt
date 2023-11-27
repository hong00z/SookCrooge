package com.example.sookcrooge

import android.app.ProgressDialog.show
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.example.sookcrooge.databinding.ActivityEditProfileBinding
import com.example.sookcrooge.databinding.DeleteAccountDialogBinding

class EditProfile : AppCompatActivity() {
    val binding: ActivityEditProfileBinding by lazy {
        ActivityEditProfileBinding.inflate(layoutInflater)
    }

    private lateinit var name:String
    private lateinit var nickname:String
    private lateinit var password:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.editProfileButton.setOnClickListener{
            if (isAllConditionQualified())
            {
                name = binding.editInputName.text.toString()
                //추후 구현: 바뀐 데이터값을 DB에 저장
                finish()
            }

        }

        binding.deleteAccountButton.setOnClickListener{
            val dialogBinding = DeleteAccountDialogBinding.inflate(layoutInflater)
            val deleteAccountDialog = AlertDialog.Builder(this).run {
                setTitle("회원 탈퇴")
                setMessage("정말로 탈퇴하시겠습니까?")
                setView(dialogBinding.root)
                show()

            }
            dialogBinding.deleteAccountNo.setOnClickListener {
                deleteAccountDialog.dismiss()
            }
            dialogBinding.deleteAccountYes.setOnClickListener {
                //추후 구현: 해당 회원의 정보를 삭제
            }
        }


    }

    private fun isAllConditionQualified():Boolean
    {
        nickname = binding.editInputNickname.text.toString()
        password = binding.editInputPassword.text.toString()

        //추후 구현: nickname이 DB에 저장된 닉네임들과 겹치는지 확인하고 겹치면 해당 오류 메시지 띄우기
        //추후 구현: password가 일치하는지 확인하고 아니라면 해당 오류 메시지 띄우기

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}