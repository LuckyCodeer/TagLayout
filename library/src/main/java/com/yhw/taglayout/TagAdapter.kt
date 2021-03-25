package com.yhw.taglayout

import android.view.View
import android.view.ViewGroup

abstract class TagAdapter{

    abstract fun onCreateView(parent: ViewGroup): View

    abstract fun onBindView(itemView:View, position: Int)

    abstract fun getItemCount(): Int
}