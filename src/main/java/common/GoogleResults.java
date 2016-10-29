package common;

import java.util.List;

/**
 * @author YHoresh
 *
 */
public class GoogleResults {

    private ResponseData responseData;
    public ResponseData getResponseData() { return this.responseData; }
    public void setResponseData(ResponseData responseData) { this.responseData = responseData; }
    @Override
    public String toString() { return "ResponseData[" + this.responseData + "]"; }

    /**
     * @author YHoresh
     *
     */
    public static class ResponseData {
        private List<Result> results;
        public List<Result> getResults() { return this.results; }
        public void setResults(List<Result> results) { this.results = results; }
        @Override
        public String toString() { return "Results[" + this.results + "]"; }
    }

    /**
     * @author YHoresh
     *
     */
    public static class Result {
        private String url;
        private String title;
        public String getUrl() { return this.url; }
        public String getTitle() { return this.title; }
        public void setUrl(String url) { this.url = url; }
        public void setTitle(String title) { this.title = title; }
        @Override
        public String toString() { return "Result[url:" + this.url + ",title:" + this.title + "]"; }
    }
}