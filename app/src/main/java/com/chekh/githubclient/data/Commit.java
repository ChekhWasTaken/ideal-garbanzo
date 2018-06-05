package com.chekh.githubclient.data;

/**
 * Created by ashota on 3/17/18.
 */

public final class Commit {
    public final int repositoryId;
    public final String sha;
    public final String message;
    public final String author;
    public final String date;

    public Commit(int repositoryId, String sha, String message, String author, String date) {
        this.repositoryId = repositoryId;
        this.sha = sha;
        this.message = message;
        this.author = author;
        this.date = date;
    }

    public static final class Builder {
        private int repositoryId;
        private String sha;
        private String message;
        private String author;
        private String date;

        public void setRepositoryId(int repositoryId) {
            this.repositoryId = repositoryId;
        }

        public void setSha(String sha) {
            this.sha = sha;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Commit build() {
            return new Commit(repositoryId, sha, message, author, date);
        }
    }
}
