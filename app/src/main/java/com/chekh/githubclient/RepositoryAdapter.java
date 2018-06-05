package com.chekh.githubclient;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.chekh.githubclient.data.Repository;
import com.chekh.githubclient.databinding.ListItemRepositoryBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ashota on 3/17/18.
 */

public class RepositoryAdapter extends RecyclerView.Adapter<RepositoryAdapter.RepositoryViewHolder> {
    private List<Repository> repositories = new ArrayList<>();

    public void addItems(List<Repository> repositories) {
        int from = this.repositories.size();
        this.repositories.addAll(repositories);
        notifyItemRangeInserted(from, repositories.size());
    }

    public void update(Repository updated) {
        int position = repositories.indexOf(updated);
        repositories.remove(position);
        repositories.add(position, updated);
        notifyItemChanged(position);
    }

    @Override
    public RepositoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemRepositoryBinding binding = DataBindingUtil.inflate(inflater, R.layout.list_item_repository, parent, false);

        return new RepositoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RepositoryViewHolder holder, int position) {
        holder.bind(repositories.get(position));
    }

    @Override
    public int getItemCount() {
        return repositories.size();
    }

    static final class RepositoryViewHolder extends RecyclerView.ViewHolder {
        private final ListItemRepositoryBinding binding;

        public RepositoryViewHolder(ListItemRepositoryBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        public void bind(Repository repository) {
            binding.setRepository(repository);
            binding.executePendingBindings();
        }
    }
}
