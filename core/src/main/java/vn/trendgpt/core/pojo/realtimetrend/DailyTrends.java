package vn.trendgpt.core.pojo.realtimetrend;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyTrends {
    @SerializedName("default")
    private Default default_;
}
