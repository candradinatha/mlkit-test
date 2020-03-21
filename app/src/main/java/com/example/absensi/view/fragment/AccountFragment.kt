package com.example.absensi.view.fragment


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper
import com.example.absensi.R
import com.example.absensi.view.AddPersonPreviewActivity
import com.example.absensi.view.activity.TrainingActivity
import kotlinx.android.synthetic.main.fragment_account.*
import java.io.File

/**
 * A simple [Fragment] subclass.
 */
class AccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        person_name_layout.editText?.addTextChangedListener(object : TextWatcher{
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                person_name_layout.error = null
                person_name_layout.isErrorEnabled = false
            }
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
        })
        button_training.setOnClickListener {
            startActivity(Intent(context, TrainingActivity::class.java))
        }
        button_action_add_face.setOnClickListener {
            if (isNameAlreadyUsed(FileHelper().trainingList, person_name_layout.editText?.text.toString()))
                Toast.makeText(context, "This name is already used. Please choose another one.", Toast.LENGTH_SHORT).show()
            else
                intentAddPerson()
        }
    }

    private fun intentAddPerson() {
        val intent = Intent(context, AddPersonPreviewActivity::class.java)
        intent.putExtra("Name", person_name_layout.editText?.text.toString())
        intent.putExtra("Folder", "Training")
        startActivity(intent)
    }


    private fun isNameAlreadyUsed(list: Array<File>?, name: String): Boolean {
        var used = false
        if (list != null && list.size > 0) {
            for (person in list) {
                // The last token is the name --> Folder name = Person name
                val tokens = person.absolutePath.split("/").toTypedArray()
                val foldername = tokens[tokens.size - 1]
                if (foldername == name) {
                    used = true
                    break
                }
            }
        }
        return used
    }

}
