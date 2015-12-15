import org.apache.commons.math3.complex.Complex;

public class FourierTransform {

    public static Complex function(double x) {
        return new Complex(x * x);
//        return x * x;
    }

    public static Complex exponent(double x, double ksi) {
        return Complex.I.multiply(-2 * Math.PI * x * ksi).exp();
    }

    public static Complex integrand(double x, double ksi) {
        return function(x).multiply(exponent(x, ksi));
    }

    public static Complex calculateSum(double step, double a, double b, double ksi) {
        Complex sum = new Complex(0, 0);
        Complex multi;
//        double multi;
        for (double x = a; x < b; x += step) {
//            multi = function(x).multiply(step);
            multi = integrand(x, ksi).multiply(step);
//            multi = function(x) * step;
            // TODO: 16.12.2015 Переделать. Не работает метод add.
            sum = new Complex(sum.getReal() + multi.getReal(), sum.getImaginary() + multi.getImaginary());
//            sum.add(multi);
        }
        return sum;
    }

    public static void rectangleMethod(double width, double a, double b, double ksi) {
        System.out.println("Для шага = " + width + ":\t" + calculateSum(width, a, b, ksi));
    }

    public static void main(String[] args) {
        System.out.println("Функция x^2");
        System.out.println("Число отсчетов 10000.");
        System.out.println("ksi = 8");
        System.out.println("От -5 до 5");
        rectangleMethod(0.0001, -5, 5, 8);
        System.out.println();
        // TODO: 16.12.2015 ТАК ?
        rectangleMethod(0.0001, -5, -4.9999, 8);

    }
}
