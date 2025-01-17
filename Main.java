import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    private static URLShortenerService service = new URLShortenerService();
    private static int defaultLifetimeHours;

    public static void main(String[] args) {
        loadConfig();
        startNotificationThread();

        Scanner scanner = new Scanner(System.in);
        User user = service.registerUser();
        System.out.println("Здравствуйте! Ваш UUID : " + user.getUuid());

        boolean running = true;
        while (running) {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Сократить новый URL");
            System.out.println("2. Использовать уже созданный URL");
            System.out.println("3. Выйти");
            System.out.print("Ваш выбор: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    shortenUrl(scanner, user);
                    break;
                case 2:
                    resolveUrl(scanner);
                    break;
                case 3:
                    running = false;
                    System.out.println("Пока, пока!");
                    break;
                default:
                    System.out.println("Некорректный выбор, попробуйте еще раз.");
            }
        }

        scanner.close();
    }

    private static void loadConfig() {
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            Properties properties = new Properties();
            properties.load(fis);
            defaultLifetimeHours = Integer.parseInt(properties.getProperty("link_lifetime_hours", "24"));
            System.out.println("Конфигурация загружена: Время жизни ссылки по умолчанию " + defaultLifetimeHours + " часa.");
        } catch (IOException e) {
            System.out.println("Ошибка загрузки конфигурационного файла. Используется значение по умолчанию: 24 часа.");
            defaultLifetimeHours = 24;
        }
    }

    private static void startNotificationThread() {
        Thread notificationThread = new Thread(() -> {
            while (true) {
                service.checkForExpiredLinks();
                try {
                    Thread.sleep(60000); 
                } catch (InterruptedException e) {
                    System.out.println("Поток уведомлений был прерван.");
                }
            }
        });
        notificationThread.setDaemon(true);
        notificationThread.start();
    }

    private static void shortenUrl(Scanner scanner, User user) {
        System.out.print("Введите URL, который хотите сократить: ");
        String originalUrl = scanner.nextLine();

        System.out.print("Укажите количество использований для этой ссылки: ");
        int clickLimit = scanner.nextInt();
        scanner.nextLine();

        Link link = service.createShortLink(user.getUuid(), originalUrl, clickLimit, defaultLifetimeHours);
        System.out.println("\nСсылка успешно сокращена!");
        System.out.println("Исходный URL: " + link.getOriginalUrl());
        System.out.println("Сокращенный URL: " + link.getShortUrl());
        System.out.println("Ссылка активна " + defaultLifetimeHours + " часов и может быть использована " + clickLimit + " раз.");
    }

    private static void resolveUrl(Scanner scanner) {
        System.out.print("Введите сокращенный URL: ");
        String shortUrl = scanner.nextLine();
        service.resolveShortUrl(shortUrl);
    }
}
