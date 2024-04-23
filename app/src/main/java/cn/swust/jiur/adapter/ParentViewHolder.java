package cn.swust.jiur.adapter;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.swust.jiur.R;
import cn.swust.jiur.entity.DataBean;
import cn.swust.jiur.impl.ItemSpanClickListener;

public class ParentViewHolder extends RecyclerView.ViewHolder {
    private TextView tv;
    private ImageView expand;
    private View parentDashedView;

    public ParentViewHolder(@NonNull View itemView) {
        super(itemView);
        tv = itemView.findViewById(R.id.textViewSetTheme);
        expand = itemView.findViewById(R.id.imageView7);
        parentDashedView = itemView.findViewById(R.id.parent_dashed_view);
    }

    public void bindView(final DataBean dataBean, final int pos, final ItemSpanClickListener listener) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) expand
                .getLayoutParams();
        expand.setLayoutParams(params);
        tv.setText(dataBean.getParentTxt());

        if (dataBean.isExpand()) {
            expand.setRotation(90);
            parentDashedView.setVisibility(View.INVISIBLE);
        } else {
            expand.setRotation(0);
            parentDashedView.setVisibility(View.VISIBLE);
        }

        //父布局OnClick监听
        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    if (dataBean.isExpand()) {
                        listener.onHideChildren(dataBean);
                        parentDashedView.setVisibility(View.VISIBLE);
                        dataBean.setExpand(false);
                        rotationExpandIcon(90, 0);
                    } else {
                        listener.onExpandChildren(dataBean);
                        parentDashedView.setVisibility(View.INVISIBLE);
                        dataBean.setExpand(true);
                        rotationExpandIcon(0, 90);
                    }
                }
            }
        });
    }

    private void rotationExpandIcon(float from, float to) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(from, to);//属性动画
        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                expand.setRotation((Float) valueAnimator.getAnimatedValue());
            }
        });
        valueAnimator.start();
    }
}
