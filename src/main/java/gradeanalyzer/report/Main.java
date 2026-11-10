package gradeanalyzer;

import gradeanalyzer.data.CsvLoader;
import gradeanalyzer.data.SyntheticDataGenerator;
import gradeanalyzer.model.GradeJournal;
import gradeanalyzer.report.ReportExporter;
import gradeanalyzer.risk.RiskModel;

import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws Exception {
        Path dataDir = Path.of("data");
        Files.createDirectories(dataDir);

        Path csv = dataDir.resolve("grades.csv");
        Path report = dataDir.resolve("report.txt");

        // 1. Генерируем данные
        SyntheticDataGenerator gen = new SyntheticDataGenerator();
        GradeJournal journal = gen.generate(120, 4, 42L);
        gen.labelRisk(journal); // размечаем для модели риска
        gen.exportToCsv(journal, csv, false);
        System.out.println("Сгенерировано студентов: " + journal.getStudents().size());

        // 2. Загружаем данные (проверка парсера)
        CsvLoader loader = new CsvLoader();
        GradeJournal loaded = loader.load(csv);
        gen.labelRisk(loaded);

        // 3. Оценка модели и отчет
        RiskModel model = new RiskModel();
        double[] m = model.evaluate(loaded);
        System.out.printf("Precision=%.2f Recall=%.2f F1=%.2f%n", m[0], m[1], m[2]);

        new ReportExporter().export(loaded, report);
        System.out.println("Отчёт сохранен: " + report.toAbsolutePath());
    }
}