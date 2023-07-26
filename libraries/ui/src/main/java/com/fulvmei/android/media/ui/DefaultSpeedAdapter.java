package com.fulvmei.android.media.ui;

import java.util.ArrayList;
import java.util.List;

public class DefaultSpeedAdapter implements SpeedAdapter {

    private final List<Speed> speedList;

    public DefaultSpeedAdapter() {
        speedList = new ArrayList<>();
        speedList.add(new Speed("2.0X", 2.0f));
        speedList.add(new Speed("1.5X", 1.5f));
        speedList.add(new Speed("1.25X", 1.25f));
        speedList.add(new Speed("1.0X", 1.0f));
        speedList.add(new Speed("0.75X", 0.75f));
        speedList.add(new Speed("0.5X", 0.5f));
    }


    @Override
    public List<Speed> getSpeedList() {
        return speedList;
    }

    @Override
    public Speed getSpeed(float speedValue) {
        if (speedValue >= 2.0f) {
           return speedList.get(0);
        } else if (speedValue >= 1.5f) {
            return speedList.get(1);
        } else if (speedValue >= 1.25f) {
            return speedList.get(2);
        } else if (speedValue >= 1.0f) {
            return speedList.get(3);
        } else if (speedValue >= 0.75f) {
            return speedList.get(4);
        } else {
            return speedList.get(5);
        }
    }
}
