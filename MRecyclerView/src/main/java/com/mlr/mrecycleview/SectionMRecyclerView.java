package com.mlr.mrecycleview;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.Scroller;

import com.mlr.adapter.MRecyclerViewAdapter;
import com.mlr.utils.LogUtils;

public class SectionMRecyclerView extends MRecyclerView {
    // ==========================================================================
    // Constants
    // ==========================================================================
    /**
     * Pinned header state: don't show the header.
     */
    public static final int PINNED_HEADER_GONE = 0;

    /**
     * Pinned header state: show the header at the top of the list.
     */
    public static final int PINNED_HEADER_VISIBLE = 1;

    /**
     * Pinned header state: show the header. If the header extends beyond the bottom of the first shown element, push it
     * up and clip.
     */
    public static final int PINNED_HEADER_PUSHED_UP = 2;

    private enum TouchState {
        SCROLL, OVERSCROLL
    }

    // 回滚每秒显示帧数
    private static final int FRAME_RATE = 1000 / 60;

    // ==========================================================================
    // Fields
    // ==========================================================================
    private View mPinnedHeaderView;
    private boolean mPinnedHeaderViewVisible;

    private int mWidthMeasureSpec = -1;
    private int mHeightMeasureSpec = -1;

    private int mPinnedHeaderViewWidth;
    private int mPinnedHeaderViewHeight;

    private boolean mPinnedBelowOverlay = true;

    private int sectionViewType = -1;

    /**
     * pinnedHeader改变监听
     */
    private OnPinnedHeaderChangeListener mOnPinnedHeaderChangeListener;
    private OnPinnedHeaderClickListener mOnPinnedHeaderClickListener;
    private OnPinnedHeaderClickListenerWithEvent mOnPinnedHeaderClickListenerWE;

    private MRecyclerViewAdapter mSrcAdapter;

    // 顶部Over Scroll 开关
    private boolean mHeaderOverscrollable = false;
    // 底部Over Scroll 开关
    private boolean mFooterOverscrollable = false;

    // 底部遮盖视图
    private View mTopOverlay;
    // 底部遮盖视图（通常是NaviBar）
    private View mBottomOverlay;

    // 记录上次的Y值
    private float mLastMotionY;

    // 引入Tap机制，避免中兴等机型点击抖动
    private TouchState mTouchState = TouchState.SCROLL;
    private int mMotionY;
    private int mTouchSlop;
    private boolean mClearHeaderOffset = false;
    private boolean mClearFooterOffset = false;

    private boolean mEverLayouted = false;

    // 回滚
    private final Handler mHandler = new Handler();
    private Scroller mOverScroller;

    // 上一次顶部Item是否与ListView顶部处于衔接状态
    private boolean mLastIsJointedAtHeader = false;
    // 底部
    private boolean mLastIsJointedAtFooter = false;
    // 上一次记录的Y值，仅供解决此BUG使用
    private int mLastEdgeY = 0;
    //sectionHeaderView用于处理滑动过程中与悬浮view重叠部分的隐藏显示
    private View firstSectionBelow;

    // ==========================================================================
    // Constructors
    // ==========================================================================
    public SectionMRecyclerView(Context context) {
        super(context);
        initSectionGridView();
    }

