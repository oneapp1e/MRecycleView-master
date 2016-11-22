package com.mlr.utils;


import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mlr.adapter.AsyncLoadingAdapter;
import com.mlr.adapter.MRecyclerViewAdapter;
import com.mlr.mrecyclerview.R;


/**
 * 注意：必须在初始化之前使用RecyclerView的setAdapter方法设置适配器,
 */
public class PullToRefreshRecyclerViewWrapper extends RelativeLayout {
    // ==========================================================================
    // Constants
    // ==========================================================================
    // 拉的状态
    public static final int STATE_RESET = 0x0;
    public static final int STATE_PULL_TO_REFRESH = 0x1;
    public static final int STATE_RELEASE_TO_REFRESH = 0x2;
    public static final int STATE_REFRESHING = 0x8;
    public static final int STATE_MANUAL_REFRESHING = 0x9;
    public static final int STATE_OVERSCROLLING = 0x10;

    public static final int DRAG_MODE_NONE = 0xAA0;
    public static final int DRAG_MODE_DOWN = 0xAA1;
    public static final int DRAG_MODE_UP = 0xAA2;

    // 拉的方向
    public int mDraggingMode = DRAG_MODE_NONE;// 默认是没有拖拽的

    // 弹性的摩擦力
    static final float FRICTION = 2.0f;
    // 动画时间
    public static final int SMOOTH_SCROLL_DURATION_MS = 200;
    // 用于状态存储
    static final String STATE_STATE = "ptr_state";
    static final String STATE_SCROLLING_REFRESHING_ENABLED = "ptr_disable_scrolling";
    static final String STATE_SHOW_REFRESHING_VIEW = "ptr_show_refreshing_view";
    static final String STATE_SUPER = "ptr_super";

    // 动画插入器
    static final Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();
    // ==========================================================================
    // Fields
    // ==========================================================================
    private Context mContext;
    // 最小的拖动距离
    private int mTouchSlop;
    // 最后一次的x、y值
    private float mLastMotionX, mLastMotionY;
    // 初始的y值
    private float mInitialMotionY;
    private boolean mFilterTouchEvents = true;// 是否过滤手势事件，过滤的条件是如果：例如上下刷新，而水平的距离大于垂直的距离，则不刷新

    // 用于被包装的View，使该View具备弹性或者拉动刷新
    private RecyclerView mRefreshableView;
    // 用于展示下拉刷新的View
    private BasePullToRefreshLoadingView mHeaderLayout;
    private View mFooterView;// 数据加载完后，再向上拉时，显示“到底了”
    // 滑动的时候是否可以刷新
    private boolean mScrollingWhileRefreshingEnabled = true;
    private int mState = STATE_RESET;
    // 监听刷新，这个比mOnPullEventListener更精准，我们在该监听中做耗时操作，并且需要在完成后再主线程中回调onRefreshComplete，以完成界面的刷新
    private OnRefreshListener mOnRefreshListener;
    // 监听下拉刷新的各种状态，对refreshing的监听没有mOnRefreshListener精确，只在手指离开时候回调该状态，而不是在滚动到恰当的地方回调
    private OnPullEventListener mOnPullEventListener;
    // 滑动的插入器
    private Interpolator mScrollAnimationInterpolator;
    // 平滑滚动的任务
    private SmoothScrollRunnable mCurrentSmoothScrollRunnable;
    // 当mHeaderLayout滑出屏幕外时是否隐藏
    private boolean mLayoutVisibilityChangesEnabled = true;
    // 刷新时是否显示View
    private boolean mShowViewWhileRefreshing = true;
    // 刷新时是否不固定，不固定的话，就是创建一个额外的header加到listView上，让其随着listView一起移动  要想启用该扩展setAdapter要先进行setAdapter
    private boolean mListViewExtrasEnabled = true;

    private BasePullToRefreshLoadingView mHeaderLoadingView;
    // 刷新完毕后，是否回到第一个
    private boolean mToFirstOnComplete = false;
    private int mFootMargin;// 用于首页底部到底了效果视图向上调整间距

    /**
     * 最后一次更新数据的时间，用于显示多长时间前更新的功能
     */
    private long mLastUpdatedTime;

    /**
     * 当前刷新模式 默认不可刷新
     */
    private Mode mMode = Mode.DISABLED;

    private LinearLayoutManager mLayoutManager;

    private RecyclerView.Adapter mAdapter;

    private TouchEventInterceptor mTouchEventInterceptor;

    // ==========================================================================
    // Constructors
    // ==========================================================================

