package com.example.recyclerviewdemo;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter {

    private List<Student> studentList;

    private int visibleThreshold = 2;
    private int lastVisibleItem;
    private int totalItemCount;
    private boolean loading;
    private int maxSize;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PEOG = 0;

    private OnLoadMoreListener onLoadMoreListener;

    public StudentAdapter(List<Student> list, RecyclerView recyclerView, int _maxSize) {

        // maxSize is optional if you want ot set a upper cap

        studentList = list;
        maxSize = _maxSize;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager =
                    (LinearLayoutManager) recyclerView.getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if ( !loading &&
                            totalItemCount <= (lastVisibleItem + visibleThreshold) &&
                            totalItemCount <= maxSize) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMoreBrief();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return studentList.get(position) != null ? VIEW_ITEM : VIEW_PEOG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        if ( viewType == VIEW_ITEM ) {
            View studentView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_student, parent, false);
            viewHolder = new StudentViewHolder(studentView);
        }
        else if (viewType == VIEW_PEOG){
            View progressView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_progress, parent, false);
            viewHolder = new ProgressViewHolder(progressView);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof StudentViewHolder) {
            Student student = studentList.get(position);
            TextView textView = ((StudentViewHolder) holder).studentInfoView;
            textView.setText(Integer.toString(student.getId()));
        }
        else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public void setLoaded() {
        loading = false;
    }

    public void setLoading() {
        loading = true;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public void clear() {
        studentList.clear();
        notifyDataSetChanged();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    private class StudentViewHolder extends RecyclerView.ViewHolder {

        public TextView studentInfoView;

        public StudentViewHolder(View itemView) {
            super(itemView);
            studentInfoView = (TextView) itemView.findViewById(R.id.student_info);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    Student student = studentList.get(getLayoutPosition());
                    Toast.makeText(context, student.getId(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
        }
    }
}
