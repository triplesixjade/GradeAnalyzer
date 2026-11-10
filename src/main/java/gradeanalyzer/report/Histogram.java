package gradeanalyzer.report;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Histogram {
    public String buildAverageHistogram(List<Double> values) {
        TreeMap<Double, Integer> buckets = new TreeMap<>();
        for (double v : values) {
            if (Double.isNaN(v)) continue;
            double b = Math.floor(v * 2) / 2.0; // шаг 0.5
            buckets.merge(b, 1, Integer::sum);
        }
        int max = buckets.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        int width = 40;
        StringBuilder sb = new StringBuilder();
        sb.append("Распределение средних баллов:\n");
        for (Map.Entry<Double, Integer> e : buckets.entrySet()) {
            double lo = e.getKey(), hi = lo + 0.5;
            int count = e.getValue();
            int bar = (int) Math.round((double) count / max * width);
            sb.append(String.format("[%.1f, %.1f) %3d | %s%n", lo, hi, count, "█".repeat(Math.max(1, bar))));
        }
        return sb.toString();
    }
}