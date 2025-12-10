package exceptions;
//этот класс исключения нужен для тестовой работы демораннера(нужно 3 чела чтобы выбросить noseatsavaible,
// в случае если нашлось меньше выбросит это исключение)
public class NotEnoughCustomersException extends Exception {
    public NotEnoughCustomersException(String msg) { super(msg); }
}