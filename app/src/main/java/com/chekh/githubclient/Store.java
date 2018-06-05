package com.chekh.githubclient;

import android.os.AsyncTask;

import com.chekh.githubclient.data.Commit;
import com.chekh.githubclient.data.Repository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by ashota on 3/17/18.
 */

public final class Store {
    private static final String GET_REPOSITORIES_FOR_USER = "https://api.github.com/users/%s/repos";
    private static final String GET_LATEST_COMMIT_FOR_RESPOSITORY = "https://api.github.com/repos/%s/%s/commits?per_page=1";

    public static final int ERROR_MALFORMED_URL = 601;
    public static final int ERROR_TIMEOUT = 602;
    public static final int ERROR_FAILURE = 603;
    public static final int ERROR_JSON = 604;

    public void getRepositoriesForUser(String user, final Callback<List<Repository>> callback) {
        new Worker(new Callback<String>() {
            @Override
            public void onDataReady(String jsonString) {
                try {
                    JSONArray repositoriesJson = new JSONArray(jsonString);
                    List<Repository> repositories = new ArrayList<>(repositoriesJson.length());

                    for (int i = 0; i < repositoriesJson.length(); i++) {
                        Repository.Builder builder = new Repository.Builder();

                        JSONObject repository = repositoriesJson.getJSONObject(i);
                        JSONObject owner = repository.getJSONObject(Keys.Repository.OWNER);

                        builder.setRepositoryId(repository.getInt(Keys.Repository.ID));
                        builder.setRepositoryName(repository.getString(Keys.Repository.NAME));

                        builder.setOwnerId(owner.getInt(Keys.RepositoryOwner.ID));
                        builder.setOwnerLogin(owner.getString(Keys.RepositoryOwner.LOGIN));
                        builder.setOwnerAvatarUrl(owner.getString(Keys.RepositoryOwner.AVATAR_URL));

                        repositories.add(builder.build());
                    }

                    callback.onDataReady(repositories);
                } catch (JSONException e) {
                    callback.onFailure(ERROR_JSON, e.getMessage());
                }
            }

            @Override
            public void onFailure(int code, String message) {
                callback.onFailure(code, message);
            }
        }).setEndpoint(format(GET_REPOSITORIES_FOR_USER, user))
                .setMethod("GET")
                .execute();
    }

    public void getLatestCommitForRepository(String user, final Repository repository, final Callback<Commit> callback) {
        new Worker(new Callback<String>() {
            @Override
            public void onDataReady(String data) {
                try {
                    JSONObject latestCommitJson = new JSONArray(data).getJSONObject(0);
                    JSONObject commitDataJson = latestCommitJson.getJSONObject(Keys.Commit.COMMIT);
                    JSONObject commitAuthorDataJson = commitDataJson.getJSONObject(Keys.Commit.COMMIT_AUTHOR);

                    Commit.Builder builder = new Commit.Builder();

                    builder.setRepositoryId(repository.id);

                    builder.setSha(latestCommitJson.getString(Keys.Commit.SHA));

                    builder.setMessage(commitDataJson.getString(Keys.Commit.COMMIT_MESSAGE));

                    builder.setDate(commitAuthorDataJson.getString(Keys.Commit.COMMIT_AUTHOR_DATE));
                    builder.setAuthor(commitAuthorDataJson.getString(Keys.Commit.COMMIT_AUTHOR_NAME));

                    callback.onDataReady(builder.build());
                } catch (JSONException e) {
                    callback.onFailure(ERROR_JSON, e.getMessage());
                }
            }

            @Override
            public void onFailure(int code, String message) {
                callback.onFailure(code, message);
            }
        }).setEndpoint(format(GET_LATEST_COMMIT_FOR_RESPOSITORY, user, repository.name)).setMethod("GET").execute();
    }

    private String format(String format, Object... args) {
        return String.format(Locale.US, format, args);
    }


    private static final class Keys {
        private static final class Repository {
            private static final String ID = "id";
            private static final String NAME = "name";
            private static final String OWNER = "owner";
        }

        private static final class RepositoryOwner {
            private static final String ID = "id";
            private static final String LOGIN = "login";
            private static final String AVATAR_URL = "avatar_url";

        }

        private static final class Commit {
            private static final String SHA = "sha";
            private static final String COMMIT = "commit";
            private static final String COMMIT_MESSAGE = "message";
            private static final String COMMIT_AUTHOR = "author";
            private static final String COMMIT_AUTHOR_NAME = "name";
            private static final String COMMIT_AUTHOR_DATE = "date";

        }
    }

    private final static class Worker extends AsyncTask<Void, Void, String> {
        private String endpoint;
        private String method;
        private final Callback<String> callback;

        public Worker(Callback<String> callback) {
            this.callback = callback;
        }

        private String getResponseText(InputStream inStream) throws IOException {

            BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }

            br.close();
            return sb.toString();
        }

        public Worker setEndpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Worker setMethod(String method) {
            this.method = method;
            return this;
        }

        @Override
        protected String doInBackground(Void... none) {
            if (endpoint == null || endpoint.isEmpty()) {
                throw new InvalidParameterException("`endpoint` cannot be empty");
            }

            if (method == null || method.isEmpty()) {
                throw new InvalidParameterException("`method` cannot be empty");
            }

            HttpURLConnection urlConnection = null;
            try {
                URL urlToRequest = new URL(endpoint);
                urlConnection = (HttpURLConnection) urlToRequest.openConnection();
                urlConnection.setRequestMethod(method);
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(false);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                int statusCode = urlConnection.getResponseCode();

                if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    callback.onFailure(statusCode, "Unauthorized");
                    return null;
                } else if (statusCode != HttpURLConnection.HTTP_OK) {
                    callback.onFailure(statusCode, urlConnection.getResponseMessage());
                    return null;
                }

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                return getResponseText(in);
            } catch (MalformedURLException e) {
                callback.onFailure(ERROR_MALFORMED_URL, e.getMessage());
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                callback.onFailure(ERROR_TIMEOUT, e.getMessage());
            } catch (IOException e) {
                callback.onFailure(ERROR_FAILURE, e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String jsonString) {
            if (jsonString != null && !jsonString.isEmpty()) callback.onDataReady(jsonString);
        }
    }

    public interface Callback<T> {
        void onDataReady(T data);

        void onFailure(int code, String message);
    }
}
