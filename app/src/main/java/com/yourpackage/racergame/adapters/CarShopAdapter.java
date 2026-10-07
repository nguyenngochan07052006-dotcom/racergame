package com.yourpackage.racergame.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yourpackage.racergame.R;
import com.yourpackage.racergame.database.CarShop;
import com.yourpackage.racergame.database.DatabaseHelper;
import java.util.List;

public class CarShopAdapter extends ArrayAdapter<CarShop> {
    private final Context context;
    private final List<CarShop> cars;

    public CarShopAdapter(Context context, List<CarShop> cars) {
        super(context, 0, cars);
        this.context = context;
        this.cars = cars;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_car, parent, false);
        }

        CarShop car = cars.get(position);
        TextView tvCarName = convertView.findViewById(R.id.tvCarName);
        TextView tvPrice = convertView.findViewById(R.id.tvPrice);
        TextView tvStatus = convertView.findViewById(R.id.tvStatus);

        tvCarName.setText(car.carName);

        int selectedId = new DatabaseHelper(context).getSelectedCarId();

        if (car.isUnlocked) {
            if (car.id == selectedId) {
                tvPrice.setText("✅ ĐANG SỬ DỤNG");
                tvPrice.setTextColor(context.getColor(android.R.color.holo_green_light));
                tvStatus.setText("🟢 Đang chọn");
            } else {
                tvPrice.setText("👆 Chạm để chọn");
                tvPrice.setTextColor(context.getColor(android.R.color.holo_blue_light));
                tvStatus.setText("🟢 Đã sở hữu");
            }
            tvStatus.setTextColor(context.getColor(android.R.color.holo_green_light));
        } else {
            tvPrice.setText("💰 " + car.price + " coins");
            tvPrice.setTextColor(context.getColor(android.R.color.holo_orange_light));
            tvStatus.setText("🔒 Chưa mua");
            tvStatus.setTextColor(context.getColor(android.R.color.holo_red_light));
        }

        return convertView;
    }
}