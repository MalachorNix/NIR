import org.apache.commons.math3.complex.Complex;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

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

        double precision = 0.001;
        String functionName = "exp^(4ix)";

        Scanner scanner = new Scanner(System.in);
        System.out.println("Считаем преобразование Фурье от " + functionName + ".");
        System.out.print("Введите начало промежутка интегрирования по x: ");
        double a = scanner.nextDouble();
        System.out.print("Введите конец промежутка интегрирования по x: ");
        double b = scanner.nextDouble();
        while (b <= a) {
            System.out.println("Конец промежутка не может быть меньше начала!");
            System.out.print("Введите конец промежутка интегрирования по x: ");
            b = scanner.nextDouble();
        }
        Scanner intScanner = new Scanner(System.in);
        System.out.print("Введите количество шагов: ");
        int n = intScanner.nextInt();
        while (n <= 0) {
            System.out.println("Количество отсчетов должно быть больше 0!");
            System.out.print("Введите количество шагов: ");
            n = intScanner.nextInt();
        }
        double step = (b - a) / n;

        double ksi;
        double ksiA;
        double ksiB;
        double stepKsi;

        System.out.print("Введите начало промежутка интегрирования по ξ: ");
        ksiA = scanner.nextDouble();
        System.out.print("Введите конец промежутка интегрирования по ξ: ");
        ksiB = scanner.nextDouble();
        while (ksiB <= ksiA) {
            System.out.println("Конец промежутка не может быть меньше начала!");
            System.out.print("Введите конец промежутка интегрирования по ξ: ");
            ksiB = scanner.nextDouble();
        }

        System.out.print("Введите количество шагов: ");
        int ksiN = intScanner.nextInt();
        while (ksiN <= 0) {
            System.out.println("Количество отсчетов должно быть больше 0!");
            System.out.print("Введите количество шагов: ");
            ksiN = intScanner.nextInt();
        }

        stepKsi = (ksiB - ksiA) / ksiN;

        File fileOfTransformFunction = new File("Функция " + functionName + ".gr");

        try {
            if (!fileOfTransformFunction.exists()) {
                fileOfTransformFunction.createNewFile();
//                System.out.println("Файл создан");
            }
            else {
//                System.out.println("Файл существует");
            }
        } catch (IOException e) {
            System.out.println("Ошибка при создании файла");
        }


        /*JFileChooser fileChooser = new JFileChooser("function");
        fileChooser.setVisible(true);
        fileChooser.setSize(800, 600);
        int ret = fileChooser.showDialog(null, "Выберите файл с функцией");

        if (ret == JFileChooser.APPROVE_OPTION) {
            try {
                FileReader reader = new FileReader(fileChooser.getSelectedFile());
                BufferedReader bufferedReader = new BufferedReader(reader);
                String builder;
                do {
                    builder = bufferedReader.readLine();
                    if (builder != null) {
                        System.out.println(builder);
                    }
                } while (builder != null);
            } catch (FileNotFoundException e) {
                System.out.println("Файл не найден!");
            } catch (IOException e) {
                System.out.println("Ошибка ввода/вывода!");
            }
        }*/

        XYSeriesCollection collectionOfFunction = new XYSeriesCollection();

        XYSeries realSeries = new XYSeries("Re");
        XYSeries imaginarySeries = new XYSeries("Im");

        for (double i = a; i <= b ; i+=step) {
            realSeries.add(i, function(i).getReal());
            imaginarySeries.add(i, function(i).getImaginary());
        }

        collectionOfFunction.addSeries(realSeries);
        collectionOfFunction.addSeries(imaginarySeries);
        JFreeChart chartOfFunction = ChartFactory.createXYLineChart("f(x) = " + functionName, "x", "y",
                collectionOfFunction, PlotOrientation.VERTICAL, true, true, true);

        try {
            ChartUtilities.saveChartAsPNG(new File("picture/График функции " + functionName + ".png"),
                    chartOfFunction, 800, 600);
        } catch (IOException e) {
            System.out.println("Ошибка записи!");
        }



        XYSeriesCollection collectionOfTransformFunction = new XYSeriesCollection();
        XYSeries realSeriesOfTransformFunction = new XYSeries("Re");
        XYSeries imaginarySeriesOfTransformFunction = new XYSeries("Im");

        XYSeriesCollection collectionOfIntensity = new XYSeriesCollection();
        XYSeries seriesOfIntensity = new XYSeries("Квадрат модуля преобразования");

        XYSeriesCollection collectionOfPhase = new XYSeriesCollection();
        XYSeries seriesOfPhase = new XYSeries("Аргумент преобразования");


        FileWriter  fileWriter = null;
        try {
            fileWriter = new FileWriter(fileOfTransformFunction);
//            fileWriter = new FileWriter(new File("exp^(4ix).gr"));
            fileWriter.write("Преобразование Фурье от " + functionName + "\n" + (ksiN + 1) + "\n");
            for (ksi = ksiA; ksi <= ksiB; ksi += stepKsi) {
                transformFunction = calculateTransform(precision, a, b, ksi);
                realSeriesOfTransformFunction.add(ksi, transformFunction.getReal());
                imaginarySeriesOfTransformFunction.add(ksi, transformFunction.getImaginary());
                seriesOfIntensity.add(ksi, transformFunction.abs() * transformFunction.abs());
                seriesOfPhase.add(ksi, transformFunction.getArgument());
                fileWriter.write(ksi + " " + transformFunction.getReal() + " " + transformFunction.getImaginary() + "\n");
                fileWriter.flush();
            }

        } catch (IOException e) {
            System.out.println("Ошибка записи!");
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        collectionOfTransformFunction.addSeries(realSeriesOfTransformFunction);
        collectionOfTransformFunction.addSeries(imaginarySeriesOfTransformFunction);
        JFreeChart chart1 = ChartFactory.createXYLineChart("F(ξ)", "ξ", "y", collectionOfTransformFunction, PlotOrientation.VERTICAL, true, true, true);

        try {
            ChartUtilities.saveChartAsPNG(new File("picture/Преобразование Фурье " + functionName + ".png"), chart1,
                    800, 600);
        } catch (IOException e) {
            System.out.println("Ошибка записи!");
        }

        collectionOfIntensity.addSeries(seriesOfIntensity);

        JFreeChart chart2 = ChartFactory.createXYLineChart("Квадрат модуля F(ξ)", "ξ", "y", collectionOfIntensity, PlotOrientation.VERTICAL, true, true, true);

        try {
            ChartUtilities.saveChartAsPNG(new File("picture/Квадрат модуля F(ξ) от " + functionName + ".png"), chart2,
                    800, 600);
        } catch (IOException e) {
            System.out.println("Ошибка записи!");
        }

        collectionOfPhase.addSeries(seriesOfPhase);

        JFreeChart chart3 = ChartFactory.createXYLineChart("Аргумент F(ξ)", "ξ", "y", collectionOfPhase, PlotOrientation.VERTICAL, true, true, true);

        try {
            ChartUtilities.saveChartAsPNG(new File("picture/Аргумент F(ξ) от " + functionName + ".png"), chart3,
                    800, 600);
        } catch (IOException e) {
            System.out.println("Ошибка записи!");
        }


    }
}
