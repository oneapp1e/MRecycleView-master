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
  <version>1.1.0</version>
  <type>pom</type>
</dependency>
```
2. Gradle

```
compile 'com.mlr:MRecyclerView:1.1.0'
```

## [具体步骤]

[具体步骤](https://github.com/oneapp1e/MRecycleView-master/wiki)
 
### 不能再透漏了，具体的请参照demo吧

### Thanks

参考[CymChad/BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper) 的动画处理

---
##### 如果有问题联系邮箱:mulinrui@163.com
  




