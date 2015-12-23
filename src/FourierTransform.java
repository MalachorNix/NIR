import org.apache.commons.math3.complex.Complex;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.io.*;
import java.util.Scanner;

public class FourierTransform {
    private static Complex[] complex;
    private static double[] x;

    public static Complex transformFunction;

    public static Complex exponent(double x, double ksi) {
        return Complex.I.multiply(-2 * Math.PI * x * ksi).exp();
    }

    public static Complex integrand(double x, double ksi, Complex y) {
        return y.multiply(exponent(x, ksi));
    }

    public static Complex calculateTransform(double step, double a, double b, double ksi) {
        Complex sum = new Complex(0, 0);
        Complex multi;
        for (int i = 0; i < x.length; i++) {
            multi = integrand(x[i], ksi, complex[i]).multiply(step);
            sum = new Complex(sum.getReal() + multi.getReal(), sum.getImaginary() + multi.getImaginary());
        }
        return sum;
    }

    private static void discr() {
        String functionName = null;
        int n = 0;
        double[] a = null;
        double[] fi = null;
        Complex[] c = null;
        double[] alpha = null;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setVisible(true);
        fileChooser.setSize(800, 600);
        int ret = fileChooser.showDialog(null, "Выберите файл с функцией");

        if (ret == JFileChooser.APPROVE_OPTION) {
            try {
                FileReader reader = new FileReader(fileChooser.getSelectedFile());
                BufferedReader bufferedReader = new BufferedReader(reader);
                //Считали название функции
                String builder = bufferedReader.readLine();
                if (builder != null) {
                    functionName = builder;
                }
                //Считали количество элементов a
                builder = bufferedReader.readLine();
                if (builder != null) {
                    n = Integer.parseInt(builder);
                    a = new double[n];
                }
                int count = 0;
                do {
                   builder = bufferedReader.readLine();
                    if (builder != null) {
                        a[count] = new Double(builder);
                        count++;
                    }
                } while (count < n);
                //Считываем число элементов фи н-ое
                builder = bufferedReader.readLine();
                if (builder != null) {
                    n = Integer.parseInt(builder);
                    fi = new double[n];
                }

                //Проверка на размеры!
                if (fi.length != a.length) {
                    System.out.println("Размеры a и φ не совпадают. Исправьте!");
                    System.exit(0);
                }



                //Заполняем фи н-ое
                count = 0;
                do {
                    builder = bufferedReader.readLine();
                    if (builder != null) {
                        fi[count] = new Double(builder);
                        count++;
                    }
                } while (count < n);

                //Считаем с н-ое
                c = new Complex[n];
                for (int i = 0; i < n; i++) {
                    c[i] = Complex.I.multiply(fi[i]).exp().multiply(a[i]);
                }

                //Считываем количество альфа
                builder = bufferedReader.readLine();
                if (builder != null) {
                    n = Integer.parseInt(builder);
                    alpha = new double[n];
                }

                //Проверка на размеры
                if (fi.length != alpha.length) {
                    System.out.println("Размеры a и φ, и alpha не совпадают. Исправьте!");
                    System.exit(0);
                }

                //Заполняем альфа
                count = 0;
                do {
                    builder = bufferedReader.readLine();
                    if (builder != null) {
                        alpha[count] = new Double(builder);
                        count++;
                    }
                } while (count < n);

                //Считываем количество x
                builder = bufferedReader.readLine();
                if (builder != null) {
                    n = Integer.parseInt(builder);
                    x = new double[n];
                }

                //Заполняем x
                count = 0;
                do {
                    builder = bufferedReader.readLine();
                    if (builder != null) {
                        x[count] = new Double(builder);
                        count++;
                    }
                } while (count < n);


            } catch (FileNotFoundException e) {
                System.out.println("Файл не найден!");
            } catch (IOException e) {
                System.out.println("Ошибка ввода/вывода!");
            } catch (NumberFormatException e) {
                System.out.println("Неправильно считано число! Убедитесь, что нет текстовых символов в строке с " +
                        "числами, или наличие табулированной функции");
                System.exit(0);
            }
        }

        File fileOfTransformFunction = new File("Функция " + functionName + ".gr");

        try {
            if (!fileOfTransformFunction.exists()) {
                fileOfTransformFunction.createNewFile();
            }
            else {
            }
        } catch (IOException e) {
            System.out.println("Ошибка при создании файла");
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
            fileWriter.write("Fourier transform " + functionName + "\n" + (x.length - 1) + "\n");

            Complex[] function = new Complex[x.length];
            Complex exponenta;
            for (int i = 0; i < x.length; i++) {
                for (int j = 0; j < alpha.length; j++) {
                    exponenta = Complex.I.multiply(alpha[j] * x[i]).exp();
                    transformFunction = c[j].multiply(exponenta);
                    realSeriesOfTransformFunction.add(x[i], transformFunction.getReal());
                    imaginarySeriesOfTransformFunction.add(x[i], transformFunction.getImaginary());
                    seriesOfIntensity.add(x[i], transformFunction.abs() * transformFunction.abs());
                    seriesOfPhase.add(x[i], transformFunction.getArgument());
                    fileWriter.write(x[i] + " " + transformFunction.getReal() + " " +
                            transformFunction.getImaginary() + "\n");
                    fileWriter.flush();
                }
            }

        } catch (IOException e) {
            System.out.println("Ошибка записи!");
        } catch (NullPointerException e) {
            System.out.println("Ошибка! Убедитесь, что нет текстовых символов в строке с числами, или наличие " +
                    "табулированной функции");
            System.exit(0);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        XYSeriesCollection collectionOfFunction = new XYSeriesCollection();

        XYSeries realSeries = new XYSeries("Re");
        XYSeries imaginarySeries = new XYSeries("Im");

        /*try {
            for (int i = 0; i < x.length ; i++) {
                realSeries.add(x[i], complex[i].getReal());
                imaginarySeries.add(x[i], complex[i].getImaginary());
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
            }*/

            collectionOfTransformFunction.addSeries(realSeriesOfTransformFunction);
            collectionOfTransformFunction.addSeries(imaginarySeriesOfTransformFunction);
            JFreeChart chart1 = ChartFactory.createXYLineChart("f(x)", "ξ", "y", collectionOfTransformFunction, PlotOrientation.VERTICAL, true, true, true);

            try {
                ChartUtilities.saveChartAsPNG(new File("picture/Преобразование Фурье " + functionName + ".png"), chart1,
                        800, 600);
            } catch (IOException e) {
                System.out.println("Ошибка записи!");
            }

            collectionOfIntensity.addSeries(seriesOfIntensity);

            JFreeChart chart2 = ChartFactory.createXYLineChart("Квадрат модуля f(x)", "ξ", "y", collectionOfIntensity, PlotOrientation.VERTICAL, true, true, true);

            try {
                ChartUtilities.saveChartAsPNG(new File("picture/Квадрат модуля f(x) от " + functionName + ".png"), chart2,
                        800, 600);
            } catch (IOException e) {
                System.out.println("Ошибка записи!");
            }

            collectionOfPhase.addSeries(seriesOfPhase);

            JFreeChart chart3 = ChartFactory.createXYLineChart("Аргумент f(x)", "ξ", "y", collectionOfPhase, PlotOrientation.VERTICAL, true, true, true);

            try {
                ChartUtilities.saveChartAsPNG(new File("picture/Аргумент f(x) от " + functionName + ".png"), chart3,
                        800, 600);
            } catch (IOException e) {
                System.out.println("Ошибка записи!");
            } catch (NullPointerException e) {
            System.out.println("Ошибка! Убедитесь, что нет текстовых символов в строке с числами.");
        }
    }


    public static void main(String[] args) {

        String functionName = null;
        int n = 0;
        double[] ksi = null;
        double precision;
        int u = 0;
        double ksiA = 0;
        double ksiB = 0;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Нажите 1, если хотите посчитать преобразование Фурье непрерывной функции");
        System.out.println("Нажите 2, если хотите посчитать преобразование Фурье дискретной функции");
        int choice = scanner.nextInt();
        if (choice == 1) {
            analog();
        }
        if (choice == 2) {
            discr();
        }

    }

    private static void analog() {
        String functionName = null; int n = 0; double[] ksi = null; int u = 0; double ksiA = 0; double ksiB = 0;
        double precision;JFileChooser fileChooser = new JFileChooser();
        fileChooser.setVisible(true);
        fileChooser.setSize(800, 600);
        int ret = fileChooser.showDialog(null, "Выберите файл с функцией");

        if (ret == JFileChooser.APPROVE_OPTION) {
            try {
                FileReader reader = new FileReader(fileChooser.getSelectedFile());
                BufferedReader bufferedReader = new BufferedReader(reader);
                //Считали название функции
                String builder = bufferedReader.readLine();
                if (builder != null) {
                    functionName = builder;
                }
                //Считали количество элементов ξ
                builder = bufferedReader.readLine();
                if (builder != null) {
                    n = Integer.parseInt(builder);
                    ksi = new double[n];
                }
                //Считали нижнюю часть ξ
                builder = bufferedReader.readLine();
                if (builder != null) {
                    ksiA = Double.parseDouble(builder);
                    builder = bufferedReader.readLine();
                    if (builder != null) {
                        ksiB = Double.parseDouble(builder);
                    }
                    //Проверка, что верхняя часть больше нижней
                    if (ksiB <= ksiA) {
                        System.out.println("Верхняя граница ξ меньше нижней! Поменяйте во входном файле диапазон ξ");
                        System.exit(0);
                    }
                } else {
                    System.out.println("Нет дальнейших значений диапазона ξ");
                    System.exit(0);
                }
                //Заполняем массив ξ
                double ksiStep = (ksiB - ksiA) / n; // Шаг
                for (int i = 0; i < ksi.length; i++) {
                    ksi[i] = ksiA;
                    ksiA+=ksiStep;
                }
                //Создаем массив x и комлексного y
                builder = bufferedReader.readLine();
                if (builder != null) {
                    int length = new Integer(builder);
                    x = new double[length];
                    complex = new Complex[length];
                } else {
                    System.out.println("Не указано количество отсчетов табулированной функции");
                    System.exit(0);
                }
                //Заполняем массив x и y
                String[] splitted;
                do {
                    builder = bufferedReader.readLine();
                    if (builder != null) {
                        splitted = builder.split("\\s+");
                        if (splitted.length == 3) {
                            x[u] = new Double(splitted[0]);
                            complex[u] = new Complex(new Double(splitted[1]), new Double(splitted[2]));
                            u++;
                        } else {
                            System.out.println("Ошибка!");
                            System.out.println("Убедитесь, что массив значения табулированной функции состоят " +
                                    "из x и вещественной и мнимой части функции в строке.");
                            System.exit(0);
                        }
                    }
                } while (builder != null);
            } catch (FileNotFoundException e) {
                System.out.println("Файл не найден!");
            } catch (IOException e) {
                System.out.println("Ошибка ввода/вывода!");
            } catch (NumberFormatException e) {
                System.out.println("Неправильно считано число! Убедитесь, что нет текстовых символов в строке с " +
                        "числами, или наличие табулированной функции");
                System.exit(0);
            }
        }

        File fileOfTransformFunction = new File("Функция " + functionName + ".gr");

        try {
            if (!fileOfTransformFunction.exists()) {
                fileOfTransformFunction.createNewFile();
            }
            else {
            }
        } catch (IOException e) {
            System.out.println("Ошибка при создании файла");
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
            fileWriter.write("Fourier transform " + functionName + "\n" + (ksi.length - 1) + "\n");
            precision = (x[x.length - 1] - x[0]) / x.length;

            for (double aKsi : ksi) {
                transformFunction = calculateTransform(precision, x[0], x[x.length - 1], aKsi);
                realSeriesOfTransformFunction.add(aKsi, transformFunction.getReal());
                imaginarySeriesOfTransformFunction.add(aKsi, transformFunction.getImaginary());
                seriesOfIntensity.add(aKsi, transformFunction.abs() * transformFunction.abs());
                seriesOfPhase.add(aKsi, transformFunction.getArgument());
                fileWriter.write(aKsi + " " + transformFunction.getReal() + " " +
                        transformFunction.getImaginary() + "\n");
                fileWriter.flush();
            }

        } catch (IOException e) {
            System.out.println("Ошибка записи!");
        } catch (NullPointerException e) {
            System.out.println("Ошибка! Убедитесь, что нет текстовых символов в строке с числами, или наличие " +
                    "табулированной функции");
            System.exit(0);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        XYSeriesCollection collectionOfFunction = new XYSeriesCollection();

        XYSeries realSeries = new XYSeries("Re");
        XYSeries imaginarySeries = new XYSeries("Im");

        try {
            for (int i = 0; i < x.length ; i++) {
                realSeries.add(x[i], complex[i].getReal());
                imaginarySeries.add(x[i], complex[i].getImaginary());
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
        } catch (NullPointerException e) {
            System.out.println("Ошибка! Убедитесь, что нет текстовых символов в строке с числами.");
        }
    }
}