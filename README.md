# MRecycleView-master


> 对RecyclerView的简单封装，自定义的属性可以直接在xml布局中使用，实现的headerView的添加，拖拽移动和滑动删除的处理，混合列表的使用，
实现sectionListView和SectionGridView，以及PinnedSectionListView和PinnedSectionGridView

## 示例图（简单demo以后再更新）
  ![gif](https://github.com/oneapp1e/MRecycleView-master/blob/master/gif01.gif)

## 使用方法
1. Maven
```
<dependency>
  <groupId>com.mlr</groupId>
  <artifactId>MRecyclerView</artifactId>
  <version>0.0.2</version>
  <type>pom</type>
</dependency>
```
2. Gradle

```
compile 'com.mlr:MRecyclerView:0.0.2'
```

## 具体步骤

 1. **可以直接使用自定义属性改变方向，实现横向滚动或者竖向滚动，默认竖向滚动** 
 

    1.1 例如竖向滚动列表：
   

        <com.mlr.mrecyclerview.MRecyclerView
            android:id="@+id/rv_common_list"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>
            
    
    1.2 或者横向滚动列表：
    
        <com.mlr.mrecyclerview.MRecyclerView
            android:id="@+id/rv_common_list"
            app:orientation="HORIZONTAL"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>

----------
     
2.**实现普通列表只需要继承MRecyclerViewAdapter适配器并实现d对应的三个抽象方法即可**

    
    /**
     * 创建itemHolder
     * @param parent
     * @param viewType
     * @return
     */
    protected abstract RecyclerView.ViewHolder createItemHolder(ViewGroup parent, int viewType);

    /**
     * 子类对item holder 的处理
     * 此处已经去除headerview数量  子类不需要处理
     *
     * @param holder   ViewHolder
     * @param position 已经去除headerview的数量的索引
     */
    protected abstract void bindItemHolder(RecyclerView.ViewHolder holder, int position, int viewType);
    
    /**
     * 获取更多app item的逻辑
     *
     * @param out           得到的数据列表
     * @param startPosition 起始位置
     * @param requestSize   请求数量
     * @return Status code
     */
    protected abstract int getMoreData(List<Data> out, int startPosition, int requestSize);
    
    
### 具体的参照demo


---
##### 联系邮箱:mulinrui@163.com
  