    /**
     * 构造方法
     *
     * @param context
     * @param refreshableView 需要被包装为下拉刷新的ListView
     */
    public PullToRefreshRecyclerViewWrapper(Context context, RecyclerView refreshableView) {
        this(context, refreshableView, 0);
    }

    public PullToRefreshRecyclerViewWrapper(Context context, RecyclerView refreshableView, int footMargin) {
        super(context);
        mContext = context;
        mRefreshableView = refreshableView;
        mFootMargin = footMargin;
        init();
    }

    // ==========================================================================
    // Getters And Setters
    // ==========================================================================
    public final int getHeaderSize() {
        return mHeaderLayout.getContentSize();
    }

    /**
     * 设置下拉事件的监听
     */
    public void setOnPullEventListener(OnPullEventListener listener) {
        mOnPullEventListener = listener;
    }

    /**
     * 设置最后更新时间
     */
    @Deprecated //这个方法支持自动设置
    public void setLastUpdatedLabel(String label) {
        mHeaderLayout.setLastUpdatedText(label);
    }

    /**
     * 设置最后更新时间
     */
    public void setLastUpdatedTime(long time) {
        this.mLastUpdatedTime = time;
    }

    /**
     * 更新最后更新数据的时间
     */
    public void refreshLastUpdatedTime() {
        this.mLastUpdatedTime = System.currentTimeMillis();
    }

    /**
     * 设置状态提示文本
     *
     * @param pullResId       下拉文字
     * @param releaseResId    释放文字
     * @param refreshingResId 刷新文字
     *                        一个项目中这个地方的方案应该是一致的，提供外部设置反而麻烦，修改为内部自动设置   liubin 2015-08-20
     */
    private void setHeaderTextResId(int pullResId, int releaseResId, int refreshingResId) {
        mHeaderLayout.setPullText(getContext().getString(pullResId));
        mHeaderLayout.setReleaseText(getContext().getString(releaseResId));
        mHeaderLayout.setRefreshingText(getContext().getString(refreshingResId));
        if (mHeaderLoadingView != null) {
            mHeaderLoadingView.setRefreshingText(getContext().getString(refreshingResId));
        }
    }

    /**
     * 设置下拉刷屏View的高度
     */
    private void setHeaderHeight(int height) {
        mHeaderLayout.setHeight(height);
        if (mHeaderLoadingView != null) {
            mHeaderLoadingView.setHeight(height);
        }
    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * 初始化方法
     */
    private void init() {
        setGravity(Gravity.CENTER);
        ViewConfiguration config = ViewConfiguration.get(mContext);
        mTouchSlop = config.getScaledTouchSlop();

        mHeaderLayout = createHeaderLayout();
        RecyclerView.LayoutManager layoutManager = mRefreshableView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            mLayoutManager = (LinearLayoutManager) layoutManager;
        }

        mAdapter = mRefreshableView.getAdapter();
        //判断如果是listview适配器 则使用addheaderView扩展
        if (mAdapter != null && mAdapter instanceof MRecyclerViewAdapter) {
            mListViewExtrasEnabled = true;
        } else {
            mListViewExtrasEnabled = false;
        }

        if (mListViewExtrasEnabled) {
            final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);
            // 创建加载中的View
            FrameLayout frame = new FrameLayout(getContext());
            mHeaderLoadingView = createLoadingLayout();
            mHeaderLoadingView.setVisibility(View.GONE);
            frame.addView(mHeaderLoadingView, lp);
            frame.setBackgroundColor(getContext().getResources().getColor(R.color.title_text_color_gray));
            frame.setLayoutParams(lp);
            ((MRecyclerViewAdapter) mAdapter).addFirstHeaderView(frame);
        }

        // 禁止ListView自身的上滑
        if (android.os.Build.VERSION.SDK_INT > 9) {
            mRefreshableView.setOverScrollMode(OVER_SCROLL_NEVER);
        }
        mFooterView = createFooterLayout();

        // 设置通用值
        setHeaderTextResId(R.string.update_refresh_state_tip_pull, R.string.update_refresh_state_tip_release,
                R.string.update_refresh_state_tip_refreshing);

