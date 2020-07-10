package com.example.bottomnavigation.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.bottomnavigation.R
import kotlinx.coroutines.*
import java.text.ParsePosition

class Leaderboard : Fragment() {

    private lateinit var viewAdapter: Myadapter

    private var lastVisibleItemPosition: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_leaderboard,container,false)

        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swiperefresh)

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)

        swipeRefreshLayout.setOnRefreshListener {

            GlobalScope.launch{
                delay(2000)
                withContext(Dispatchers.Main) {
                    viewAdapter.addItem(List(10){"PersonX ${it + 1}" })
                    swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(context, "Loading Finish", Toast.LENGTH_SHORT).show()
                }

            }

        }

        viewAdapter = Myadapter(MutableList(10){"Person ${it + 1}" })

        val recyclerView = view.findViewById<RecyclerView>(R.id.leaderboard_list)

        recyclerView.adapter = viewAdapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition+1 == viewAdapter.itemCount){
                    GlobalScope.launch{
                        delay(1000)
                        withContext(Dispatchers.Main) {
                            viewAdapter.addMoreItem(List(10){"PersonX ${it + 1}" })
                        }

                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            }
        })

        return view
    }
}

class Myadapter(private val myDataset:MutableList<String>) : RecyclerView.Adapter<Myadapter.ViewHolder>() {

    open class ViewHolder(open val item:View) : RecyclerView.ViewHolder(item)

    class ItemViewHolder(override val item:View) : ViewHolder(item)

    class FooterViewHolder(override val item:View) : ViewHolder(item)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if(viewType == TYPE_FOOTER){
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.refresh_footer_item,parent,false)

            return FooterViewHolder(itemView)
        }else{
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_view_item,parent,false)

            return ItemViewHolder(itemView)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(holder is ItemViewHolder){
            holder.item.findViewById<TextView>(R.id.user_name_text).text = myDataset[position]

            holder.item.findViewById<ImageView>(R.id.user_avatar_image).setImageResource(listOfAvatars[position % listOfAvatars.size])

            //next click item
            holder.item.setOnClickListener{
                val bundle = bundleOf(USERNAME_KEY to myDataset[position])
                holder.item.findNavController().navigate(R.id.action_leaderboard_to_userProfile,bundle)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(position + 1 == itemCount){
            return TYPE_FOOTER
        }
        return TYPE_ITEM
    }

    override fun getItemCount() = myDataset.size+1

    fun addItem(itemList:List<String>) {
        myDataset.clear()
        myDataset.addAll(itemList)
        notifyDataSetChanged()
    }

    fun addMoreItem(itemList:List<String>) {
        myDataset.addAll(itemList)
        notifyDataSetChanged()
    }

    companion object {
        const val USERNAME_KEY = "userName"
        const val TYPE_ITEM = 0
        const val TYPE_FOOTER = 1
    }
}

private val listOfAvatars = listOf(
        R.drawable.avatar_1_raster,
        R.drawable.avatar_2_raster,
        R.drawable.avatar_3_raster,
        R.drawable.avatar_4_raster,
        R.drawable.avatar_5_raster,
        R.drawable.avatar_6_raster
)