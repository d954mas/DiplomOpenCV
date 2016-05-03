package com.d9564mas.diplom;

import org.opencv.core.Mat;

/**
 * Created by user on 03.05.2016.
 */
public interface Filter {
    public abstract void apply(final Mat src, final Mat dst);
}
