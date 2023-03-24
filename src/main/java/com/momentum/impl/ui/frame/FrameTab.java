package com.momentum.impl.ui.frame;

import com.momentum.impl.ui.shape.Rect;

public class FrameTab<T> extends Rect {

    final String title;
    final T category;

    public FrameTab(String title, T category) {
        this.title = title;
        this.category = category;
    }
}
