package org.example;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("Выберите режим работы:");
            System.out.println("1 - готовые данные");
            System.out.println("2 - частично готовые данные (вбиты рейсы и самолеты, взаимодействие через CustomerUser)");
            System.out.println("3 - выход из программы");
            System.out.print("Ваш выбор: ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> {
                    DemoRunner.main(args);
                  exit = true;
                }
                case "2" -> {
                    ManualRunner.main(args);
                 exit = true;
                }
                case "3" -> {
                    System.out.println("Выход из программы...");
                    exit = true;
                }
                default -> System.out.println("ошибка ввода!\n");
            }
        }

    }
}
