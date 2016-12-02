# MRecycleView-master


> 对RecyclerView的简单封装，具体能做什么？
- 封装了LayoutManager直接在xml布局中使用一个属性解决
- 封装adapter代码（减少百分之70%代码）
- 添加加载动画（一行代码轻松切换5种默认动画）（借鉴使用CymChad的动画方案）
- 添加头部（支持多个）、下拉刷新、上拉加载
- 轻松实现加载更多（是否需要更多加载逻辑已经帮你实现了哦）
- 底部显示到底了提示
- 实现Section封装，以及pinnerHeader
- 直接在数据上实现viewType的划分，不需要额外代码
- 添加拖拽item（）

## 示例图
### 1. 普通列表
  ![gif](https://github.com/oneapp1e/MRecycleView-master/blob/master/gif/gif01.gif)
### 2. 普通网格
![gif](https://github.com/oneapp1e/MRecycleView-master/blob/master/gif/gif02.gif)
### 3. 混合列表
![gif](https://github.com/oneapp1e/MRecycleView-master/blob/master/gif/gif03.gif)
### 4. section+pinner+list
![gif](https://github.com/oneapp1e/MRecycleView-master/blob/master/gif/gif04.gif)
### 5. section+pinner+grid
![gif](https://github.com/oneapp1e/MRecycleView-master/blob/master/gif/gif05.gif)
### 6. draw+move+list
![gif](https://github.com/oneapp1e/MRecycleView-master/blob/master/gif/gif06.gif)
### 7. draw+move+grid
![gif](https://github.com/oneapp1e/MRecycleView-master/blob/master/gif/gif07.gif)
### 8. pulltoRefresh
![gif](https://github.com/oneapp1e/MRecycleView-master/blob/master/gif/gif08.gif)
### 9. 动画的使用
![gif](https://github.com/oneapp1e/MRecycleView-master/blob/master/gif/gif09.gif)

## 使用方法
1. Maven
```
<dependency>
  <groupId>com.mlr</groupId>
  <artifactId>MRecyclerView</artifactId>
  <version>0.0.6</version>
  <type>pom</type>
</dependency>
```
2. Gradle

```
compile 'com.mlr:MRecyclerView:0.0.6'
```

## 具体步骤

 1. **可以直接使用自定义属性改变方向，实现横向滚动或者竖向滚动，默认竖向滚动**

```
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
```
----------

2.**实现普通列表只需要继承MRecyclerViewAdapter适配器并实现对应的两个个抽象方法即可**

```
    //创建一个ViewHolder
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
```
----------

3. **如果可以确定每个item的高度是固定的，设置这个选项可以提高性能**
```
mRecyclerView.setHasFixedSize(true);
```
----------

4. **添加动画**

```
4.1 一行代码搞定，默认提供5种方法（渐显、缩放、从下到上，从左到右、从右到左）
 commonListAdapter.openLoadAnimation(MRecyclerViewAdapter.ALPHAIN);

4.2 如果没有你要的怎么办？自己自定义自己需要的动画

 public class CustomAnimation implements BaseAnimation {

    @Override
    public Animator[] getAnimators(View view) {
        return new Animator[]{
                ObjectAnimator.ofFloat(view, "scaleY", 1, 1.1f, 1),
                ObjectAnimator.ofFloat(view, "scaleX", 1, 1.1f, 1)
        };
    }
}

//同样的方法调用即可
 commonListAdapter.openLoadAnimation(new CustomAnimation());

```
----------

5. **添加headerView**
```
5.1 添加headerView  可以添加多个啊
    View headerView1 = createHeadView("headerView1");
    commonListAdapter.addHeaderView(headerView1);
    View headerView2 = createHeadView("headerView2");
    commonListAdapter.addHeaderView(headerView2);

5.2 将一个headerView插入到最上边
    View headerView3 = createHeadView("headerView3");
    commonListAdapter.addHeaderView(headerView3, 0);

5.3 移除headerView
    commonListAdapter.removeHeaderView(headerView2);

```

 ----------

6. **底部显示到底了提示**
```
//启动到底了试图
commonListAdapter.setToEndEnabled(true, rvCommonList);
```
 ----------

7. **启用加载更多**
```
//启动加载更多 默认已经开启不需要单独设置
commonListAdapter.setHasMore(true);
//只需要实现加载更多的监听即可 是否需要加载更多的逻辑已经帮你实现了啊
commonListAdapter.setLoadMoreListener(new LoadMoreListener<AppInfo>() {
            @Override
            public int onLoadMoreRequested(List<AppInfo> out, int startPosition, int requestSize) {
                if (count >= DataServer.MaxCount) {
                    LogUtils.e("mlr 没有更多数据");
                } else {
                    LogUtils.e("mlr 请求更多数据");
                    out.addAll(DataServer.getCommonMoreData(requestSize));
                    count++;
                }
                return 200;
            }
        });
```
 ----------

8. **轻松实现网格布局**
```
//在继承的适配器中实现这个方法 想要几个网格你说了算
    @Override
    public int getSpanCount() {
        return 5;
    }

//如果要实现混合布局这样搞就行了 根据viewType处理即可
    @Override
    protected int getSpanSize(int position, int viewType) {
        if (viewType == DataServer.VIEW_TYPE_TITLE) {
            return 5;
        } else {
            return super.getSpanSize(position, viewType);
        }
    }
```
 ----------
9. **轻松实现拖拽和滑动删除**
```
//在继承的适配器中实现这两个方法可以针对单个item进行是否开启拖拽和滑动
  @Override
    public boolean isLongPressDragEnabled(int position) {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled(int position) {
        return true;
    }
```
 ----------

10. **实现Section封装，以及pinnerHeader**
```
//Section封装实际就是混合布局啊，混合布局轻松搞定！
//PinnerHeader这样简单吧！
mRecyclerView.setSectionViewType(getSectionViewType());
mRecyclerView.setPinnedHeaderView(SectionHolder.itemView);
```
 ----------

### 不能再透漏了，具体的请参照demo吧

### Thanks

参考[CymChad/BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper) 的动画处理

---
##### 如果有问题联系邮箱:mulinrui@163.com
  




