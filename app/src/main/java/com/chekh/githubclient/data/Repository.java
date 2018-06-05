package com.chekh.githubclient.data;

/**
 * Created by ashota on 3/17/18.
 */

public final class Repository {
    public final int id;
    public final String name;
    public final RepositoryOwner owner;
    public final Commit latestCommit;

    public Repository(int id, String name, RepositoryOwner owner, Commit latestCommit) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.latestCommit = latestCommit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Repository that = (Repository) o;

        if (id != that.id) return false;
        if (!name.equals(that.name)) return false;
        return owner.equals(that.owner);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + owner.hashCode();
        return result;
    }

    public static final class Builder {
        private int repositoryId;
        private String repositoryName;
        private Commit latestCommit;
        private RepositoryOwner.Builder repositoryOwnerBuilder;
        private Commit.Builder commitBuilder;

        public Builder() {
            repositoryOwnerBuilder = new RepositoryOwner.Builder();
            commitBuilder = new Commit.Builder();
        }

        public void setRepositoryId(int repositoryId) {
            this.repositoryId = repositoryId;
        }

        public void setRepositoryName(String repositoryName) {
            this.repositoryName = repositoryName;
        }

        public void setOwnerId(int ownerId) {
            repositoryOwnerBuilder.setId(ownerId);
        }

        public void setOwnerLogin(String ownerLogin) {
            repositoryOwnerBuilder.setLogin(ownerLogin);
        }

        public void setOwnerAvatarUrl(String ownerAvatarUrl) {
            repositoryOwnerBuilder.setAvatarUrl(ownerAvatarUrl);
        }

        public void setLatestCommitSha(String sha) {
            commitBuilder.setSha(sha);
        }

        public void setLatestCommitMessage(String message) {
            commitBuilder.setMessage(message);
        }

        public void setLatestCommitAuthor(String author) {
            commitBuilder.setAuthor(author);
        }

        public void setLatestCommitDate(String date) {
            commitBuilder.setDate(date);
        }

        public void from(Repository repository) {
            setRepositoryId(repository.id);
            setRepositoryName(repository.name);

            setOwnerId(repository.owner.id);
            setOwnerLogin(repository.owner.login);
            setOwnerAvatarUrl(repository.owner.avatarUrl);

            if (repository.latestCommit == null) return;

            commitBuilder.setRepositoryId(repository.id);
            setLatestCommitSha(repository.latestCommit.sha);
            setLatestCommitMessage(repository.latestCommit.message);
            setLatestCommitAuthor(repository.latestCommit.author);
            setLatestCommitDate(repository.latestCommit.date);
        }

        public Repository build() {
            commitBuilder.setRepositoryId(repositoryId);
            return new Repository(repositoryId, repositoryName, repositoryOwnerBuilder.build(), latestCommit == null ? commitBuilder.build() : latestCommit);
        }

        public void setLatestCommitFrom(Commit latestCommit) {
            this.latestCommit = latestCommit;
        }
    }
}
