package com.example.recyclerviewdemo;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private RecyclerView studentRecyclerView;
    private StudentAdapter studentAdapter;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout refreshLayout;

    private Context context;
    private StudentListHelper listHelper;
    private List<Student> studentList;
    private Handler handler;    // This is used to create a new thread delay when fetching new data
                                // You don't have to use this depending on your implementation

    public MainActivityFragment() {}


    public static MainActivityFragment newInstance() {
        return new MainActivityFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize variables

        studentList = new ArrayList<Student>();
        listHelper = new StudentListHelper();
        handler = new Handler();

        listHelper.addToList(studentList);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        studentRecyclerView = (RecyclerView) view.findViewById(R.id.student_recycler_view);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.student_swipe_refresh);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup layouts

        studentRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(context);
        studentRecyclerView.setLayoutManager(layoutManager);
        studentAdapter = new StudentAdapter(studentList,
                studentRecyclerView, listHelper.getMaxSize());
        studentRecyclerView.setAdapter(studentAdapter);

        // Set LoadMoreListener

        studentAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMoreBrief() {
                studentList.add(null); // Add progress bar and notify adapter to response the change
                studentAdapter.notifyItemInserted(studentList.size() - 1);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        studentList.remove(studentList.size() - 1);
                        studentAdapter.notifyItemRemoved(studentList.size());
                        int start = studentList.size();
                        listHelper.addToList(studentList);    // Update the list and notify the adapter
                        int offset = studentList.size() - start;
                        studentAdapter.notifyItemRangeInserted(start, offset);  // Have to notify the adapter every time when
                        studentAdapter.setLoaded();                             // when the data set has changed
                    }
                }, 2000);
            }
        });

        // Set refresh layout color and listener

        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                studentAdapter.setLoading();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        studentList.clear();
                        listHelper.refresh();
                        studentAdapter.setMaxSize(listHelper.getMaxSize());
                        studentAdapter.notifyDataSetChanged();
                        refreshLayout.setRefreshing(false);
                        studentAdapter.setLoaded();
                    }
                }, 2000);
            }
        });
    }


}
