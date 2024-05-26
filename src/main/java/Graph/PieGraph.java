package Graph;

import org.example.Entity.Product;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.chart.ui.UIUtils;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PieGraph extends JFrame {
    private List<Product> data;

    public PieGraph(List<Product> data) {
        super("График товаров");
        this.data = data;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Устанавливаем DISPOSE_ON_CLOSE

        DefaultPieDataset dataset = createDataset();
        // Создаем диаграмму
        JFreeChart chart = createChart(dataset);
        // Создаем панель диаграммы и добавляем её в окно приложения
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(560, 370));
        setContentPane(chartPanel);

        // Центрируем окно на экране
        setLocationRelativeTo(null);
    }

    private JFreeChart createChart(DefaultPieDataset dataset) {
        JFreeChart chart = ChartFactory.createPieChart(
                "Example Pie Chart",  // заголовок диаграммы
                dataset,             // данные
                true,                // легенда
                true,
                false);

        PiePlot plot = (PiePlot) chart.getPlot();
        // Настройка диаграммы (если требуется)

        return chart;
    }

    private DefaultPieDataset createDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Product product : data) {
            dataset.setValue(product.getName(), product.getPrice());
        }
        return dataset;
    }
}
