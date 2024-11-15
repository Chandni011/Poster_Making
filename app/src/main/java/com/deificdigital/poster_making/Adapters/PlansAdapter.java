package com.deificdigital.poster_making.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import com.deificdigital.poster_making.Premium_Description_Activity;
import com.deificdigital.poster_making.R;
import com.deificdigital.poster_making.models.AllPlans;
import java.util.List;

public class PlansAdapter extends RecyclerView.Adapter<PlansAdapter.PlanViewHolder> {

    private List<AllPlans> plansList;
    private Context context;

    public PlansAdapter(List<AllPlans> plansList, Context context) {
        this.plansList = plansList;
        this.context = context;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.premium_description_layout, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        AllPlans plan = plansList.get(position);
        holder.priceTextView.setText(plan.getPlanDetails().getPlanData().getPrice());
        holder.durationTextView.setText(plan.getPlanDetails().getPlanData().getDuration() + " days");
        holder.planName.setText(plan.getPlanDetails().getPlanData().getPlan_name());

        holder.btnSubscription.setOnClickListener(v -> {
            if (context instanceof Premium_Description_Activity) {
                Premium_Description_Activity activity = (Premium_Description_Activity) context;
                activity.recordTransactionAndStartPayment(plan.getPlanDetails().getPlanData());
            }
        });
    }

    @Override
    public int getItemCount() {
        return plansList.size();
    }

    public static class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView durationTextView, planName, priceTextView;
        AppCompatButton btnSubscription;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            priceTextView = itemView.findViewById(R.id.tvRs);
            durationTextView = itemView.findViewById(R.id.tvDuration);
            planName = itemView.findViewById(R.id.tvPlanName);
            btnSubscription = itemView.findViewById(R.id.btn_subscription);
        }
    }
}