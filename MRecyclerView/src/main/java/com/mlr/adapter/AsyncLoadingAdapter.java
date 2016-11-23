package com.mlr.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;

import com.mlr.mrecyclerview.R;
import com.mlr.mrecyclerview.MRecyclerView;
import com.mlr.utils.LogUtils;


/**
 * 分步加载列表适配器
 * (Description)
 *
 * @author mulinrui
 */
public abstract class AsyncLoadingAdapter extends RecyclerView.Adapter<ViewHolder> {
    // ==========================================================================
    // Constants
    // ==========================================================================
    /**
     * 默认每次加载更多的项数
     */
    private static final int DEFAULT_INCREMENT = 20;
    /**
     * 默认预加载提前量
     */
    private static final int DEFAULT_PRELOAD_COUNT = 5;
    /**
     * 最大项数
     */
    private static final int ITEM_COUNT_LIMIT = Integer.MAX_VALUE - 1;
    /**
     * 更多视图类型
     */
    protected static final int VIEW_TYPE_MORE = 1;
    /**
     * 到底了视图类型
     */
    protected static final int VIEW_TYPE_END = 2;
    /**
     * 普通item视图类型
     */
    protected static final int VIEW_TYPE_ITEM = 3;

    /**
     * 阻塞最大循环次数
     */
    private static final int MAX_BLOCK_LOOP_CNT = 300;
    /**
     * 阻塞轮询周期
     */
    private static final int CHECK_BLOCK_INTERVAL = 100;

    // ==========================================================================
    // Fields
    // ==========================================================================
    private Context mContext;
    private volatile boolean mLoading;
    /**
     * 更多视图是否启用  默认启用
     */
    private volatile boolean mMoreEnabled;
    /**
     * 到底了视图是否启用
     */
    private volatile boolean mToEndEnabled;
    /**
     * 是否能往上滑动 标记内容大于一屏显示到底了视图
     */
    private volatile boolean mCanScrollUp;
    /**
     * item限制
     */
    private volatile int mItemLimit;
    /**
     * 滚动列表
     */
    private MRecyclerView mRecyclerView;

    // ==========================================================================
    // Constructors
    // ==========================================================================
    AsyncLoadingAdapter(Context context) {
        mContext = context;
        mLoading = false;
        mMoreEnabled = true;
        mItemLimit = ITEM_COUNT_LIMIT;
    }

    // ==========================================================================
    // Getters
    // ==========================================================================
    public Context getContext() {
        return mContext;
    }

    public boolean isMoreEnabled() {
        return mMoreEnabled;
    }

    // ==========================================================================
    // Setters
    // ==========================================================================
    void setMoreEnabled(boolean enabled) {
        mMoreEnabled = enabled;
    }

    /**
     * 开启到底了试图 必须传入列表view
     *
     * @param enabled      enabled
     * @param recyclerView recyclerView
     */
    public void setToEndEnabled(boolean enabled, MRecyclerView recyclerView) {
        mToEndEnabled = enabled;
        mRecyclerView = recyclerView;
    }