        // 更新UI
        updateUI();
    }

    /**
     * 创建HeaderLayout的view
     */
    protected BasePullToRefreshLoadingView createHeaderLayout() {
        PullToRefreshLoadingView layout = new PullToRefreshLoadingView(mContext);
        layout.setVisibility(View.INVISIBLE);
        layout.setBackgroundColor(getContext().getResources().getColor(R.color.title_text_color_gray));
        return layout;
    }

    // 创建用于展示“到底了”的视图
    protected View createFooterLayout() {
        TextView view = new TextView(getContext());
        int marginTop = getResources().getDimensionPixelSize(R.dimen.no_more_margin_top);
        view.setPadding(0, marginTop, 0, 0);

        view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        view.setGravity(Gravity.CENTER_HORIZONTAL);
        view.setTextColor(getContext().getResources().getColor(R.color.update_push_header_txt_color));
        view.setText(getContext().getString(R.string.list_overscroll_tips));
        view.setBackgroundColor(getContext().getResources().getColor(R.color.title_text_color_gray));
        return view;
    }

    /**
     * 创建加载中的view
     */
    protected BasePullToRefreshLoadingView createLoadingLayout() {
        PullToRefreshLoadingView layout = new PullToRefreshLoadingView(mContext);
        layout.setVisibility(View.INVISIBLE);
        return layout;
    }

    /**
     * 更新UI
     */
    protected void updateUI() {
        // 我们需要使用正确的LayoutParam值
        LayoutParams lp;
        int height = getContext().getResources().getDimensionPixelSize(R.dimen.update_pull_refresh_height);
        lp = new LayoutParams(LayoutParams.MATCH_PARENT, height);
        // 先移除，再添加 ROB 不明白为什么先移除
        if (this == mHeaderLayout.getParent()) {
            removeView(mHeaderLayout);
        }
        addView(mHeaderLayout, lp);

        lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mFooterView, lp);

        lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mRefreshableView, lp);

        refreshLoadingViewsSize();
    }

    /**
     * 刷新加载中的View的大小
     */
    protected final void refreshLoadingViewsSize() {
        final int maximumPullScroll = (int) (getMaxPullScroll() * 1.2f);

        int pLeft = getPaddingLeft();
        int pTop = getPaddingTop();
        int pRight = getPaddingRight();
        int pBottom = getPaddingBottom();
        mHeaderLayout.setHeight(maximumPullScroll);// 设置header的高度
        pTop = -maximumPullScroll;// paddingTop设置为负数，让其底部和界面顶部对齐，以便下拉
        pBottom = 0;
        setPadding(pLeft, pTop, pRight, pBottom);
    }

    // 重新设置ListView的高度
    public final void refreshRefreshableViewSize(int width, final int height) {
        ViewGroup.LayoutParams layoutParams = mRefreshableView.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        mRefreshableView.setLayoutParams(layoutParams);
    }

    /**
     * 下拉的最大距离
     */
    private int getMaxPullScroll() {
        return Math.round(getHeight() / FRICTION);
    }

    /**
     * 是否正在刷新
     */
    public final boolean isRefreshing() {
        return mState == STATE_REFRESHING || mState == STATE_MANUAL_REFRESHING;
    }

    /**
     * 是否可以下拉
     * 这个方法用于被包装的View来判断是否可以下拉刷新，例如listView的条件就是第一个可见的是0或者1，因为有headerview的存在，并且还要判断0角标位的子View的top大于或者等于listView的top，
     * 才算是真正的可以下拉刷新
     */
    protected boolean isReadyForPullDown() {

        if (mMode == Mode.DISABLED || mMode == Mode.PULL_FROM_END) {
            return false;
        }

        if (null == mAdapter) {
            return true;
        }
        /** 检查第一个item的position是否为0，但是ListView有headerview，会造成混乱，所以我们根据角标0位置top进行比较 */
        if (mLayoutManager != null) {
            if (mLayoutManager.findFirstVisibleItemPosition() <= 1) {
                final View firstVisibleChild = mRefreshableView.getChildAt(0);
                if (firstVisibleChild != null) {
                    return firstVisibleChild.getTop() >= mRefreshableView.getTop();
                }
            }
        }

        return false;
    }

    // 判断是否可以向上拉
    protected boolean isReadyForPullUp() {
        if (mMode == Mode.DISABLED || mMode == Mode.PULL_FROM_START) {
            return false;
        }

        if (null == mAdapter) {
            return false;
        }
        // 如果是AsyncLoadingAdapter适配器，存在异步加载的情况，需要做判断
        if (mAdapter instanceof AsyncLoadingAdapter) {
            AsyncLoadingAdapter adapter2 = (AsyncLoadingAdapter) mAdapter;
            if (adapter2.hasMore()) {
                return false;
            }
        }

        int count = mAdapter.getItemCount();
        if (mLayoutManager != null) {
            int position = mLayoutManager.findLastVisibleItemPosition();
            return (count - 1) == position && mRefreshableView.getChildAt(mRefreshableView.getChildCount() - 1)
                    .getBottom() + mRefreshableView.getPaddingBottom() == mRefreshableView.getHeight();
        }

        return false;
    }

    /**
     * 手势拦截
     */
    public final boolean onInterceptTouchEvent(MotionEvent event) {
        final int action = event.getAction();// 获取当前的状态
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {// 如果是cancel或者up
            mDraggingMode = DRAG_MODE_NONE;// 拖拽结束
            return false;// 不拦截
        }

        if (mTouchEventInterceptor != null && mTouchEventInterceptor.intercepted(event)) {
            return false;
        }

        if (action != MotionEvent.ACTION_DOWN && isBeingDragged()) {// 如果action不等于down，并且是拖拽的，拦截
            return true;
        }
        boolean handled = false;

        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                // 正在刷新，并且刷新的时候不能滑动，直接返回true
                if (!mScrollingWhileRefreshingEnabled && isRefreshing()) {
                    return true;
                }
                // 如果可以下拉刷新了
                if (isReadyForPullDown() || isReadyForPullUp()) {
                    final float y = event.getY(), x = event.getX();
                    final float diff, oppositeDiff, absDiff;

                    diff = y - mLastMotionY;// 则只计算y差距
                    oppositeDiff = x - mLastMotionX;// 计算x差距

                    absDiff = Math.abs(diff);// 取绝对值
                    // 如果滑动距离大于最小滑动距离，并且不过滤手势事件，或者说过滤，但是正确的方向上移动的距离比不正确的方向上移动的距离要大
                    // 即纵向的距离比横向的距离要大
                    if (absDiff > mTouchSlop && (!mFilterTouchEvents || absDiff > Math.abs(oppositeDiff))) {
                        if (diff > 0 && isReadyForPullDown()) {// 下拉操作
                            mDraggingMode = DRAG_MODE_DOWN;
                        } else if (diff < 0 && isReadyForPullUp()) {// 上拉操作
                            mDraggingMode = DRAG_MODE_UP;
                        } else {
                            mDraggingMode = DRAG_MODE_NONE;
                        }

                        if (mDraggingMode == DRAG_MODE_NONE) {
                            handled = false;
                        } else {
                            mLastMotionY = y;// x和y进行赋值
                            mLastMotionX = x;
                            handled = true;// 拖动状态
                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                if (isReadyForPullDown() || isReadyForPullUp()) {// 如果即将开始，则赋初始值
                    mLastMotionY = mInitialMotionY = event.getY();
                    mLastMotionX = event.getX();
                    handled = false;
                }
                break;
            }
        }
        return handled;// 是否拦截根据是否拖动来决定
    }

    public final boolean onTouchEvent(MotionEvent event) {
        // 正在刷新，并且刷新的时候不能滑动，直接返回true
        if (!mScrollingWhileRefreshingEnabled && isRefreshing()) {
            return true;
        }
        // 如果是down事件，并且处于控件的边缘，则不处理
        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE: {// move事件
                if (isBeingDragged()) {// 正处于拖动
                    mLastMotionY = event.getY();// 赋值
                    mLastMotionX = event.getX();
                    // 如果可以不是固定的View或者是固定的View，但是不处于刷新状态，可以执行下拉刷新
                    if (mDraggingMode == DRAG_MODE_DOWN && (mListViewExtrasEnabled || !isRefreshing())) {
                        pullDownEvent();
                    } else if (mDraggingMode == DRAG_MODE_UP) {
                        pullUpEvent();
                    }
                    return true;
                }
                break;
            }

            case MotionEvent.ACTION_DOWN: {// down事件
                if (isReadyForPullDown() || isReadyForPullUp()) {// 赋初始值和最后的值
                    mLastMotionY = mInitialMotionY = event.getY();
                    mLastMotionX = event.getX();
                    return true;
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mDraggingMode == DRAG_MODE_DOWN) {// 下拉离开
                    mDraggingMode = DRAG_MODE_NONE;
                    // 判断状态为释放刷新，且有监听者，则把状态设置为刷新中
                    if (mState == STATE_RELEASE_TO_REFRESH && (null != mOnRefreshListener)) {
                        setState(STATE_REFRESHING, true);
                        return true;
                    }
                    // 如果已经处于刷新阶段，移动到顶端
                    if (isRefreshing()) {
                        if (mListViewExtrasEnabled) {// 如果可以随手指移动到顶端
                            smoothScrollTo(0);
                        }
                        return true;
                    }
                    // 重置状态
                    setState(STATE_RESET);
                    return true;
                } else if (mDraggingMode == DRAG_MODE_UP) {// 上拉离开
                    mDraggingMode = DRAG_MODE_NONE;
                    smoothScrollTo(0);
                    return true;
                }
                break;

        }
        return false;
    }

    /**
     * 完成刷新
     */
    public final void onRefreshComplete() {
        onRefreshComplete(true);
    }

    /**
     * 完成刷新
     *
     * @param success 数据是否成功加载回来
     */
    public final void onRefreshComplete(boolean success) {
        if (success) {
            refreshLastUpdatedTime();
        }

        if (isRefreshing()) {
            setState(STATE_RESET);
        }
        if (toFirstOnComplete()) {// 刷新完毕后，是否要回复到第一个item
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    //TODO setSelected 此处暂时注释 不知道为什么要设置setSelected(true) 这样recycleView会向下传递给子view造成item被选中
//                    mRefreshableView.setSelected(true);
                    mRefreshableView.scrollToPosition(0);
                }
            }, SMOOTH_SCROLL_DURATION_MS * 2);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View head = null, body = null, foot = null, other = null;

        /**
         * 由于我们在init()方法中，向当前视图加入了用于下载更新时显示的头视图(mHeaderLayout)、显示数据的RecyclerView(mRefreshableView)
         * 和数据加载完了显示"到底了"的TextView(mFooterView),我们在认定的时间，可能通过对象类型做区分
         */
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v instanceof BasePullToRefreshLoadingView) {
                // 这种类型的认定为头视图
                head = v;
            } else if (v instanceof RecyclerView) {
                body = v;
            } else if (v instanceof TextView) {
                foot = v;
            } else if (v instanceof ImageView) {
                other = v;
            } else {
                throw new IllegalArgumentException("配置了其它的视图，此组件暂时不支持");
            }
        }

        if (head == null || body == null || foot == null) {
            throw new IllegalArgumentException("视图配置错误");
        }
        // 至此，我们得到了三个视图，对它们布局
        /*
         * 我们应该重写onMeasure方法，这里直接用layout限制了子视图的宽和高
         */
        head.layout(l, -head.getMeasuredHeight(), r, 0);
        body.layout(l, 0, r, b - t);
        foot.layout(l, b - t - mFootMargin, r, b - t - mFootMargin + foot.getMeasuredHeight());
        if (other != null) {
            other.layout(r - other.getMeasuredWidth() - 40, b - other.getMeasuredHeight() - 40, r - 40, b - 40);
        }
    }

    protected boolean toFirstOnComplete() {
        if (mRefreshableView != null) {
            try {
                return mRefreshableView.getChildAt(0).getTop() == 0;
            } catch (Throwable ignored) {

            }
        }
        return mToFirstOnComplete;
    }

    /**
     * 手动设置为刷新
     */
    public final void setRefreshing() {
        if (!isRefreshing()) {
            setState(STATE_MANUAL_REFRESHING, false);
        }
    }

    /**
     * 执行下拉
     */
    private void pullDownEvent() {
        final int newScrollValue;
        final int itemDimension;
        final float initialMotionValue, lastMotionValue;
        initialMotionValue = mInitialMotionY;// 手指按下的初始值
        lastMotionValue = mLastMotionY;// 当前手指的位置
        newScrollValue = Math.round(Math.min(initialMotionValue - lastMotionValue, 0) / FRICTION);
        itemDimension = getHeaderSize();
        setHeaderScroll(newScrollValue);
        if (newScrollValue != 0 && !isRefreshing()) {
            /*
             * 如果在数据刚加载出来的时间，快速向下滑动，此时mState的值是STATE_RESET，mInitialMotionY和mLastMotionY的差值的绝对值会
             * 较大，超过了itemDimension值，这种情况下不会触发状态的改变，实现者也不会设置向下刷新和时间等信息。由于是快速滑动，这可能是系统正常值
             * 跳跃；也可能是父类对touch事件进行了个别的拦截，产生了值的跳跃 增加一个初始状态的判断。能解决这个问题 liubin 2015-08-11
             */
            if (mState != STATE_PULL_TO_REFRESH && (itemDimension >= Math.abs(newScrollValue) || mState == STATE_RESET)) {
                setState(STATE_PULL_TO_REFRESH);
            } else if (mState == STATE_PULL_TO_REFRESH && itemDimension < Math.abs(newScrollValue)) {
                setState(STATE_RELEASE_TO_REFRESH);
            }
        }
    }

    // 执行上拉操作
    private void pullUpEvent() {
        final int newScrollValue;
        final float initialMotionValue, lastMotionValue;
        initialMotionValue = mInitialMotionY;// 手指按下的初始值
        lastMotionValue = mLastMotionY;// 当前手指的位置
        newScrollValue = Math.round(Math.max(initialMotionValue - lastMotionValue, 0) / FRICTION);
        scrollTo(0, newScrollValue);
    }

    /**
     * 设置状态
     */
    final void setState(int state, final boolean... params) {
        switch (state) {
            case STATE_RESET:
                onReset();
                break;
            case STATE_PULL_TO_REFRESH:
                onPullToRefresh();
                break;
            case STATE_RELEASE_TO_REFRESH:
                onReleaseToRefresh();
                break;
            case STATE_REFRESHING:
            case STATE_MANUAL_REFRESHING:
                onRefreshing(params[0]);
                break;
            case STATE_OVERSCROLLING:
                // NO-OP
                break;
        }
        // 回调监听者
        if (null != mOnPullEventListener) {
            mOnPullEventListener.onPullEvent(this, state);
        }

        // 处理多久前更新的功能
        if (mState == STATE_RESET && state == STATE_PULL_TO_REFRESH && mLastUpdatedTime > 0) {
            long time = mLastUpdatedTime;
            String label = getContext().getResources().getString(R.string.update_refresh_date,
                    StringFormatUtils.formatPullToRefreshTime(getContext(), time));
            setLastUpdatedLabel(label);
        }

        mState = state;
    }

    /**
     * 回复下拉状态
     */
    protected void onReset() {
        // 不使用额外的View
        if (!mListViewExtrasEnabled) {
            onResetInner();
            return;
        }
        // 以下为使用额外的View
        final BasePullToRefreshLoadingView originalLoadingLayout, listViewLoadingLayout;
        final int scrollToHeight, selection;
        final boolean scrollLvToEdge;
        originalLoadingLayout = mHeaderLayout;// 原始View
        listViewLoadingLayout = mHeaderLoadingView;
        scrollToHeight = -getHeaderSize();// 计算出header的高度
        selection = 0;

        if (mLayoutManager != null) {
            scrollLvToEdge = Math.abs(mLayoutManager.findFirstVisibleItemPosition() - selection) <= 1;
        } else {
            scrollLvToEdge = Math.abs(0 - selection) <= 1;
        }

        if (listViewLoadingLayout.getVisibility() == View.VISIBLE) {
            // 让原始的View可见
            originalLoadingLayout.setVisibility(View.VISIBLE);
            originalLoadingLayout.refreshing();
            // 让额外的View不可见
            listViewLoadingLayout.setVisibility(View.GONE);
            // 恢复Header的位置
            if (scrollLvToEdge && mState != STATE_MANUAL_REFRESHING) {
                mRefreshableView.scrollToPosition(selection);
                setHeaderScroll(scrollToHeight);
            }
        }
        onResetInner();
    }

    /**
     * 回复下拉状态
     */
    protected void onResetInner() {
        mDraggingMode = DRAG_MODE_NONE; // 拖拽为false
        mLayoutVisibilityChangesEnabled = true;
        smoothScrollTo(0, new OnSmoothScrollFinishedListener() {
            @Override
            public void onSmoothScrollFinished() {
                mHeaderLayout.reset();// 头View和尾view都重置
            }
        });// 位置回归
    }

    /**
     * 下拉刷新，调用mHeaderLayout的下拉刷新
     */
    protected void onPullToRefresh() {
        mHeaderLayout.pullToRefresh();
    }

    /**
     * 释放刷新，调用mHeaderLayout的释放刷新
     */
    protected void onReleaseToRefresh() {
        mHeaderLayout.releaseToRefresh();
    }

    /**
     * 刷新中
     */
    protected void onRefreshing(final boolean doScroll) {
        // 如果额外的View不存在，或者刷新时不显示View，或者adapter为空或者没有数据，采用正常的刷新中
        if (!mListViewExtrasEnabled || !mShowViewWhileRefreshing || null == mAdapter) {
            onRefreshingInner(doScroll);
            return;
        }
        // 直接回调监听者
        onRefreshingInner(false);

        final BasePullToRefreshLoadingView origLoadingView, listViewLoadingView;
        final int selection, scrollToY;
        origLoadingView = mHeaderLayout;
        listViewLoadingView = mHeaderLoadingView;
        selection = 0;
        scrollToY = getScrollY() + getHeaderSize();
        // 原始的View隐藏，并回复状态
        origLoadingView.reset();
        origLoadingView.hideAllViews();
        // 显示需要展示View
        listViewLoadingView.setVisibility(View.VISIBLE);
        listViewLoadingView.refreshing();
        if (origLoadingView.getSubHeaderText() != null
                && !TextUtils.isEmpty(origLoadingView.getSubHeaderText().getText())) {
            listViewLoadingView.setLastUpdatedText(origLoadingView.getSubHeaderText().getText().toString());
        }
        if (doScroll) {
            // 因为此时隐藏了mHeaderLayout，所以不希望因为滚动而使mHeaderLayout可见
            mLayoutVisibilityChangesEnabled = false;
            if (mState != STATE_MANUAL_REFRESHING) {
                // 移动headerView
                setHeaderScroll(scrollToY);
            }
            // 设置选中
            mRefreshableView.scrollToPosition(selection);
            // 移动到指定的位置
            smoothScrollTo(0);
        }
    }

    /**
     * 判断当前是否处于拖拽模式
     *
     * @return ture 处于拖拽模式;false 没有处于拖拽模式
     * @note 这个方法无法判断是下拉还是上拉
     */
    private boolean isBeingDragged() {
        return mDraggingMode == DRAG_MODE_DOWN || mDraggingMode == DRAG_MODE_UP;
    }

    /**
     * 刷新中
     */
    protected void onRefreshingInner(final boolean doScroll) {
        mHeaderLayout.refreshing();
        if (doScroll) {
            if (mShowViewWhileRefreshing) {// 刷新时显示View
                OnSmoothScrollFinishedListener listener = new OnSmoothScrollFinishedListener() {
                    @Override
                    public void onSmoothScrollFinished() {// 等滑动到正确位置后，开始回调刷新接口
                        notifyRefreshListener();
                    }
                };
                smoothScrollTo(-getHeaderSize(), listener);// 滑动到指定的位置，也就是让scrollY = header的高度
            } else {
                notifyRefreshListener();
                smoothScrollTo(0);
            }
        } else {
            notifyRefreshListener();
        }
    }

    /**
     * 设置刷新的监听
     */
    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    /**
     * 回调监听
     */
    private void notifyRefreshListener() {
        if (null != mOnRefreshListener && !isNetworkDisabled()) {
            mOnRefreshListener.onRefresh(this);
        }
    }

    /**
     * 在调用onRefresh通知前台刷新数据时统一处理网络不可用的情况
     *
     * @return
     */
    private boolean isNetworkDisabled() {
        //todo 判断 是否有网络 如果没有网络提示没有 调用刷新完成方法
        // if( ){
        // Toast.makeText();
        //  onRefreshComplete();
        // return true;
        //  }
        return false;
    }

    @Override
    protected final void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        refreshLoadingViewsSize();
        refreshRefreshableViewSize(w, h);
        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }

    @Override
    protected final void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mScrollingWhileRefreshingEnabled = bundle.getBoolean(STATE_SCROLLING_REFRESHING_ENABLED, false);
            mShowViewWhileRefreshing = bundle.getBoolean(STATE_SHOW_REFRESHING_VIEW, true);
            super.onRestoreInstanceState(bundle.getParcelable(STATE_SUPER));
            int viewState = bundle.getInt(STATE_STATE, 0);
            if (viewState == STATE_REFRESHING || viewState == STATE_MANUAL_REFRESHING) {
                setState(viewState, true);
            }
            return;
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected final Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putInt(STATE_STATE, mState);
        bundle.putBoolean(STATE_SCROLLING_REFRESHING_ENABLED, mScrollingWhileRefreshingEnabled);
        bundle.putBoolean(STATE_SHOW_REFRESHING_VIEW, mShowViewWhileRefreshing);
        bundle.putParcelable(STATE_SUPER, super.onSaveInstanceState());
        return bundle;
    }

    /**
     * 滚动下拉刷新的View
     */
    protected final void setHeaderScroll(int value) {
        // 获取下拉刷新的最大距离
        final int maxPullScroll = getMaxPullScroll();
        // 对取值进行范围约束
        value = Math.min(maxPullScroll, Math.max(-maxPullScroll, value));
        // 当mHeaderLayout滑出屏幕外时是否隐藏，有必要？
        if (mLayoutVisibilityChangesEnabled) {
            if (value < 0) {
                mHeaderLayout.setVisibility(View.VISIBLE);
            } else {
                mHeaderLayout.setVisibility(View.INVISIBLE);
            }
        }
        // 移动到指定的位置
        scrollTo(0, value);
    }

    /**
     * 移动动画
     */
    protected final void smoothScrollTo(int scrollValue) {
        smoothScrollTo(scrollValue, SMOOTH_SCROLL_DURATION_MS);
    }

    /**
     * 移动动画
     */
    protected final void smoothScrollTo(int scrollValue, OnSmoothScrollFinishedListener listener) {
        smoothScrollTo(scrollValue, SMOOTH_SCROLL_DURATION_MS, 0, listener);
    }

    /**
     * 移动动画
     */
    private final void smoothScrollTo(int scrollValue, long duration) {
        smoothScrollTo(scrollValue, duration, 0, null);
    }

    /**
     * 移动动画
     */
    private final void smoothScrollTo(int newScrollValue, long duration, long delayMillis,
                                      OnSmoothScrollFinishedListener listener) {
        if (null != mCurrentSmoothScrollRunnable) {// 停止之前的任务
            mCurrentSmoothScrollRunnable.stop();
        }
        final int oldScrollValue;
        oldScrollValue = getScrollY();

        if (oldScrollValue != newScrollValue) {
            if (null == mScrollAnimationInterpolator) {
                // 使用默认的插入器
                mScrollAnimationInterpolator = new DecelerateInterpolator();
            }
            // 创建新任务
            mCurrentSmoothScrollRunnable = new SmoothScrollRunnable(oldScrollValue, newScrollValue, duration, listener);
            // 执行任务
            if (delayMillis > 0) {
                postDelayed(mCurrentSmoothScrollRunnable, delayMillis);
            } else {
                post(mCurrentSmoothScrollRunnable);
            }
        }
    }

    public RecyclerView getRefreshableView() {
        return mRefreshableView;
    }

    /**
     * 设置刷新模式
     *
     * @param mode
     */
    public void setPullToRefreshMode(Mode mode) {
        this.mMode = mode;
    }

    public static enum Mode {

        /**
         * Disable all Pull-to-Refresh gesture and Refreshing handling
         */
        DISABLED,

        /**
         * Only allow the user to Pull from the start of the Refreshable View to refresh. The start is either the Top or
         * Left, depending on the scrolling direction.
         */
        PULL_FROM_START,

        /**
         * Only allow the user to Pull from the end of the Refreshable View to refresh. The start is either the Bottom
         * or Right, depending on the scrolling direction.
         */
        PULL_FROM_END,

        /**
         * Allow the user to both Pull from the start, from the end to refresh.
         */
        BOTH,

    }

    public void setTouchEventInterceptor(TouchEventInterceptor interceptor) {
        this.mTouchEventInterceptor = interceptor;
    }

    // ==========================================================================
