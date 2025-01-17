public class Link {
    private final String originalUrl;
    private final String shortUrl;
    private final long expirationTime;
    private final int clickLimit;
    private int clickCount;

    public Link(String originalUrl, String shortUrl, long expirationTime, int clickLimit) {
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
        this.expirationTime = expirationTime;
        this.clickLimit = clickLimit;
        this.clickCount = 0;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expirationTime;
    }

    public boolean canClick() {
        return clickCount < clickLimit;
    }

    public void incrementClick() {
        clickCount++;
    }
}
