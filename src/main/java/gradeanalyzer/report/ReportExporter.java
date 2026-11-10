package gradeanalyzer.report;

import gradeanalyzer.model.GradeJournal;
import gradeanalyzer.model.Student;
import gradeanalyzer.risk.RiskModel;
import gradeanalyzer.stats.StatisticsCalculator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ReportExporter {
    private final StatisticsCalculator stats = new StatisticsCalculator();
    private final Histogram histogram = new Histogram();
    private final RiskModel riskModel = new RiskModel();

    public void export(GradeJournal journal, Path outFile) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ОТЧЁТ ПО УСПЕВАЕМОСТИ ===\n\n");
        sb.append("Студентов: ").append(journal.getStudents().size()).append('\n');
        sb.append("Дисциплин: ").append(journal.getDisciplines().size()).append('\n');
        sb.append("Семестров: ").append(journal.getSemesters()).append("\n\n");

        List<Double> averages = stats.studentAverages(journal);
        sb.append("Средний балл по журналу: ").append(String.format("%.2f%n", stats.mean(averages)));
        sb.append("Медиана средних: ").append(String.format("%.2f%n", stats.median(averages)));
        sb.append("StdDev средних: ").append(String.format("%.2f%n", stats.stdev(averages)));
        sb.append('\n');

        sb.append(histogram.buildAverageHistogram(averages)).append('\n');

        sb.append("Модель риска:\n");
        double[] metrics = riskModel.evaluate(journal);
        sb.append(String.format("  Precision: %.2f%n  Recall:    %.2f%n  F1:        %.2f%n",
            metrics[0], metrics[1], metrics[2]));

        sb.append("\nСтуденты в зоне риска:\n");
        int risky = 0;
        for (Student s : journal.getStudents()) {
            if (riskModel.predictAtRisk(s)) {
                risky++;
                sb.append(String.format("  %s %-15s avg=%.2f%n",
                    s.getId(), s.getName(), stats.mean(s.allGrades())));
            }
        }
        sb.append("Всего в группе риска: ").append(risky).append('\n');

        Files.writeString(outFile, sb.toString());
    }
}