// Inner/Nested Classes
// ==========================================================================
    public static interface OnSmoothScrollFinishedListener {
        void onSmoothScrollFinished();
    }

    public static interface OnRefreshListener {
        public void onRefresh(final PullToRefreshRecyclerViewWrapper refreshView);
    }

    public static interface OnPullEventListener {
        public void onPullEvent(final PullToRefreshRecyclerViewWrapper refreshView, int state);
    }

    final class SmoothScrollRunnable implements Runnable {
        private final Interpolator mInterpolator;
        private final int mScrollToY;
        private final int mScrollFromY;
        private final long mDuration;
        private OnSmoothScrollFinishedListener mListener;

        private boolean mContinueRunning = true;
        private long mStartTime = -1;
        private int mCurrentY = -1;

        public SmoothScrollRunnable(int fromY, int toY, long duration, OnSmoothScrollFinishedListener listener) {
            mScrollFromY = fromY;
            mScrollToY = toY;
            mInterpolator = mScrollAnimationInterpolator;
            mDuration = duration;
            mListener = listener;
        }

        @Override
        public void run() {
            // 如果没有设置开始时间，则以当前时间做为开始时间
            if (mStartTime == -1) {
                mStartTime = System.currentTimeMillis();
            } else {
                // 用当前时间减开始时间再乘以1000，是为了除以总时间时减少四舍五入的误差
                long normalizedTime = (1000 * (System.currentTimeMillis() - mStartTime)) / mDuration;
                // 在比例时间和1000中取小值，是因为之前除了总时间，所以比例时间是绝对小于1*1000的，这里是为了防止时间超过后，还在运行
                // 并且比例时间是需要大于0的，这是对比例时间的一种约束
                normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);
                // 用mScrollFromY - mScrollToY，算出的间距 ，根据插入器计算出当前需要移动的值
                final int deltaY = Math.round((mScrollFromY - mScrollToY)
                        * mInterpolator.getInterpolation(normalizedTime / 1000f));
                mCurrentY = mScrollFromY - deltaY;
                // 移动到指定的位置
                setHeaderScroll(mCurrentY);
            }

            // 如果目标位置不等于当前位置，则继续
            if (mContinueRunning && mScrollToY != mCurrentY) {
                PullToRefreshRecyclerViewWrapper.this.postDelayed(this, 16);// 是否需要16毫秒的延迟？
            } else {
                if (null != mListener) {
                    mListener.onSmoothScrollFinished();
                }
            }
        }

        // 停止
        public void stop() {
            mContinueRunning = false;
            // 感觉没有必要移除，
            removeCallbacks(this);
        }
    }
}