    public SectionMRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSectionGridView();
    }


    // ==========================================================================
    // Getters
    // ==========================================================================

    public int getTopOverlayHeight() {
        return mTopOverlay == null ? 0 : mTopOverlay.getMeasuredHeight();
    }

    public int getBottomOverlayHeight() {
        return mBottomOverlay == null ? 0 : mBottomOverlay.getMeasuredHeight();
    }

    private int getItemCount() {
        return mSrcAdapter == null ? 0 : mSrcAdapter.getItemCount();
    }

    // ==========================================================================
    // Setters
    // ==========================================================================
    @Override
    public void setAdapter(Adapter adapter) {
        if (null == adapter) {
            super.setAdapter(null);
        } else {
            super.setAdapter(adapter);
            if (adapter instanceof MRecyclerViewAdapter) {
                mSrcAdapter = (MRecyclerViewAdapter) adapter;
            }
        }
    }

    public void setPinnedBelowOverlay(boolean b) {
        mPinnedBelowOverlay = b;
    }

    public void setTopOverlay(View topOverlay) {
        mTopOverlay = topOverlay;
    }

    /**
     * 底部填充视图，主要目的是为了获取高度。 ** 必须在GridView.setAdapter之前调用此方法 因为adapter中需要获取此对象的高度
     * mlr recyclerview中遮盖层没有起作用  需要检查
     *
     * @param bottomOverlay 底部遮盖层
     */
    public void setBottomOverlay(View bottomOverlay) {
        mBottomOverlay = bottomOverlay;
    }

    /**
     * 必须使用该方法给出section的viewType
     *
     * @param viewType
     */
    public void setSectionViewType(int viewType) {
        sectionViewType = viewType;
    }

    public void setOnPinnedHeaderChangeListener(OnPinnedHeaderChangeListener l) {
        mOnPinnedHeaderChangeListener = l;
    }

    public void setOnPinnedHeaderClickListener(OnPinnedHeaderClickListener l) {
        mOnPinnedHeaderClickListener = l;
    }

    public void setOnPinnedHeaderClickListenerWithEvent(OnPinnedHeaderClickListenerWithEvent l) {
        mOnPinnedHeaderClickListenerWE = l;
    }

    /**
     * 定义section位置
     */
    public void setSectionPosition(int position) {
        if (mPinnedHeaderView == null || sectionViewType == -1 || mSrcAdapter == null)
            return;
        //查询当前位置是否是sectionViewType 如果不是向下查找
        for (; position < mSrcAdapter.getItemCount(); position++) {
            if (sectionViewType == mSrcAdapter.getItemViewType(position)) {
                break;
            }
        }
        //找到后滚动到该位置
        this.scrollToPosition(position);
    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    private void initSectionGridView() {
        mOverScroller = new Scroller(getContext(), new DecelerateInterpolator());

        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();

        ensureScrollListener();

    }

    @Override
    public GridLayoutManager getLayoutManager() {
        return layoutManager;
    }

    public void setPinnedHeaderView(View view) {
        mPinnedHeaderView = view;

        // Disable vertical fading when the pinned header is present
        // TODO change ListView to allow separate measures for top and bottom
        // fading edge;
        // in this particular case we would like to disable the top, but not the
        // bottom edge.
        if (mPinnedHeaderView != null) {
            setFadingEdgeLength(0);
        }
        requestLayout();
    }

    /**
     * Computes the desired state of the pinned header for the given position of the first visible list item. Allowed
     * return values are {@link #PINNED_HEADER_GONE}, {@link #PINNED_HEADER_VISIBLE} or {@link #PINNED_HEADER_PUSHED_UP}
     * .
     */
    private int getPinnedHeaderState(int adjustPosition) {
        if (mPinnedHeaderView == null) {
            return PINNED_HEADER_GONE;
        }

        // 得到最后一行position
        int firstItemOfLastContentRow = layoutManager.findLastCompletelyVisibleItemPosition();
        if (getBottomOverlayHeight() != 0) {
            // 底部BottomOverlay效果会始终填充一个隐藏视图，所以可以可视最后item需要减去1
            // 详见：{@link AsyncLoadingAdapter#getView(int position, View convertView, ViewGroup parent) }
            firstItemOfLastContentRow -= 1;
        }
        if (adjustPosition < 0 || adjustPosition > firstItemOfLastContentRow || getItemCount() == 0) {
            return PINNED_HEADER_GONE;
        }

        // 当position为0时(特殊情况section不是从一个位置开始的)
        if (adjustPosition == 0) {
            // 检查是否应该显示悬浮view
            if (!checkCunSectionViewType(adjustPosition)) {
                return PINNED_HEADER_GONE;
            }
        }

        // Make the pinned header invisible when the ListView has an over-scroll
        // effect (on i919, for example)
        int pinnedIndex = adjustPosition - layoutManager.findFirstVisibleItemPosition();
        int pinnedTop = getPinnedHeaderTop();
        if (adjustPosition == 0) {
            View firstChild = getChildAt(pinnedIndex);
            if (firstChild != null && firstChild.getBottom() - getScrollY() > pinnedTop + firstChild.getHeight()) {
                return PINNED_HEADER_GONE;
            }
        }
        if (adjustPosition == firstItemOfLastContentRow) {
            View firstChildOfLastContentRow = getChildAt(pinnedIndex);
            if (firstChildOfLastContentRow != null
                    && firstChildOfLastContentRow.getBottom() - getScrollY() <= pinnedTop
                    + mPinnedHeaderView.getMeasuredHeight()) {
                return PINNED_HEADER_PUSHED_UP;
            }
        }

        // The header should get pushed up if the top item shown
        // is the last item in a section for a particular letter.
        int nextSectionPosition = getNextSectionViewTypePosition(adjustPosition + 1);
        // 当前可见view
        View currentView = layoutManager.findViewByPosition(adjustPosition);
        // 下一个sectionView
        if (currentView != null) {
            View nextSectionView = layoutManager.findViewByPosition(nextSectionPosition);
            if (nextSectionView != null && currentView.getBottom() == nextSectionView.getTop()) {
                return PINNED_HEADER_PUSHED_UP;
            }
        }

        // The header should gone if the top item shown
        // is the first item in a section for a particular letter and the divider above the item is still visible.
        int prevPositionSection = getPrevSectionViewTypePosition(adjustPosition - 1);

        if (currentView != null) {
            View prevSectionView = layoutManager.findViewByPosition(prevPositionSection);
            if (prevSectionView != null && currentView.getTop() == prevSectionView.getBottom()) {
                View firstChild = getChildAt(pinnedIndex);
                if (firstChild.getTop() - getScrollY() > pinnedTop) {
                    return PINNED_HEADER_GONE;
                }
            }

        }
        return PINNED_HEADER_VISIBLE;
    }

    /**
     * 检查当前位置是否是悬浮view的位置
     *
     * @return
     */
    private boolean checkCunSectionViewType(int position) {

        return sectionViewType == mSrcAdapter.getItemViewType(position);
    }

    /**
     * 获取下一个sectionViewType的位置
     *
     * @param position 起始位置
     * @return viewType
     */
    public int getNextSectionViewTypePosition(int position) {
        if (sectionViewType == -1) {
            return 0;
        }
        for (; position < mSrcAdapter.getItemCount(); position++) {
            if (sectionViewType == mSrcAdapter.getItemViewType(position)) {
                return position;
            }
        }
        return 0;
    }

    /**
     * 获取上一个sectionViewType的位置
     *
     * @param position 起始位置
     * @return viewType
     */
    public int getPrevSectionViewTypePosition(int position) {
        if (sectionViewType == -1) {
            return 0;
        }
        for (; position > 0; position--) {
            if (sectionViewType == mSrcAdapter.getItemViewType(position)) {
                return position;
            }
        }
        return 0;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidthMeasureSpec = widthMeasureSpec;
        mHeightMeasureSpec = heightMeasureSpec;
        measurePinnedHeader();
    }

    private void measurePinnedHeader() {
        if (mPinnedHeaderView != null) {
            ViewGroup.LayoutParams lp = mPinnedHeaderView.getLayoutParams();
            if (null == lp) {
                lp = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            }
            mPinnedHeaderView.setLayoutParams(lp);
            try {
                measureChild(mPinnedHeaderView, mWidthMeasureSpec, mHeightMeasureSpec);
            } catch (ArrayIndexOutOfBoundsException e) {
                LogUtils.e(e);
            }
            mPinnedHeaderViewWidth = mPinnedHeaderView.getMeasuredWidth();
            mPinnedHeaderViewHeight = mPinnedHeaderView.getMeasuredHeight();
        }
    }

    private int getPinnedHeaderTop() {
        return mPinnedBelowOverlay ? getTopOverlayHeight() : 0;
    }

    private void layoutPinnedHeader(int yOffset) {
        if (mPinnedHeaderView != null) {
            mPinnedHeaderView.layout(0, getPinnedHeaderTop() + yOffset, mPinnedHeaderViewWidth, getPinnedHeaderTop()
                    + yOffset + mPinnedHeaderViewHeight);
        }
    }

    private boolean gesture_up = true;
    private GestureDetector gesture = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (distanceY > 0) {
                gesture_up = true;
            } else if (distanceY < 0) {
                gesture_up = false;
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }
    });

    private void configurePinnedHeader() {
        if (mPinnedHeaderView == null) {
            return;
        }

        //当前悬浮view的位置
        int pinnedPosition = getAdjustPosition(layoutManager.findFirstVisibleItemPosition());
        int pinnedIndex = 0;
        int childCount = getChildCount();
        View child;
        int t = mPinnedBelowOverlay ? getTopOverlayHeight() : 0;
        boolean hasChildBelowOverlay = false;
        for (; pinnedIndex < childCount; pinnedIndex++) {
            child = getChildAt(pinnedIndex);
            if (child.getBottom() - getScrollY() > t) {
                hasChildBelowOverlay = true;
                break;
            }
        }
        if (!hasChildBelowOverlay) {
            mPinnedHeaderViewVisible = false;
            return;
        }
        pinnedPosition += pinnedIndex;

        if (mSrcAdapter != null) {
            // 如果当前页面不是sectionVIewType
            if (!checkCunSectionViewType(pinnedPosition)) {
                if (firstSectionBelow != null) {
                    mSrcAdapter.setSectionHeaderVisible(firstSectionBelow, View.VISIBLE);
                }
            }
        }

        int state = getPinnedHeaderState(pinnedPosition);
        int prevSectionViewTypePosition = getPrevSectionViewTypePosition(pinnedPosition);
        switch (state) {
            case PINNED_HEADER_GONE:
                mPinnedHeaderViewVisible = false;
                if (mSrcAdapter != null) {
                    if (pinnedPosition <= 0 && firstSectionBelow != null) {
                        mSrcAdapter.setSectionHeaderVisible(firstSectionBelow, View.VISIBLE);
                    }
                }
                break;

            case PINNED_HEADER_VISIBLE:
                measurePinnedHeader();
                // 增加mPinnedHeaderView.isLayoutRequested()条件是为了解决论坛编辑收藏专区的TextView无法及时显示的问题
                // TODO 即使这样写也有一定机率出现不能及时显示的问题  2015-10-30
                if (mPinnedHeaderView.getTop() - getScrollY() != getPinnedHeaderTop() || mPinnedHeaderView.isLayoutRequested()) {
                    layoutPinnedHeader(getScrollY());
                }
                mPinnedHeaderViewVisible = true;
                mOnPinnedHeaderChangeListener.onConfigurePinnedHeader(mPinnedHeaderView, prevSectionViewTypePosition, 255);

                View firstSectionBelowOverlay = getChildAt(pinnedIndex);

                if (mSrcAdapter != null) {
                    if (firstSectionBelowOverlay != null) {
                        if (checkCunSectionViewType(pinnedPosition)) {
                            if (firstSectionBelow != null) {
                                mSrcAdapter.setSectionHeaderVisible(firstSectionBelow, View.VISIBLE);
                            }
                            firstSectionBelow = firstSectionBelowOverlay;
                            mSrcAdapter.setSectionHeaderVisible(firstSectionBelow, View.INVISIBLE);
                            if (!gesture_up) {// 向下滑动
                                // LogUtils.i("offset ==0,firstSectionBelow.getTop()="+firstSectionBelow.getTop());
                                if (firstSectionBelow != null && firstSectionBelow.getTop() == 0) {// 跟随手指移动
                                    // firstSectionBelow.setVisibility(View.VISIBLE);
                                    // LogUtils.i("firstSectionBelow.getTop="+firstSectionBelow.getTop());
                                    mSrcAdapter.setSectionHeaderVisible(firstSectionBelow,
                                            View.VISIBLE);
                                }
                            }

                        } else {
                            if (firstSectionBelow != null) {
                                // firstSectionBelow.setVisibility(View.VISIBLE);
                                mSrcAdapter.setSectionHeaderVisible(firstSectionBelow, View.VISIBLE);
                            }
                        }
                    }
                }
                break;

            case PINNED_HEADER_PUSHED_UP:
                View firstViewBelowOverlay = getChildAt(pinnedIndex);
                if (firstViewBelowOverlay != null) {
                    int bottom = firstViewBelowOverlay.getBottom() - getScrollY();
                    int pinnedHeaderTop = getPinnedHeaderTop();
                    int pinnedHeaderBottom = pinnedHeaderTop + mPinnedHeaderView.getHeight();
                    int y;
                    int alpha;
                    if (bottom < pinnedHeaderBottom) {
                        y = (bottom - pinnedHeaderBottom);
                        alpha = 255 * (bottom - pinnedHeaderTop) / mPinnedHeaderView.getHeight();
                        // alpha = 255;
                    } else {
                        y = 0;
                        alpha = 255;
                    }
                    if (mPinnedHeaderView.getTop() - getScrollY() != getPinnedHeaderTop() + y) {
                        layoutPinnedHeader(y + getScrollY());
                    }
                    mPinnedHeaderViewVisible = true;
                    mOnPinnedHeaderChangeListener.onConfigurePinnedHeader(mPinnedHeaderView, prevSectionViewTypePosition, alpha);

                    if (mSrcAdapter != null) {
                        if (!checkCunSectionViewType(pinnedPosition))
                            if (firstSectionBelow != null) {
                                mSrcAdapter.setSectionHeaderVisible(firstSectionBelow, View.VISIBLE);
                            }
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mPinnedHeaderView != null) {
            layoutPinnedHeader(0);
            configurePinnedHeader();
        }
        if (mTopOverlay != null) {
            measureOverlay(mTopOverlay);
        }
        if (mBottomOverlay != null) {
            measureOverlay(mBottomOverlay);
        }

        if (!mEverLayouted) {
            mEverLayouted = true;
        }
    }

    private void measureOverlay(View overlay) {
        ViewGroup.LayoutParams p = overlay.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.AT_MOST);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight == LayoutParams.MATCH_PARENT) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY);
        } else if (lpHeight == LayoutParams.WRAP_CONTENT) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.UNSPECIFIED);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        }
        overlay.measure(childWidthSpec, childHeightSpec);
    }

    /**
     * 获取当前悬浮view的位置
     *
     * @param rawPosition 第一个可见的position
     * @return position
     */
    public int getAdjustPosition(int rawPosition) {
        return rawPosition;
    }


    /**
     * 设置滚动监听
     */
    private void ensureScrollListener() {
        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                onScroll();
            }
        };
        addOnScrollListener(onScrollListener);
    }

    public void onScroll() {
        if (mEverLayouted) {
            configurePinnedHeader();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        try {
            super.dispatchDraw(canvas);
            if (mPinnedHeaderViewVisible && null != mPinnedHeaderView) {
                drawChild(canvas, mPinnedHeaderView, getDrawingTime());
            }
        } catch (Exception e) {
            // dispatchDraw may throw NullPointerException
            LogUtils.e(e);
        }

    }

    // ==========================================================================
    // Over Scroll效果
    // ==========================================================================

    // protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
    // boolean clampedY) {
    // // Android 2.3以上版本是通过在此回调中修改mScrollY值实现Over Scroll和边界抖动效果
    // // 屏蔽系统ListView Over Scroll效果
    boolean isTrackPinnedHeaderClick = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // 此处主要处理PinnerHeader 点击事件
        if (mPinnedHeaderView != null
                && (mOnPinnedHeaderClickListener != null || mOnPinnedHeaderClickListenerWE != null)
                && mPinnedHeaderViewVisible) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isTrackPinnedHeaderClick = isInHeaderArea(event);
                    if (isTrackPinnedHeaderClick) {
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    if (!isInHeaderArea(event)) {
                        isTrackPinnedHeaderClick = false;
                    }
                    break;
            }
        }
        return super.onInterceptTouchEvent(event);
    }

    private boolean isInHeaderArea(MotionEvent event) {
        boolean result = false;
        if (mPinnedHeaderView != null) {
            if (event.getX() > mPinnedHeaderView.getLeft() && event.getX() < mPinnedHeaderView.getRight()
                    && event.getY() > mPinnedHeaderView.getTop() && event.getY() < mPinnedHeaderView.getBottom()) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gesture.onTouchEvent(event);
        if (mPinnedHeaderView != null
                && (mOnPinnedHeaderClickListener != null || mOnPinnedHeaderClickListenerWE != null)
                && isTrackPinnedHeaderClick) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    if (isInHeaderArea(event)) {
                        return true;
                    } else {
                        isTrackPinnedHeaderClick = false;
                        break;
                    }
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP: {
                    if (isInHeaderArea(event)) {
                        int pinnedPosition = getAdjustPosition(layoutManager.findFirstVisibleItemPosition());
                        int pinnedIndex = 0;
                        int childCount = getChildCount();
                        View child;
                        int t = mPinnedBelowOverlay ? getTopOverlayHeight() : 0;
                        boolean hasChildBelowOverlay = false;
                        for (; pinnedIndex < childCount; pinnedIndex++) {
                            child = getChildAt(pinnedIndex);
                            if (child.getBottom() - getScrollY() > t) {
                                hasChildBelowOverlay = true;
                                break;
                            }
                        }
                        if (hasChildBelowOverlay) {
                            pinnedPosition += pinnedIndex;
                            int prevSectionViewTypePosition = getPrevSectionViewTypePosition(pinnedPosition);
                            if (mOnPinnedHeaderClickListenerWE != null) {
                                mOnPinnedHeaderClickListenerWE.onPinnedHeaderClick(mPinnedHeaderView, prevSectionViewTypePosition,
                                        event.getX(), event.getY());
                            }
                            if (mOnPinnedHeaderClickListener != null) {
                                mOnPinnedHeaderClickListener.onPinnedHeaderClick(mPinnedHeaderView, prevSectionViewTypePosition);
                            }
                            return true;
                        }

                    }
                }
            }
        }
        if (!mHeaderOverscrollable && !mFooterOverscrollable) {
            // 没开启OverScroll效果
            return super.onTouchEvent(event);
        }

        final int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                resetOverScrollParams(y);

                break;

            case MotionEvent.ACTION_MOVE:

                switch (mTouchState) {
                    case SCROLL:
                        int deltaY = y - mMotionY;
                        int distance = Math.abs(deltaY);
                        // 满足父控件滚动的判断条件，复用父控件滚动时进行的以下处理
                        // Item背景点击状态清理， 阻止触发ItemClick事件
                        if (distance > mTouchSlop + 1) {

                            mLastMotionY = y;
                            mTouchState = TouchState.OVERSCROLL;
                            // 因为AbsListView.startScrollIfNeeded
                            // 执行AbsListView.scrollIfNeeded造成ListView偏移
                            // 打开开关，修正偏移值
                            if (deltaY < 0) {
                                mClearHeaderOffset = true;
                            } else {
                                mClearFooterOffset = true;
                            }

                        }

                        break;
                    case OVERSCROLL:
                        // Over Scroll
                        if (overScrollIfNeeded(event.getY())) {
                            return true;
                        }
                        break;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 回滚
                springBackIfNeeded();

                break;
        }

        if (Build.VERSION.SDK_INT >= 9) {
            if (canNotScrollDown(y) || canNotScrollUp(y)) {
                int deltaY = y - mLastEdgeY;
                return scrollOffset(event, deltaY);
            }
            mLastEdgeY = y;
        }

        // 之前未拦截，交由父控件处理
        return updateMotionY(event, getScrollY());
    }

    private boolean updateMotionY(MotionEvent ev, int offsetValue) {
        boolean returnValue;

        if (offsetValue != 0) {
            MotionEvent obtainEvent = MotionEvent.obtain(ev);
            obtainEvent.offsetLocation(0, offsetValue);
            returnValue = super.onTouchEvent(obtainEvent);
            obtainEvent.recycle();
        } else {
            returnValue = super.onTouchEvent(ev);
        }

        return returnValue;
    }

    /**
     * 视图是否可以向下滚动
     * <p/>
     * 问题根本原因： 向上滚动到边界（ListView可能处于onOverScrolled状态） 再向下滚动时由于实现的OverScroll之前拦截向父控件传递TouchEvent导致判断异常
     * <p/>
     * 详见： AbsListView.onTouchEvent ┗ MotionEvent.ACTION_MOVE ┗ TOUCH_MODE_OVERSCROLL ┗ int newDirection = y > mLastY ?
     * 1 : -1; ┗ if (mDirection != newDirection) 正常情况应该走入此if分支 mDirection -1代表向上，1代表向下 异常情况时，拦截前 mDirection == 1，
     * 再次向下滚动时mDirection == 1
     *
     * @param y y
     * @return true 当前视图不能向下滚动，但是这是BUG需要向下滚动
     */
    private boolean canNotScrollDown(int y) {

        // Bottom
        boolean atBottomEdge = atBottomEdge();
        View footer = getChildAt(getChildCount() - 1);
        int contentBottom = footer.getBottom() - getScrollY();
        boolean footerJointed = contentBottom == getHeight();

        // 向下滚动
        int deltaY = y - mLastEdgeY;

        if (deltaY > 0 && atBottomEdge && footerJointed) {
            if (mLastIsJointedAtFooter) {
                // 如果底部连续两次都处于JOINTED状态，说明已经卡住不能动

                // 重置状态
                mLastIsJointedAtFooter = false;
                return true;
            }
            // 记录本次边界值JOINTED状态
            mLastIsJointedAtFooter = true;
        } else {
            // 如果不是连续两次JOINTED状态，重置状态
            mLastIsJointedAtFooter = false;
        }
        return false;
    }

    /**
     * 视图是否可以向上滚动 {@link #canNotScrollDown}
     *
     * @param y y
     * @return true 当前视图不能向下滚动，但是这是BUG需要向上滚动
     */
    private boolean canNotScrollUp(int y) {

        // Top
        boolean atTopEdge = atTopEdge();
        View header = getChildAt(0);
        int contentTop = header.getTop() - getScrollY();
        boolean headerJointed = contentTop == 0;

        int deltaY = y - mLastEdgeY;

        if (deltaY < 0 && atTopEdge && headerJointed) {
            if (mLastIsJointedAtHeader) {

                mLastIsJointedAtHeader = false;
                return true;
            }
            mLastIsJointedAtHeader = true;

        } else {
            mLastIsJointedAtHeader = false;
        }
        return false;
    }

    /**
     * 模拟一个向相反方向的偏移，满足BUG {@link #canNotScrollDown}中的判断条件。
     *
     * @param event  event
     * @param deltaY deltaY
     * @return 是否
     */
    private boolean scrollOffset(MotionEvent event, int deltaY) {
        MotionEvent obtain = MotionEvent.obtain(event);
        obtain.offsetLocation(0, deltaY);
        boolean returnVlaue = super.onTouchEvent(obtain);
        obtain.recycle();
        return returnVlaue;
    }

    private void resetOverScrollParams(final int y) {

        mTouchState = TouchState.SCROLL;

        mLastMotionY = mMotionY = y;

        mClearHeaderOffset = false;
        mClearFooterOffset = false;

        if (!mOverScroller.isFinished()) {
            mOverScroller.forceFinished(true);
            mHandler.removeCallbacks(mSpringBackRunnable);
        }
    }

    /**
     * 如果Over Scroll在ListView顶部或底部超出Overlay的高度执行回滚操作
     */
    private void springBackIfNeeded() {

        if (getScrollY() != 0) {

            mOverScroller.startScroll(0, getScrollY(), 0, -getScrollY());
            mHandler.post(mSpringBackRunnable);

            invalidate();
        }

    }

    private boolean overScrollIfNeeded(float y) {

        boolean interceptTouchEvent = false;

        float deltaY = y - mLastMotionY;
        float scrollY = getScrollY() - deltaY;
        mLastMotionY = y;
        // 清除Float转Int造成的四舍五入干扰
        mLastMotionY += scrollY - (int) scrollY;

        if (atTopOverScroll(deltaY)) {
            interceptTouchEvent = true;
            configureHeaderOnOverscroll(scrollY);
        } else if (atBottomOverScroll(deltaY)) {
            interceptTouchEvent = true;
            configureFooterOnOverscroll(scrollY);
        }

        return interceptTouchEvent;
    }

    /**
     * 底部能否进行OverScroll
     *
     * @param deltaY deltaY
     * @return 是否
     */
    private boolean atBottomOverScroll(float deltaY) {
        boolean bottomOverscroll = false;

        // Bottom
        boolean atBottomEdge = atBottomEdge();
        View footer = getChildAt(getChildCount() - 1);
        int contentBottom = footer.getBottom() - getScrollY();

        boolean footerJointed = contentBottom == getHeight();
        boolean footerOverScrolled = contentBottom < getHeight();

        if (atBottomEdge) {
            if (deltaY < 0 && (footerJointed || footerOverScrolled)) {

                // 向上移动
                bottomOverscroll = true;
            } else if (deltaY > 0 && footerOverScrolled) {
                // 向下移动
                bottomOverscroll = true;
            }
        }
        return bottomOverscroll;
    }

    /**
     * 顶部能否进行OverScroll
     *
     * @param deltaY deltaY
     * @return 是否
     */
    private boolean atTopOverScroll(float deltaY) {
        boolean topOverscroll = false;

        // Top
        boolean atTopEdge = atTopEdge();
        View header = getChildAt(0);
        int contentTop = header.getTop() - getScrollY();

        boolean headerJointed = contentTop == 0;
        boolean headerOverScrolled = contentTop > 0;

        if (atTopEdge) {
            if (deltaY < 0 && headerOverScrolled) {
                // 向上移动
                topOverscroll = true;
            } else if (deltaY > 0 && (headerJointed || headerOverScrolled)) {
                // 向下移动
                topOverscroll = true;
            }
        }
        return topOverscroll;
    }

    /**
     * 处理ListView顶部Over Scroll
     */
    private void configureHeaderOnOverscroll(float deltaY) {

        if (!mHeaderOverscrollable) {
            return;
        }

        handleHeaderOverScrollOffset();

        // 处理边界处
        deltaY = Math.min(deltaY, 0);

        overScrollTo(deltaY);
        configurePinnedHeader();
    }

    /**
     * 处理ListView底部Over Scroll操作
     *
     * @param deltaY 本次ListView纵向移动的增量值。向上移动值为负数，反之相反
     */
    private void configureFooterOnOverscroll(float deltaY) {

        if (!mFooterOverscrollable) {
            return;
        }

        handleFooterOverScrollOffset();

        // 处理边界处
        deltaY = Math.max(deltaY, 0);

        overScrollTo(deltaY);

        configurePinnedHeader();
    }

    /**
     * 处理Over Scroll操作
     *
     * @param deltaY deltaY
     */
    private void overScrollTo(float deltaY) {

        scrollTo(0, (int) deltaY);

        // 避免拉出部分出现很多横线
        invalidateParentIfNeeded();

        // 刷新ScrollBars
        // awakenScrollBars();
    }

    private void handleHeaderOverScrollOffset() {
        if (mClearHeaderOffset) {
            // test("start = " + getChildAt(0).getTop());
            if (getChildCount() > 0) {
                View FirstView = getChildAt(0);
                if (FirstView != null) {
                    int top = FirstView.getTop();
                    for (int i = 0; i < getChildCount(); i++) {
                        View child = getChildAt(i);
                        child.offsetTopAndBottom(-top);
                    }
                    requestLayout();
                }
            }
            mClearHeaderOffset = false;
            // test("end   = " + getChildAt(0).getTop());
        }
    }

    private void handleFooterOverScrollOffset() {
        if (mClearFooterOffset) {
            // test("start = " + getChildAt(getChildCount() - 1).getBottom() + " , height = " + getHeight());
            if (getChildCount() > 0) {
                View lastView = getChildAt(getChildCount() - 1);
                if (lastView != null) {
                    int bottom = lastView.getBottom();
                    int offsetValue = bottom - getHeight();
                    for (int i = 0; i < getChildCount(); i++) {
                        View child = getChildAt(i);
                        child.offsetTopAndBottom(-offsetValue);
                    }
                    requestLayout();
                }
            }
            mClearFooterOffset = false;
            // test("end   = " + getChildAt(getChildCount() - 1).getBottom() + " , height = " + getHeight());
        }
    }

    /**
     * 判断是否处于ListView顶部边界处
     *
     * @return true是  false不是
     */
    private boolean atTopEdge() {
        int childCount = getChildCount();
        if (childCount == 0) {
            return false;
        }

        final int firstTop = getChildAt(0).getTop();

        // 滑动到顶部
        return layoutManager.findFirstVisibleItemPosition() == 0 && firstTop >= getPaddingTop();
    }

    /**
     * 判断当前是否处于底部边界处
     *
     * @return true是  false不是
     */
    private boolean atBottomEdge() {
        int childCount = getChildCount();
        if (childCount == 0) {
            return false;
        }

        final int itemCount = getItemCount();

        View lastItem = getChildAt(childCount - 1);
        final int lastBottom = lastItem.getBottom();

        final int end = getHeight() - getPaddingBottom();

        // 滑动到底部
        return layoutManager.findFirstVisibleItemPosition() + childCount >= itemCount && lastBottom <= end;
    }

    /**
     * 避免Over Scroll时出现横多横线
     * <p/>
     * view.invalidateParentIfNeeded方法是包继承权限，此处与其实现类似
     */
    private void invalidateParentIfNeeded() {

        ViewParent parent = getParent();
        if (parent != null && parent instanceof View) {
            ((View) parent).invalidate();
        }
    }

    // ==========================================================================
    // Inner/Nested Classes
    // ==========================================================================

    /**
     * pinnedHeader改变监听
     */
    public interface OnPinnedHeaderChangeListener {

        /**
         * Configures the pinned header view to match the first visible list item.
         *
         * @param header                      pinned header view.
         * @param prevSectionViewTypePosition position of the first visible list item.
         * @param alpha                       fading of the header view, between 0 and 255.
         */
        void onConfigurePinnedHeader(View header, int prevSectionViewTypePosition, int alpha);

    }

    /**
     * 处理Over Scroll Back操作
     */
    private Runnable mSpringBackRunnable = new Runnable() {

        @Override
        public void run() {

            final Scroller scroller = mOverScroller;
            if (!scroller.isFinished()) {
                scroller.computeScrollOffset();
                scrollUpdate(scroller.getCurrX(), scroller.getCurrY());
                mHandler.postDelayed(this, FRAME_RATE);
            }

        }

        private void scrollUpdate(final int x, final int y) {
            invalidate();
            // 设置滚动到何处
            scrollTo(x, y);
            invalidateParentIfNeeded();

            configurePinnedHeader();
        }

    };

    public interface OnPinnedHeaderClickListener {

        /**
         * Configures the pinned header view to match the first visible list item.
         *
         * @param header                      pinned header view.
         * @param prevSectionViewTypePosition position of the first visible list item.
         */
        void onPinnedHeaderClick(View header, int prevSectionViewTypePosition);
    }

    public interface OnPinnedHeaderClickListenerWithEvent {
        /**
         * Configures the pinned header view to match the first visible list item.
         *
         * @param header                      pinned header view.
         * @param prevSectionViewTypePosition position of the first visible list item.
         */
        void onPinnedHeaderClick(View header, int prevSectionViewTypePosition, float eventX, float eventY);
    }
}
