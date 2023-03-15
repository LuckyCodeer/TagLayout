# TagLayout
Android上的标签布局、流式布局，支持单选、多选等操作

### 引入依赖

```kotlin
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

dependencies {
            implementation 'com.github.LuckyCodeer:TagLayout:1.0.5'
	}
```

### 使用方法

#### 布局文件使用
```xml
 <com.yhw.taglayout.TagLayout
        android:id="@+id/tag_layout_1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:choiceMode="singleChoice">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:padding="10dp"
            android:layout_margin="10dp"
            android:text="正常" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:padding="10dp"
            android:layout_margin="10dp"
            android:text="单选" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:padding="10dp"
            android:layout_margin="10dp"
            android:text="多选" />
    </com.yhw.taglayout.TagLayout>
```
或
```xml
    <com.yhw.taglayout.TagLayout
        android:id="@+id/tag_layout_2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:defaultChoicePosition="0"
        app:choiceMode="none" />
```

##### 布局属性
1. app:defaultChoicePosition="0" //单选时默认选中项
2. app:choiceMode="none" //设置选择模式，支持单选(singleChoice)和多选(multipleChoice) 默认(none)表示不设置选择模式


#### 动态添加数据
```kotlin
val list = mutableListOf("陕西西安", "新疆乌鲁木齐", "北京天安门", "西安大雁塔", "秦始皇兵马俑", "内蒙古呼伦贝尔大草原", "山西大同")
tagLayout.setAdapter(MyAdapter(list))
```
#### 其它事件操作
```kotlin
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
getIndexBtn.setOnClickListener{
    if(tagLayout2.getChoiceMode() == TagLayout.ChoiceMode.SingleChoice.choiceMode){
        Toast.makeText(this@MainActivity, "单选了 ${tagLayout2.getCheckedPosition()}", Toast.LENGTH_SHORT).show()
    }else if(tagLayout2.getChoiceMode() == TagLayout.ChoiceMode.MultipleChoice.choiceMode){
        Toast.makeText(this@MainActivity, "多选了 ${tagLayout2.getCheckedList()}", Toast.LENGTH_SHORT).show()
    }else{
        Toast.makeText(this@MainActivity, "非选中模式，不能获取", Toast.LENGTH_SHORT).show()
    }
}
```

**\*示例代码采用Kotlin语言编写，如果你的项目是Java，用法与Kotlin基本类似**
