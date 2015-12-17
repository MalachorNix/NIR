import org.apache.commons.math3.complex.Complex;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;

public class FourierTransform {

    public static Complex transformFunction;

    public static Complex function(double x) {
//        return new Complex(x * x);
//        return new Complex(Math.sin(2 * x));
        return Complex.I.multiply(4 * x).exp();
    }

    public static Complex exponent(double x, double ksi) {
        return Complex.I.multiply(-2 * Math.PI * x * ksi).exp();
    }

    public static Complex integrand(double x, double ksi) {
        return function(x).multiply(exponent(x, ksi));
    }

    public static Complex calculateTransform(double step, double a, double b, double ksi) {
        Complex sum = new Complex(0, 0);
        Complex multi;
        for (double x = a; x < b; x += step) {
            multi = integrand(x, ksi).multiply(step);
            sum = new Complex(sum.getReal() + multi.getReal(), sum.getImaginary() + multi.getImaginary());
        }
        return sum;
    }

    public static void showAll(double width, double a, double b, double ksi) {
        transformFunction = calculateTransform(width, a, b, ksi);
        System.out.println(ksi + "\t" + transformFunction.getReal() +"\t" + transformFunction.getImaginary());
    }

    public static void main(String[] args) {

        int n = 256;
        double a = -5;
        double b = 5;
        double step = (b - a) / n;
        double precision = 0.001;

        System.out.println("Преобразование Фурье от функции x^2");
        System.out.println("Число отсчетов " + n);
        System.out.println("Интегрирование в промежутке от " + a + " до " + b);
        System.out.println("ksi:\t Re: \t Im:");

        for (double ksi = a; ksi <= b; ksi += step) {
            showAll(precision, a, b, ksi);
        }

        /*XYSeriesCollection collection = new XYSeriesCollection();

        XYSeries series = new XYSeries("Re");
        XYSeries series1 = new XYSeries("Im");
        for (double i = a; i <= b ; i+=step) {
            series.add(i, function(i).getReal());
            series1.add(i, function(i).getImaginary());
        }
        collection.addSeries(series);
        collection.addSeries(series1);
        JFreeChart chart = ChartFactory.createXYLineChart("f(x) = e^(4ix)", "x", "y", collection, PlotOrientation.VERTICAL, true, true, true);
//        JFreeChart chart = ChartFactory.createXYLineChart("f(x) = sin2x", "x", "y", collection, PlotOrientation.VERTICAL, true, true, true);
//        JFreeChart chart = ChartFactory.createXYLineChart("f(x) = x^2", "x", "y", collection, PlotOrientation.VERTICAL, true, true, true);
        JFrame frame = new JFrame("График функции");
        frame.getContentPane().add(new ChartPanel(chart));
        frame.setSize(800, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        XYSeriesCollection collection1 = new XYSeriesCollection();
        XYSeries series2 = new XYSeries("Re");
        XYSeries series3 = new XYSeries("Im");

        XYSeriesCollection collection2 = new XYSeriesCollection();
        XYSeries series4 = new XYSeries("Квадрат модуля преобразования");

        XYSeriesCollection collection3 = new XYSeriesCollection();
        XYSeries series5 = new XYSeries("Аргумент преобразования");

        for (double ksi = a; ksi <= b; ksi += step) {
//            showAll(precision, a, b, ksi);
            transformFunction = calculateTransform(precision, a, b, ksi);
            series2.add(ksi, transformFunction.getReal());
            series3.add(ksi, transformFunction.getImaginary());
            series4.add(ksi, transformFunction.abs() * transformFunction.abs());
            series5.add(ksi, transformFunction.getArgument());
        }
        collection1.addSeries(series2);
        collection1.addSeries(series3);
        JFreeChart chart1 = ChartFactory.createXYLineChart("F(\u03BE)", "x", "y", collection1, PlotOrientation.VERTICAL, true, true, true);
        JFrame frame1 = new JFrame("График преобразования Фурье от функции");
        frame1.getContentPane().add(new ChartPanel(chart1));
        frame1.setSize(800, 600);
        frame1.setVisible(true);
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        collection2.addSeries(series4);

        JFreeChart chart2 = ChartFactory.createXYLineChart("Квадрат модуля F(\u03BE)", "x", "y", collection2, PlotOrientation.VERTICAL, true, true, true);
        JFrame frame2 = new JFrame("График квадрата модуля преобразования Фурье от функции");
        frame2.getContentPane().add(new ChartPanel(chart2));
        frame2.setSize(800, 600);
        frame2.setVisible(true);
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        collection3.addSeries(series5);

        JFreeChart chart3 = ChartFactory.createXYLineChart("Аргумент F(\u03BE)", "x", "y", collection3, PlotOrientation.VERTICAL, true, true, true);
        JFrame frame3 = new JFrame("График амплитуды преобразования Фурье от функции");
        frame3.getContentPane().add(new ChartPanel(chart3));
        frame3.setSize(800, 600);
        frame3.setVisible(true);
        frame3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);*/


    }
}
