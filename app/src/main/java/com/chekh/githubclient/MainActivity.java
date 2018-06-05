package com.chekh.githubclient;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.chekh.githubclient.data.Commit;
import com.chekh.githubclient.data.Repository;
import com.chekh.githubclient.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Store store;
    private ActivityMainBinding binding;
    private RepositoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        adapter = new RepositoryAdapter();

        store = new Store();

        binding.listRepositories.setHasFixedSize(true);
        binding.listRepositories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.listRepositories.setAdapter(adapter);

        store.getRepositoriesForUser("mralexgray", new Store.Callback<List<Repository>>() {
            @Override
            public void onDataReady(List<Repository> data) {
                adapter.addItems(data);

                for (final Repository repository : data) {
                    store.getLatestCommitForRepository("mralexgray", repository, new Store.Callback<Commit>() {
                        @Override
                        public void onDataReady(Commit data) {
                            Repository.Builder builder = new Repository.Builder();
                            builder.from(repository);

                            builder.setLatestCommitFrom(data);

                            adapter.update(builder.build());
                        }

                        @Override
                        public void onFailure(int code, String message) {
                            System.out.println("error: " + code + " | " + "message: " + message);
                        }
                    });
                }
            }

            @Override
            public void onFailure(int code, String message) {
                System.out.println("error: " + code + " | " + "message: " + message);
            }
        });
    }
}
