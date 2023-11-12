package com.hello.writer.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hello.writer.CreatePost;
import com.hello.writer.DataReturn;
import com.hello.writer.R;
import com.hello.writer.databinding.FragmentHomeBinding;
import com.hello.writer.PostAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Queue;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    boolean refresh = false;
    RecyclerView recyclerView;

    private FirebaseDatabase database;
    LinearLayoutManager manager;
    SwipeRefreshLayout swipeRefreshLayout;
    PostAdapter customAdapter;
    ArrayList list = new ArrayList<>();
    private DatabaseReference reference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        database = FirebaseDatabase.getInstance();

        recyclerView = root.findViewById(R.id.postView);
        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                firebaseData();
                refresh = true;
            }
        });

        firebaseData();


        //fab Button
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Fragment newFragment = new createPost();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment_content_main, newFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                startActivity(new Intent(getContext(), CreatePost.class));
            }
        });





        return root;
    }

    private void firebaseData() {

        recyclerView.setHasFixedSize(true);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        list = new ArrayList<>();
        customAdapter = new PostAdapter(getContext(), list);
        recyclerView.setAdapter(customAdapter);
        reference = database.getReference("posts");


        Query re = reference.orderByChild("timestamp");
        //Query re = search.orderByChild("timestamp");
        re.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    DataReturn dataReturn = ds.getValue(DataReturn.class);
                    if( dataReturn.getStatus() != null && dataReturn.getStatus().equals("approved")){
                        list.add(dataReturn);
                    }

                }
                if (refresh){
                    swipeRefreshLayout.setRefreshing(false);
                    refresh = false;
                }
                Collections.reverse(list);
                customAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.toException();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}