package com.mlr.adapter;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mlr.animation.AlphaInAnimation;
import com.mlr.animation.BaseAnimation;
import com.mlr.animation.ScaleInAnimation;
import com.mlr.animation.SlideInBottomAnimation;
import com.mlr.animation.SlideInLeftAnimation;
import com.mlr.animation.SlideInRightAnimation;
import com.mlr.holder.BaseHolder;
import com.mlr.holder.SimpleHolder;
import com.mlr.model.ViewTypeInfo;
import com.mlr.mrecyclerview.MRecyclerView;
import com.mlr.mrecyclerview.R;
import com.mlr.utils.ISpanSizeLookup;
import com.mlr.utils.LoadMoreListener;
import com.mlr.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * recyclerView列表适配器
 * 可以使用addHeaderView
 *
 * @param <Data> 数据列表
 * @param <T>    BaseHolder
 */
public abstract class MRecyclerViewAdapter<Data extends ViewTypeInfo, T extends BaseHolder>
        extends AsyncLoadingAdapter<T> implements ISpanSizeLookup {
    // ==========================================================================
    // Constants
    // ==========================================================================
    /**
     * headview的起始type
     */
    private static final int VIEW_TYPE_HEAD_VIEW_ONE = 1000;
    // ==========================================================================
    // Fields
    // ==========================================================================
    private List<Data> mItems;

    private LoadMoreListener mLoadMoreListener;

    private volatile boolean mHasMore;
    /**
     * loadmore 之前是否需要阻塞
     */
    private volatile boolean mBlockLoadMore = false;

    private TextView mBtnRefresh;

    private LinearLayout mSpinnerBg;

    private SparseArray<View> mHeaderViews = new SparseArray<>();

    /**
     * 是否开启默认拖拽 @link{DragAndMoveItemTouchHelperCallback} 默认不开启
     */
    private boolean isDefaultDrag = false;

    // ==========================================================================
    // Constructors
    // ==========================================================================
    public MRecyclerViewAdapter(Context context, List<Data> items) {
        super(context);
        mItems = new ArrayList<>();
        if (null != items) {
            appendData(items);
            if (items.size() < getIncrement()) {
                mHasMore = false;
                setMoreEnabled(false);
            } else {
                mHasMore = true;
                setMoreEnabled(true);
            }
        } else {
            mHasMore = true;
            setMoreEnabled(true);
        }
        // 初始化moreView，以解决请求数据线程比UI线程跑得更快时，刷新UI造成的mBtnRefresh等控件空指针异常
        createMoreViewHolder(null, VIEW_TYPE_MORE);
    }

    // ==========================================================================
    // Getters
    // ==========================================================================

    public List<Data> getData() {
        return mItems;
    }

    public boolean isDefaultDrag() {
        return isDefaultDrag;
    }

    // ==========================================================================
    // Setters
    // ==========================================================================
    public void setHasMore(boolean hasMore) {
        mHasMore = hasMore;
    }

    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
    }

    /**
     * 是否启动 默认的拖拽和移动 @link{DragAndMoveItemTouchHelperCallback}
     *
     * @param defaultDrag
     */
    public void setDefaultDrag(boolean defaultDrag) {
        isDefaultDrag = defaultDrag;
    }

    // ==========================================================================
    // Methods
    // ==========================================================================


    /**
     * 阻塞更多项的加载
     */
    private void blockLoadMore() {
        mBlockLoadMore = true;
    }

    /**
     * 取消对更多项加载的阻塞
     */
    private void unblockLoadMore() {
        mBlockLoadMore = false;
    }

    public void refreshAll() {
        ((Activity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    private boolean addUniqueItem(Data item) {
        boolean duplicate = false;
        for (int i = 0; i < mItems.size(); i++) {
            if (isItemDuplicate(item, mItems.get(i))) {
                duplicate = true;
                break;
            }
        }
        return !duplicate && mItems.add(item);
    }

    private int appendData(List<Data> data2) {
        if (null == data2) {
            return 0;
        }

        /**
         * 重新复制一份数据，避免发生ConcurrentModificationException异常
         * liubin 2015-04-16
         */
        final List<Data> data = new ArrayList<>(data2);

        int addedCount = 0;
        for (Data item : data) {
            if (!filterItem(item) && addUniqueItem(item)) {
                addedCount++;
            }
        }
        return addedCount;
    }

    public final void setData(final List<Data> data) {
        // 调用在非UI线程
        ((Activity) getContext()).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                setDataInner(data);
            }

        });
    }

    private void setDataInner(List<Data> data) {
        if (mItems != data) {
            mItems.clear();
            appendData(data);
        }
        int mLoadedCount = 0;
        if (data != null) {
            mLoadedCount = data.size();
        }
        if (mLoadedCount < getIncrement()) {
            mHasMore = false;
            setMoreEnabled(false);
        } else {
            mHasMore = true;
            setMoreEnabled(true);
        }
        refreshAll();
    }

    public Data getItem(int position) {
        if (position < 0 || position >= mItems.size()) {
            return null;
        }
        return mItems.get(position);
    }

    private void clickRefresh() {
        mBtnRefresh.setVisibility(View.GONE);
        mSpinnerBg.setVisibility(View.VISIBLE);
        unblockLoadMore();
    }

    @Override
    public final int getCount() {
        return mHeaderViews.size() + mItems.size();
    }

    @Override
    protected final int getContentItemViewType(int position) {
        if (mHeaderViews.size() > 0 && position < mHeaderViews.size()) {
            return VIEW_TYPE_HEAD_VIEW_ONE + position;
        }
        //此处已经减去headerview数量  子类不需要处理
        return getItemType(position - getHeaderCount());
    }

    /**
     * 获取item的viewType
     * 此处已经减去headerView数量  子类不需要处理
     *
     * @param position 已经减去headerView数量的索引
     * @return viewType
     */
    private final int getItemType(int position) {
        if (getItem(position).getViewType() < VIEW_TYPE_ITEM ||
                getItem(position).getViewType() >= VIEW_TYPE_HEAD_VIEW_ONE) {
            LogUtil.e("mlr getItemType position:" + position + " viewType:" + getItem(position).getViewType());
            throw new RuntimeException("ViewType没有设置啊，请调用setViewType设置大于等于3小于1000的ViewType");
        }
        return getItem(position).getViewType();
    }

    @Override
    protected final T createItemViewHolder(ViewGroup parent, int viewType) {
        if (viewType >= VIEW_TYPE_HEAD_VIEW_ONE && viewType < VIEW_TYPE_HEAD_VIEW_ONE + mHeaderViews.size()) {
            if (mHeaderViews.get(viewType) == null) {
                throw new RuntimeException("HeaderView 为空了，请检查headerView的添加");
            } else {
                return (T) new SimpleHolder(mHeaderViews.get(viewType), getContext());
            }
        }
        return createItemHolder(parent, viewType);
    }

    /**
     * 创建itemHolder
     *
     * @param parent   parent
     * @param viewType viewType
     * @return BaseHolder
     */
    protected abstract T createItemHolder(ViewGroup parent, int viewType);

    @Override
    protected final void bindItemViewHolder(T holder, int position, int viewType) {
        if (viewType >= VIEW_TYPE_HEAD_VIEW_ONE && viewType < VIEW_TYPE_HEAD_VIEW_ONE + mHeaderViews.size()) {
            //headerView不处理
            LogUtil.v("bindItemViewHolder headerView不处理");
        } else if (viewType == VIEW_TYPE_MORE || viewType == VIEW_TYPE_END) {
            //更多 或者 底部到底了  不处理
            LogUtil.v("bindItemViewHolder 更多 或者 底部到底了 不处理");
        } else {
            //此处已经去除headerview数量  子类不需要处理
            bindItemHolder(holder, position - getHeaderCount(), viewType);
        }
    }

    /**
     * 子类对item holder 的处理
     * 此处已经去除headerview数量  子类不需要处理
     *
     * @param holder   ViewHolder
     * @param position 已经去除headerview的数量的索引
     */
    protected abstract void bindItemHolder(T holder, int position, int viewType);

    @Override
    public boolean hasMore() {
        return mHasMore;
    }

    @Override
    protected boolean readyForLoadMore(int startPosition, int requestSize) {
        return !mBlockLoadMore;
    }

    @Override
    protected int onLoadMore(int startPosition, int requestSize) {
        LogUtil.e("mlr Req startPosition:" + startPosition + " requestSize:" + requestSize);
        List<Data> moreItems = new Vector<>(requestSize);
        int responseSize;
        int addedCount = 0;
        int statusCode = getMoreData(moreItems, startPosition, requestSize);
        responseSize = moreItems.size();
        if (responseSize > 0) {
            addedCount = appendData(moreItems);
        }
        LogUtil.e("mlr Rsp responseSize:" + responseSize + " mItems.size():" + mItems.size());

        if (responseSize >= requestSize || !isMoreLoaded(statusCode)) {
            mHasMore = true;
            if (!isMoreLoaded(statusCode)) {
                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setMoreEnabled(mHasMore);
                        mBtnRefresh.setVisibility(View.VISIBLE);
                        mSpinnerBg.setVisibility(View.GONE);
                        blockLoadMore();
                    }
                });
            }
        } else {
            mHasMore = false;
        }
        return addedCount;
    }

    /**
     * 根据状态码判断是否还需要加载
     *
     * @param statusCode statusCode
     * @return boolean
     */
    protected boolean isMoreLoaded(int statusCode) {
        //// TODO: 2016/11/10 根据状态码判断是否还需要加载  默认200
        return statusCode == 200;
    }

    @Override
    protected final T createMoreViewHolder(ViewGroup parent, int viewType) {
        View v = getInflater().inflate(R.layout.list_load_more, parent, false);
        mBtnRefresh = (TextView) v.findViewById(R.id.btn_refresh);
        mSpinnerBg = (LinearLayout) v.findViewById(R.id.relative_spinner_bg);
        return (T) new SimpleHolder(v, getContext());
    }

    @Override
    protected final T createEndViewHolder(ViewGroup parent, int viewType) {
        View v = getInflater().inflate(R.layout.list_to_end, parent, false);
        return (T) new SimpleHolder(v, getContext());
    }

    @Override
    protected final void bindMoreViewHolder(BaseHolder holder, int position, int viewType) {
        if (viewType == VIEW_TYPE_MORE) {
            mBtnRefresh.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    clickRefresh();
                }

            });
        }
    }

    @Override
    protected void bindEndViewHolder(T holder, int position, int viewType) {

    }

    /**
     * 判断是否是重复项的逻辑。需要时重写。
     */
    protected boolean isItemDuplicate(Data item1, Data item2) {
        return false;
    }

    /**
     * 判断某个item是否需要被过滤掉。如果返回true，则该item不会被添加到列表数据源中。默认返回false。
     *
     * @param item data
     * @return 需要被过滤返回true，否则返回false
     */
    protected boolean filterItem(Data item) {
        return false;
    }

    /**
     * 获取更多app item的逻辑
     *
     * @param out           得到的数据列表
     * @param startPosition 起始位置
     * @param requestSize   请求数量
     * @return Status code
     */
    public final int getMoreData(List<Data> out, int startPosition, int requestSize) {
        if (mLoadMoreListener != null) {
            return mLoadMoreListener.onLoadMoreRequested(out, startPosition, requestSize);
        }
        return -1;
    }

    public void addHeaderView(View v) {
        addHeaderView(v, -1);
    }

    /**
     * 将headerView添加到第一个位置
     *
     * @param v 添加的headerView
     */
    public void addFirstHeaderView(View v) {
        addHeaderView(v, 0);
    }

    public void addHeaderView(View v, int index) {

        if (v != null && mHeaderViews.indexOfValue(v) == -1) {
            if (index <= -1 || index >= mHeaderViews.size()) {
                mHeaderViews.put(VIEW_TYPE_HEAD_VIEW_ONE + mHeaderViews.size(), v);
            } else {
                SparseArray<View> tempHeaderViews = new SparseArray<>(mHeaderViews.size());
                //克隆原来的数据
                for (int i = 0; i < mHeaderViews.size(); i++) {
                    tempHeaderViews.put(VIEW_TYPE_HEAD_VIEW_ONE + i, mHeaderViews.get(VIEW_TYPE_HEAD_VIEW_ONE + i));
                }
                //清空原来的数据 重新添加
                mHeaderViews.clear();
                //添加index之前的数据
                for (int i = 0; i < index; i++) {
                    mHeaderViews.put(VIEW_TYPE_HEAD_VIEW_ONE + i, tempHeaderViews.get(VIEW_TYPE_HEAD_VIEW_ONE + i));
                }
                //新增数据
                mHeaderViews.put(VIEW_TYPE_HEAD_VIEW_ONE + index, v);
                //添加index之后的数据
                for (int i = index; i < tempHeaderViews.size(); i++) {
                    mHeaderViews.put(VIEW_TYPE_HEAD_VIEW_ONE + i + 1, tempHeaderViews.get(VIEW_TYPE_HEAD_VIEW_ONE + i));
                }
            }
        }
    }

    /**
     * 移除headerView
     *
     * @param v 移除的headerView
     */
    public void removeHeaderView(View v) {
        SparseArray<View> tempHeaderViews = new SparseArray<>(mHeaderViews.size());
        //克隆原来的数据
        for (int i = 0; i < mHeaderViews.size(); i++) {
            tempHeaderViews.put(VIEW_TYPE_HEAD_VIEW_ONE + i, mHeaderViews.get(VIEW_TYPE_HEAD_VIEW_ONE + i));
        }
        //获取需要移除view的位置
        int viewType = -1;
        for (int i = 0; i < mHeaderViews.size(); i++) {
            if (tempHeaderViews.get(VIEW_TYPE_HEAD_VIEW_ONE + i) == v) {
                viewType = VIEW_TYPE_HEAD_VIEW_ONE + i;
            }
        }

        //如果不存在 不进行处理
        if (viewType == -1) {
            return;
        }

        //移除数据
        mHeaderViews.delete(viewType);

        //添加index之后的数据
        for (int i = viewType - VIEW_TYPE_HEAD_VIEW_ONE + 1; i < tempHeaderViews.size(); i++) {
            mHeaderViews.put(VIEW_TYPE_HEAD_VIEW_ONE + i - 1, tempHeaderViews.get(VIEW_TYPE_HEAD_VIEW_ONE + i));
        }
        //删除最后一位数据
        mHeaderViews.delete(VIEW_TYPE_HEAD_VIEW_ONE + tempHeaderViews.size() - 1);

    }

    /**
     * 获取headerView的数量
     *
     * @return 数量
     */
    @Override
    public int getHeaderCount() {
        return mHeaderViews.size();
    }

    /**
     * 获取一个单元格占原始单元格的倍数（不能大于一行的列数）
     *
     * @param position 位置
     * @return 表示实际一个单元格是原单元格的n倍
     */
    @Override
    public final int getSpanSize2(int position) {
        //判断如果是seciton 或者 更多 或者end
        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_MORE || viewType == VIEW_TYPE_END
                || (viewType >= VIEW_TYPE_HEAD_VIEW_ONE && viewType < VIEW_TYPE_HEAD_VIEW_ONE + getHeaderCount())) {
            //表示实际一个单元格是原单元格的n倍
            return getSpanCount();
        }
        return getSpanSize(position - getHeaderCount(), viewType);
    }

    /**
     * 获取一个单元格占原始单元格的倍数（不能大于一行的列数）
     * 默认为1  子类需要处理就重写该方法
     *
     * @param position 已经去除headerview的数量的索引
     * @param viewType viewType
     * @return int
     */
    protected int getSpanSize(int position, int viewType) {
        return 1;
    }

    @Override
    public int getSpanCount() {
        return 1;
    }

    /**
     * SectionMRecyclerView 中有使用
     * 主要用于悬浮view只是sectionView的一部分
     * 如果使用的是SectionMRecyclerView 子类如果需要可以重写该方法控制sectionViewType的holder中view的显示隐藏
     *
     * @param firstSectionBelow sectionViewType对应的view
     * @param visible           设置是否可见
     */
    public void setSectionHeaderVisible(View firstSectionBelow, int visible) {
        if (null != firstSectionBelow && firstSectionBelow.getTag() instanceof BaseHolder) {
            //注意设置tag这样就能取到对应的holder进行处理了
        }
    }

    /**
     * SectionMRecyclerView 中有使用
     * 子类必须重新改方法 设置SectionViewType
     *
     * @return int
     */
    public int getSectionViewType() {
        return -1;
    }

    /**
     * 是否启用滑动删除
     *
     * @return 是否启用滑动删除
     */
    public final boolean isItemViewSwipeEnabled2(int position) {
        if (isDefaultDrag) {
            //判断如果是seciton 或者 更多 或者end
            int viewType = getItemViewType(position);
            if (viewType == VIEW_TYPE_MORE || viewType == VIEW_TYPE_END
                    || (viewType >= VIEW_TYPE_HEAD_VIEW_ONE && viewType < VIEW_TYPE_HEAD_VIEW_ONE + getHeaderCount())) {
                return false;
            }
            return isItemViewSwipeEnabled(position - getHeaderCount());
        }
        return false;
    }

    /**
     * 是否启用滑动删除 已经去除headerCount的position
     * 如果开启自己写的拖拽{@link #isDefaultDrag}，子类可以重写该方法判断
     *
     * @return 是否启用滑动删除
     */
    public boolean isItemViewSwipeEnabled(int position) {
        return false;
    }

    /**
     * 移除item
     *
     * @param position 位置
     */
    public final void onItemDismiss(int position) {
        mItems.remove(position - getHeaderCount());
        notifyItemRemoved(position);

        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        }, 200);
    }


    /**
     * 是否启用长按拖拽
     *
     * @return 是否启用长按拖拽
     */
    public final boolean isLongPressDragEnabled2(int position) {
        if (isDefaultDrag) {
            //判断如果是seciton 或者 更多 或者end
            int viewType = getItemViewType(position);
            if (viewType == VIEW_TYPE_MORE || viewType == VIEW_TYPE_END
                    || (viewType >= VIEW_TYPE_HEAD_VIEW_ONE && viewType < VIEW_TYPE_HEAD_VIEW_ONE + getHeaderCount())) {
                return false;
            } else {
                return isLongPressDragEnabled(position - getHeaderCount());
            }
        }

        return false;
    }

    /**
     * 是否启用长按拖拽 已经去除headerCount的position
     * 如果开启自己写的拖拽{@link #isDefaultDrag}，子类可以重写该方法判断
     *
     * @return 是否启用长按拖拽
     */
    public boolean isLongPressDragEnabled(int position) {
        return false;
    }

    /**
     * 移动item
     *
     * @param from 起始位置
     * @param to   结束位置
     */
    public final void onItemMoved(int from, int to) {
        LogUtil.e("mlr onItemMoved from:" + from + "  to:" + to + "  mItems.size():" + mItems.size());
        //如果适配器索引值大于数据集合的索引值 说明子类有其他处理 那么此处不进行移动  子类可以重新该方法
        if (from > getCount() - 1 || to > getCount() - 1) {
            return;
        }
        Collections.swap(mItems, from - getHeaderCount(), to - getHeaderCount());
        notifyItemMoved(from, to);
    }

    /**
     * 移动item完成，动画结束，需要更新一下数据.
     * 否则会造成onClick方法里面position没有更新获取的数据不对
     */
    public final void clearView() {
        notifyDataSetChanged();
    }


    @Override
    public final void onViewAttachedToWindow(T holder) {
        int viewType = holder.getItemViewType();
        if (viewType == VIEW_TYPE_MORE) {
            ImageView loadingView = (ImageView) holder.itemView.findViewById(R.id.drawable_loading);
            ((AnimationDrawable) loadingView.getDrawable()).start();
        } else if (viewType == VIEW_TYPE_END
                || (viewType >= VIEW_TYPE_HEAD_VIEW_ONE && viewType < VIEW_TYPE_HEAD_VIEW_ONE + getHeaderCount())) {
            //不处理
        } else {
            addAnimation(holder);
        }

    }

    // ==========================================================================
    // 以下是关于动画的处理
    // ==========================================================================
    /**
     * Use with {openLoadAnimation}
     */
    public static final int ALPHAIN = 0x00000001;
    /**
     * Use with {openLoadAnimation}
     */
    public static final int SCALEIN = 0x00000002;
    /**
     * Use with {openLoadAnimation}
     */
    public static final int SLIDEIN_BOTTOM = 0x00000003;
    /**
     * Use with {openLoadAnimation}
     */
    public static final int SLIDEIN_LEFT = 0x00000004;
    /**
     * Use with {openLoadAnimation}
     */
    public static final int SLIDEIN_RIGHT = 0x00000005;

    /**
     * 是否仅第一次启用动画
     */
    private boolean mFirstOnlyEnable = true;
    /**
     * 是否启用动画
     */
    private boolean mOpenAnimationEnable = false;
    /**
     * 动画加速器
     */
    private Interpolator mInterpolator = new LinearInterpolator();
    private int mDuration = 300;
    private int mLastPosition = -1;
    //
    private BaseAnimation mCustomAnimation;
    private BaseAnimation mSelectAnimation = new AlphaInAnimation();

    /**
     * add animation when you want to show time
     *
     * @param holder
     */
    private void addAnimation(T holder) {
        if (mOpenAnimationEnable) {
            if (!mFirstOnlyEnable || holder.getLayoutPosition() > mLastPosition) {
                BaseAnimation animation = null;
                if (mCustomAnimation != null) {
                    animation = mCustomAnimation;
                } else {
                    animation = mSelectAnimation;
                }
                for (Animator anim : animation.getAnimators(holder.itemView)) {
                    startAnim(anim, holder.getLayoutPosition());
                }
                mLastPosition = holder.getLayoutPosition();
            }
        }
    }

    /**
     * set anim to start when loading
     *
     * @param anim
     * @param index
     */
    protected void startAnim(Animator anim, int index) {
        anim.setDuration(mDuration).start();
        anim.setInterpolator(mInterpolator);
    }

    /**
     * Set the view animation type.
     *
     * @param animationType One of {ALPHAIN}, {SCALEIN}, {SLIDEIN_BOTTOM}, {SLIDEIN_LEFT}, {SLIDEIN_RIGHT}.
     */
    public void openLoadAnimation(int animationType) {
        this.mOpenAnimationEnable = true;
        mCustomAnimation = null;
        switch (animationType) {
            case ALPHAIN:
                mSelectAnimation = new AlphaInAnimation();
                break;
            case SCALEIN:
                mSelectAnimation = new ScaleInAnimation();
                break;
            case SLIDEIN_BOTTOM:
                mSelectAnimation = new SlideInBottomAnimation();
                break;
            case SLIDEIN_LEFT:
                mSelectAnimation = new SlideInLeftAnimation();
                break;
            case SLIDEIN_RIGHT:
                mSelectAnimation = new SlideInRightAnimation();
                break;
            default:
                break;
        }
    }

    /**
     * Set Custom ObjectAnimator
     *
     * @param animation ObjectAnimator
     */
    public void openLoadAnimation(BaseAnimation animation) {
        this.mOpenAnimationEnable = true;
        this.mCustomAnimation = animation;
    }

    /**
     * To open the animation when loading
     */
    public void openLoadAnimation() {
        this.mOpenAnimationEnable = true;
    }

    /**
     * @param firstOnly true just show anim when first loading false show anim when load the data every time
     */
    public void isFirstOnly(boolean firstOnly) {
        this.mFirstOnlyEnable = firstOnly;
    }

}
