package com.das.notepad;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterRecycler extends RecyclerView.Adapter<AdapterRecycler.ViewHolder> {
    private LayoutInflater layoutInflater;
    private List<Note> lNote;


    public AdapterRecycler(Context context, List<Note> lNote) {
        this.layoutInflater = LayoutInflater.from(context);
        this.lNote = lNote;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.custom_list_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Cargamos los datos de lNote en el ViewHolder
        String title = lNote.get(position).getTitle();
        String date = lNote.get(position).getDate();
        String time = lNote.get(position).getTime();
        holder.tvTitle.setText(title);
        holder.tvDate.setText(date);
        holder.tvTime.setText(time);
        holder.tvID.setText(String.valueOf(lNote.get(position).getId()));

    }

    @Override
    public int getItemCount() {
        return lNote.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvTime, tvID;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvID = itemView.findViewById(R.id.listId);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Pasamos el ID de nota seleccionada
                    Intent i = new Intent(v.getContext(), DescriptionNote.class);
                    i.putExtra("ID", lNote.get(getAdapterPosition()).getId());
                    v.getContext().startActivity(i);
                }
            });
        }
    }
}
