package com.yhw.taglayout.sample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yhw.taglayout.TagAdapter
import com.yhw.taglayout.TagLayout

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tagLayout1 = findViewById<TagLayout>(R.id.tag_layout_1)
        val tagLayout2 = findViewById<TagLayout>(R.id.tag_layout_2)
        val getIndexBtn = findViewById<Button>(R.id.btn_get_index)

        tagLayout1.onItemClickListener = object : TagLayout.OnItemClickListener {
            override fun onItemClick(position: Int, view: View) {
                when (position) {
                    0 -> tagLayout2.setChoiceMode(TagLayout.ChoiceMode.None)
                    1 -> tagLayout2.setChoiceMode(TagLayout.ChoiceMode.SingleChoice)
                    2 -> tagLayout2.setChoiceMode(TagLayout.ChoiceMode.MultipleChoice)
                }
            }
        }

        val list = mutableListOf("陕西西安", "新疆乌鲁木齐", "北京天安门", "西安大雁塔", "秦始皇兵马俑", "内蒙古呼伦贝尔大草原", "山西大同")
        tagLayout2.setAdapter(MyAdapter(list))
        //单击事件
        tagLayout2.onItemClickListener = object : TagLayout.OnItemClickListener {
            override fun onItemClick(position: Int, view: View) {
                Toast.makeText(this@MainActivity, "点击了 $position", Toast.LENGTH_SHORT).show()
            }
        }
        //长按事件
        tagLayout2.onItemLongClickListener = object : TagLayout.OnItemLongClickListener {
            override fun onItemLongClick(position: Int, view: View) {
                Toast.makeText(this@MainActivity, "长按了 $position", Toast.LENGTH_SHORT).show()
            }
        }
        //单选监听
        tagLayout2.onSingleCheckedChangeListener =
            object : TagLayout.OnSingleCheckedChangeListener {
                override fun onCheckedChanged(position: Int) {
                    Log.i(TAG, "选中了 $position")
                }
            }
        //多选监听
        tagLayout2.onMultipleCheckedChangeListener =
            object : TagLayout.OnMultipleCheckedChangeListener {
                override fun onCheckedChanged(positionList: MutableList<Int>) {
                    for (position in positionList) {
                        Log.i(TAG, "选中了 $position")
                    }
                }
            }

        //获取选中项
        getIndexBtn.setOnClickListener {
            if (tagLayout2.getChoiceMode() == TagLayout.ChoiceMode.SingleChoice.choiceMode) {
                Toast.makeText(
                    this@MainActivity,
                    "单选了 ${tagLayout2.getCheckedPosition()}",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (tagLayout2.getChoiceMode() == TagLayout.ChoiceMode.MultipleChoice.choiceMode) {
                Toast.makeText(
                    this@MainActivity,
                    "多选了 ${tagLayout2.getCheckedList()}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this@MainActivity, "非选中模式，不能获取", Toast.LENGTH_SHORT).show()
            }
        }
    }

    class MyAdapter(private val dataList: MutableList<String>) : TagAdapter() {
        override fun onCreateView(parent: ViewGroup): View {
            return LayoutInflater.from(parent.context)
                .inflate(R.layout.tag_item_layout, parent, false)
        }

        override fun onBindView(itemView: View, position: Int) {
            val textView: TextView = itemView.findViewById(R.id.tv_title)
            textView.text = dataList[position]
        }

        override fun getItemCount(): Int {
            return dataList.size
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.list) {
            startActivity(Intent(this, ListActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

}