package com.chekh.githubclient.data;

/**
 * Created by ashota on 3/17/18.
 */

public final class RepositoryOwner {
    public final int id;
    public final String login;
    public final String avatarUrl;

    public RepositoryOwner(int id, String login, String avatarUrl) {
        this.id = id;
        this.login = login;
        this.avatarUrl = avatarUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RepositoryOwner that = (RepositoryOwner) o;

        if (id != that.id) return false;
        if (!login.equals(that.login)) return false;
        return avatarUrl.equals(that.avatarUrl);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + login.hashCode();
        result = 31 * result + avatarUrl.hashCode();
        return result;
    }

    public static final class Builder {
        private int id;
        private String login;
        private String avatarUrl;

        public void setId(int id) {
            this.id = id;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public RepositoryOwner build() {
            return new RepositoryOwner(id, login, avatarUrl);
        }
    }
}
