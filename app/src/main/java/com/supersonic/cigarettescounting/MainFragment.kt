package com.supersonic.cigarettescounting

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.supersonic.cigarettescounting.databinding.FragmentMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment() {

    companion object{
        private const val QUANTITY_DEFAULT_VALUE = 1
        private const val MINUS = "minus"
        private const val PLUS = "plus"
    }

    private lateinit var preferences: SharedPreferences
    private lateinit var binding: FragmentMainBinding

    private var plusMinus = PLUS
    private var quantity = 0
    private var stepCountPlus = 1
    private var stepCountMinus = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)

        preferences = this.requireActivity().getPreferences(Context.MODE_PRIVATE)
        quantity = preferences.getInt("quantity", QUANTITY_DEFAULT_VALUE)
        binding.quantityTextView.text = quantity.toString()
        binding.plusRadioGroup.check(preferences.getInt("plusRadioButtonId", R.id.plusRadioButton1))
        binding.minusRadioGroup.check(preferences.getInt("minusRadioButtonId", R.id.minusRadioButton1))
        binding.timeTextView.text = "Last Update: ${preferences.getString("lastUpdatedTime", "00:00")}"

        binding.plusRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            preferences.edit().putInt("plusRadioButtonId", checkedId).apply()
        }
        binding.minusRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            preferences.edit().putInt("minusRadioButtonId", checkedId).apply()
        }

        binding.switch3.setOnClickListener {view: View ->
            plusMinus = if (plusMinus == PLUS){
                MINUS
            }else{
                PLUS


            }
        }

        binding.quantityTextView.setOnClickListener { view: View ->
            if (plusMinus == PLUS && quantity < 1000){
                addQuantity()
                updateTime()
            }else if (plusMinus == MINUS){
                decreaseQuantity()
                if (quantity !=0){
                    updateTime()
                }
            }

        }

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun addQuantity(){

        if (binding.plusRadioGroup.visibility == View.VISIBLE){

            val selectedCheckBoxID = binding.plusRadioGroup.checkedRadioButtonId

            if (selectedCheckBoxID != -1){
                when (selectedCheckBoxID){
                    R.id.plusRadioButton1 -> stepCountPlus = 1
                    R.id.plusRadioButton2 -> stepCountPlus = 3
                    R.id.plusRadioButton3 -> stepCountPlus = 5
                    R.id.plusRadioButton4 -> stepCountPlus = 10
                }
            }
        }


        quantity+=stepCountPlus
        binding.quantityTextView.text = quantity.toString()
        preferences.edit().putInt("quantity", quantity).apply()

    }

    private fun decreaseQuantity(){

        if (binding.minusRadioGroup.visibility == View.VISIBLE){

            val selectedCheckBoxID = binding.minusRadioGroup.checkedRadioButtonId

            if (selectedCheckBoxID != -1){
                when (selectedCheckBoxID){
                    R.id.minusRadioButton1 -> stepCountMinus = 1
                    R.id.minusRadioButton2 -> stepCountMinus = 3
                    R.id.minusRadioButton3 -> stepCountMinus = 5
                    R.id.minusRadioButton4 -> stepCountMinus = 10
                }
            }
        }



        if (quantity - stepCountMinus < 0){
            resetQuantity()
        } else {
            quantity-=stepCountMinus
            binding.quantityTextView.text = quantity.toString()
            preferences.edit().putInt("quantity", quantity).apply()
        }

    }

    private fun resetQuantity(){

        quantity = 0
        binding.quantityTextView.text = quantity.toString()
        preferences.edit().putInt("quantity", quantity).apply()



    }

    private fun resetQuantityWithDialog(){
        if (quantity > 0){
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Do you want to reset the counted quantity?")
                .setPositiveButton("YES", DialogInterface.OnClickListener{ dialog, id ->
                    resetQuantity()
                    updateTime()
                })
                .setNegativeButton("NO", DialogInterface.OnClickListener{ dialog, id ->

                })
            builder.create().show()
        }
    }

    private fun updateTime(){
        val simpleDateFormat = SimpleDateFormat("HH:mm").format(Date())
        binding.timeTextView.text = "Last Update: $simpleDateFormat"
        preferences.edit().putString("lastUpdatedTime", simpleDateFormat).apply()
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menuResetButton -> {
                resetQuantityWithDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
       }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadSettings()
    }

    private fun loadSettings() {
        val sp: SharedPreferences? = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }

        val stepsShowing:Boolean = sp!!.getBoolean("steps_check_box", true)
        val stepAdd: String? = sp.getString("adding_preference", "1")
        val stepDec: String? = sp.getString("decrease_preference", "1")

        if (!stepsShowing){

            binding.minusRadioGroup.visibility = View.VISIBLE
            binding.plusRadioGroup.visibility = View.VISIBLE

        }else{

            if (stepAdd != null && stepDec != null) {
                stepCountPlus = stepAdd.toInt()
                stepCountMinus = stepDec.toInt()
            }
            binding.minusRadioGroup.visibility = View.GONE
            binding.plusRadioGroup.visibility = View.GONE
        }

    }
}