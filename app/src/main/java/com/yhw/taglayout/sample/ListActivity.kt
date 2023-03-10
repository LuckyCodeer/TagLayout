package com.yhw.taglayout.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.yhw.taglayout.TagAdapter
import com.yhw.taglayout.TagLayout

/**
 * 列表
 */
class ListActivity : AppCompatActivity() {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mRefreshLayout: SwipeRefreshLayout
    private var mAdapter: ListAdapter? = null
    private val list1 =
        mutableListOf("陕西西安", "新疆乌鲁木齐", "北京天安门", "西安大雁塔", "秦始皇兵马俑", "内蒙古呼伦贝尔大草原", "山西大同")
    private val list2 = mutableListOf("红色", "黄色", "绿色", "紫色", "蓝色")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        mRecyclerView = findViewById(R.id.recycler_view)
        mRefreshLayout = findViewById(R.id.refresh_layout)

        mRefreshLayout.setOnRefreshListener {
            mRefreshLayout.isRefreshing = true
            getData()
        }
        getData()
    }

    private fun getData() {
        val list = mutableListOf<Bean>()
        for (i in 0..100) {
            val tagList = mutableListOf<String>()
            if (i % 2 == 0) {
                for (s in list1) {
                    tagList.add(s)
                }
            } else {
                for (s in list2) {
                    tagList.add(s)
                }
            }
            val bean = Bean("", tagList)
            bean.name = "$i."
            bean.tagList = tagList
            list.add(bean)
        }
        if (mAdapter == null) {
            mAdapter = ListAdapter(list)
            mRecyclerView.adapter = mAdapter
        } else {
            mAdapter?.notifyDataSetChanged()
        }
        mRefreshLayout.isRefreshing = false
    }

    class ListAdapter(private val list: List<Bean>) : Adapter<MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_layout, parent, false)
            return MyViewHolder(itemView)
        }

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.textView.text = list[position].name
            holder.tagLayout.setAdapter(MyTagAdapter(list[position].tagList))
            holder.itemView.setOnClickListener {
                Toast.makeText(holder.itemView.context, "" + position, Toast.LENGTH_SHORT).show()
            }
            /*holder.tagLayout.onItemClickListener = object : TagLayout.OnItemClickListener {
                override fun onItemClick(position: Int, view: View) {
                    Toast.makeText(holder.itemView.context, "点击了标签$position", Toast.LENGTH_SHORT)
                        .show()
                }
            }*/
        }

    }

    class MyViewHolder(itemView: View) : ViewHolder(itemView) {
        val tagLayout: TagLayout = itemView.findViewById(R.id.tag_layout)
        val textView: TextView = itemView.findViewById(R.id.tv_text)
    }

    class MyTagAdapter(private val dataList: List<String>) : TagAdapter() {
        override fun onCreateView(parent: ViewGroup): View {
            return LayoutInflater.from(parent.context)
                .inflate(R.layout.tag_item_layout, parent, false)
        }

        override fun onBindView(itemView: View, position: Int) {
            itemView.setBackgroundResource(R.drawable.tag_normal_bg)
            val textView: TextView = itemView.findViewById(R.id.tv_title)
            textView.text = dataList[position]
        }

        override fun getItemCount(): Int {
            return dataList.size
        }
    }

    data class Bean(var name: String, var tagList: List<String>)
}