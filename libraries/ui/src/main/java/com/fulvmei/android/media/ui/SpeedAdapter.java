package com.fulvmei.android.media.ui;

import java.util.List;

public interface SpeedAdapter {

    List<Speed> getSpeedList();

     Speed getSpeed(float speedValue);

    class Speed {
        public String name;
        public float value;

        public Speed(String name, float value) {
            this.name = name;
            this.value = value;
        }
    }
}
