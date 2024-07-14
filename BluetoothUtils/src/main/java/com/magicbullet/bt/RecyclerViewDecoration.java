package com.magicbullet.bt;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class RecyclerViewDecoration extends RecyclerView.ItemDecoration {
    private final int space;

    public RecyclerViewDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull @NotNull Rect outRect, @NonNull @NotNull View view, @NonNull @NotNull RecyclerView parent, @NonNull @NotNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildAdapterPosition(view) == 0)
            outRect.top = space;
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int dividerLeft = 32;
        int dividerRight = parent.getWidth() - 32;
        Drawable mDivider = ContextCompat.getDrawable(parent.getContext(), R.drawable.item_divider);
        // this loop creates the top and bottom
        // divider for each items in the RV
        // as each items are different
        for (int i = 0; i < parent.getChildCount(); i++) {

            // this condition is because the last
            // and the first items in the RV have
            // no dividers in the list
            if (i != parent.getChildCount() - 1) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)child.getLayoutParams(); //RecyclerView.LayoutParams

                // calculating the distance of the
                // divider to be drawn from the top
                int dividerTop = child.getBottom() + params.bottomMargin;
                assert mDivider != null;
                int dividerBottom = dividerTop + mDivider.getIntrinsicHeight();
                mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom);
                mDivider.draw(c);
            }
        }
    }
}


