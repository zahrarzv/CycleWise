package com.gitgirls.cyclewise.presentation;

import android.content.Context;

import com.gitgirls.cyclewise.R;
import com.gitgirls.cyclewise.models.Flow;

public final class FlowDisplayMapper {
    private FlowDisplayMapper() {}
    public static String toDisplayString(Context context, Flow flow) {
        switch (flow) {
            case LIGHT:
                return context.getString(R.string.flow_light);
            case MEDIUM:
                return context.getString(R.string.flow_medium);
            case HEAVY:
                return context.getString(R.string.flow_heavy);
            default:
                throw new IllegalArgumentException(
                        context.getString(R.string.error_unknown_flow_value, flow)
                );
        }
    }

    public static Flow fromDisplayString(Context context, String label) {
        if (context.getString(R.string.flow_light).equals(label)) {
            return Flow.LIGHT;
        }
        if (context.getString(R.string.flow_medium).equals(label)) {
            return Flow.MEDIUM;
        }
        if (context.getString(R.string.flow_heavy).equals(label)) {
            return Flow.HEAVY;
        }
        throw new IllegalArgumentException(
                context.getString(R.string.error_unknown_flow_value, label)
        );
    }
}