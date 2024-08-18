package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.BeerCSVRecord;

import java.io.File;
import java.util.List;

/**
 * @author Carson
 * @Version
 */
public interface BeerCsvService {
    List<BeerCSVRecord> convertCSV(File csvFile);
}