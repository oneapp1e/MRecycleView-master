package com.mlr.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */
public class ViewTypeInfo implements Parcelable {

    // ==========================================================================
    // Constants
    // ==========================================================================

    /**
     * 未指定视图类型
     */
    public static final int VIEW_TYPE_NONE = -1;

    // ==========================================================================
    // Fields
    // ==========================================================================

    protected int viewType = VIEW_TYPE_NONE;

    // ==========================================================================
    // Constructors
    // ==========================================================================

    public ViewTypeInfo() {
    }

    // ==========================================================================
    // Getters
    // ==========================================================================

    /**
     * 获得该对象对应的视图类型
     *
     * @return 视图类型
     */
    public int getViewType() {
        return viewType;
    }

    // ==========================================================================
    // Setters
    // ==========================================================================

    /**
     * 设置该对象对应的视图类型
     *
     * @param viewType 视图类型
     */
    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    // ==========================================================================
    // Methods
    // ==========================================================================


    // ==========================================================================
    // Inner/Nested Classes
    // ==========================================================================

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.viewType);
    }

    protected ViewTypeInfo(Parcel in) {
        this.viewType = in.readInt();
    }

    public static final Creator<ViewTypeInfo> CREATOR = new Creator<ViewTypeInfo>() {
        @Override
        public ViewTypeInfo createFromParcel(Parcel source) {
            return new ViewTypeInfo(source);
        }

        @Override
        public ViewTypeInfo[] newArray(int size) {
            return new ViewTypeInfo[size];
        }
    };
}
