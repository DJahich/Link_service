import java.awt.Desktop;
import java.net.URI;
import java.util.*;

public class URLShortenerService {
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, Link> shortUrlToLink = new HashMap<>();

    public User registerUser() {
        User user = new User();
        users.put(user.getUuid(), user);
        return user;
    }

    public Link createShortLink(String userUuid, String originalUrl, int clickLimit) {
        User user = users.get(userUuid);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        String shortUrl = generateShortUrl(originalUrl);
        long expirationTime = System.currentTimeMillis() + 24 * 60 * 60 * 1000; 
        Link link = new Link(originalUrl, shortUrl, expirationTime, clickLimit);
        user.addLink(link);
        shortUrlToLink.put(shortUrl, link);
        return link;
    }

    private String generateShortUrl(String originalUrl) {
        String uniqueKey = UUID.randomUUID().toString().substring(0, 6); 
        return "http://short.ly/" + uniqueKey; 
    }

    public void resolveShortUrl(String shortUrl) {
        Link link = shortUrlToLink.get(shortUrl);
        if (link == null) {
            System.out.println("Ошибка! Сокращенная ссылка не найдена!");
            return;
        }
        if (link.isExpired()) {
            System.out.println("Ошибка! Время использования истекло!");
            return;
        }
        if (!link.canClick()) {
            System.out.println("Ошибка! Максимальное количестов использований израсходовано");
            return;
        }

        link.incrementClick();
        try {
            System.out.println("Перенаправление на : " + link.getOriginalUrl());
            Desktop.getDesktop().browse(new URI(link.getOriginalUrl())); 
        } catch (Exception e) {
            System.out.println("Ошибка! Невозможно открыть ссылку.");
        }
    }
    public void checkForExpiredLinks() {
        Iterator<Map.Entry<String, Link>> iterator = shortUrlToLink.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Link> entry = iterator.next();
            Link link = entry.getValue();
            if (link.isExpired()) {
                System.out.println("Уведомление: Ссылка " + link.getShortUrl() + " истекла.");
                iterator.remove();
            }
        }
    }
    public Link createShortLink(String userUuid, String originalUrl, int clickLimit, int lifetimeHours) {
        User user = users.get(userUuid);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }
    
        String shortUrl = generateShortUrl(originalUrl);
        long expirationTime = System.currentTimeMillis() + lifetimeHours * 60 * 60 * 1000L;
        Link link = new Link(originalUrl, shortUrl, expirationTime, clickLimit);
        user.addLink(link);
        shortUrlToLink.put(shortUrl, link);
        return link;
    }
    
}