    public boolean setItemLimit(int limit) {
        if (limit == Integer.MAX_VALUE) {
            LogUtils.e("Item limit should be less than Integer.MAX_VALUE " + Integer.MAX_VALUE);
            return false;
        }
        mItemLimit = limit;
        return true;
    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * 获取列表当前实际项数
     *
     * @return 列表当前实际项数
     */
    public abstract int getCount();

    /**
     * 获取列表每次加载更多时加载的项数。默认值为{@link #DEFAULT_INCREMENT}。
     *
     * @return 每次加载更多时加载的项数
     */
    int getIncrement() {
        return DEFAULT_INCREMENT;
    }

    /**
     * 获取列表预加载提前量。假设提前量是2，那么列表在滚动到倒数第2项时，就会提前开 始下一页更多项的加载。默认值为
     * {@link #DEFAULT_PRELOAD_COUNT}。
     *
     * @return 预加载提前量
     */
    private int getPreloadCount() {
        return DEFAULT_PRELOAD_COUNT;
    }

    /**
     * 是否还有更多项
     *
     * @return true表示还可以继续加载，false表示全部项已经加载完毕。
     */
    public abstract boolean hasMore();

    /**
     * 子类重写该方法实现加载更多项的逻辑。该方法会在非UI线程内异步执行。
     *
     * @param startPosition 加载更多的起始项位置
     * @param requestSize   请求加载的项数
     * @return 实际加载的项数
     */
    protected abstract int onLoadMore(int startPosition, int requestSize);

    /**
     * 获取指定位置的列表项的类型
     *
     * @param position 在列表中的位置
     * @return 该列表项的类型
     */
    protected int getContentItemViewType(int position) {
        return VIEW_TYPE_ITEM;
    }

    /**
     * 获取列表项的视图
     */
    protected abstract ViewHolder createItemViewHolder(ViewGroup parent, int viewType);

    /**
     * 获取“更多”项的视图
     */
    public abstract ViewHolder createMoreViewHolder(ViewGroup parent, int viewType);

    /**
     * 获取底部到底了视图
     *
     * @param parent   parent
     * @param viewType viewType
     * @return ViewHolder
     */
    private ViewHolder createEndViewHolder(ViewGroup parent, int viewType) {
        View v = View.inflate(mContext, R.layout.list_to_end, null);
        return new ViewHolder(v) {
        };
    }

    /**
     * 如果返回false，异步的loadMore线程将发生阻塞，直到返回true或者超时。
     *
     * @return boolean
     */
    protected boolean readyForLoadMore(int startPosition, int requestSize) {
        return true;
    }

    private int getItemLimit() {
        return mItemLimit;
    }

    /**
     * 加载更多
     */
    private synchronized void loadMore() {
        if (!mLoading) {
            mLoading = true;
        } else {
            // Already loading
            return;
        }
        final int itemCount = getCount() - getFixedCount() - getHeaderCount();
        Runnable loadRunnable = new Runnable() {

            @Override
            public void run() {
                int increment = Math.min(getIncrement(), getItemLimit() - getCount());
                int checkCount = 0;
                while (!readyForLoadMore(itemCount, increment) && checkCount++ < MAX_BLOCK_LOOP_CNT) {
                    LogUtils.w("Block load more until ready!");
                    try {
                        Thread.sleep(CHECK_BLOCK_INTERVAL);
                    } catch (Exception e) {
                        LogUtils.e(e);
                    }
                }
                onLoadMore(itemCount, increment);

            }

        };
        Runnable refreshUIRunnable = new Runnable() {

            @Override
            public void run() {
                // if (result != null && result > 0) {
                // mItemCount += result;
                // }
                notifyDataSetChanged();
                mLoading = false;
            }

        };
        loadAndShow(loadRunnable, refreshUIRunnable);
    }

    /**
     * 固定数据
     * 不需要分页加载更多判断的数据
     * 有不需要加载的数据时必须重写该方法
     *
     * @return int
     */
    protected int getFixedCount() {
        return 0;
    }

    /**
     * 去除headerView的数量
     * 如果使用headerView必须重写该方法
     *
     * @return private
     */
    protected int getHeaderCount() {
        return 0;
    }

    private void loadAndShow(final Runnable loadRunnable, final Runnable refreshUIRunnable) {
        new AsyncTask<Integer, Void, Void>() {

            @Override
            protected Void doInBackground(Integer... params) {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                loadRunnable.run();
                traceToEnd();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                refreshUIRunnable.run();
            }

        }.execute();
    }

    /**
     * 检测到底状态
     */
    private void traceToEnd() {
        // 页面能往上滑动 底下就会出现“到底儿了”  当第一个可见view不是0说明页面可以滑动
        if (mToEndEnabled && !mCanScrollUp && !hasMore()) {
            int firstVisibleItem = 0;
            if (mRecyclerView != null) {
                firstVisibleItem = mRecyclerView.getChildCount() <= 0 ? 0 : mRecyclerView.getLayoutManager().findFirstVisibleItemPosition();
            }
            LogUtils.e("traceToEnd  firstVisibleItem:" + firstVisibleItem);
            // 到底视图可用时 往上滑动 数据需要重刷
            if (firstVisibleItem > 0) {
                mCanScrollUp = true;
                mRecyclerView.post(new Runnable() {

                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }
    }

    @Override
    public final int getItemCount() {
        final int itemCount = getCount();
        if (mMoreEnabled && hasMore() && (itemCount < getItemLimit())) {
            return itemCount + 1;
        } else if (mToEndEnabled && mCanScrollUp && !hasMore()) {
            return itemCount + 1;
        } else {
            return itemCount;
        }
    }

    @Override
    public final int getItemViewType(int position) {
        if (position < getCount()) {
            return getContentItemViewType(position);
        }
        if (hasMore() && mMoreEnabled) {
            return VIEW_TYPE_MORE;
        }
        return VIEW_TYPE_END;
    }

    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        if (viewType == VIEW_TYPE_MORE) {
            viewHolder = createMoreViewHolder(parent, viewType);
        } else if (viewType == VIEW_TYPE_END) {
            viewHolder = createEndViewHolder(parent, viewType);
        } else {
            viewHolder = createItemViewHolder(parent, viewType);
        }

        if (viewHolder == null) {
            LogUtils.e("Found NULL view at " + viewType + "!", new Exception());
            return new ViewHolder(new View(getContext())) {
            };
        }
        return viewHolder;
    }

    @Override
    public final void onBindViewHolder(final ViewHolder holder, final int position) {
        final int itemCount = getCount();
        if ((position >= itemCount - 1 - getPreloadCount()) && (itemCount < getItemLimit()) && hasMore()
                && mMoreEnabled) {
            // load more items
            loadMore();
        }

        int viewType = holder.getItemViewType();
        if (viewType == VIEW_TYPE_MORE) {
            bindMoreViewHolder(holder, position, viewType);
        } else if (viewType == VIEW_TYPE_END) {
            bindEndViewHolder(holder, position, viewType);
        } else {
            bindItemViewHolder(holder, position, viewType);
        }


    }

    /**
     * 数据处理
     */
    protected abstract void bindItemViewHolder(ViewHolder holder, int position, int viewType);

    protected abstract void bindMoreViewHolder(ViewHolder holder, int position, int viewType);

    private void bindEndViewHolder(ViewHolder holder, int position, int viewType) {
    }

    // ==========================================================================
    // Inner/Nested Classes
    // ==========================================================================
}
