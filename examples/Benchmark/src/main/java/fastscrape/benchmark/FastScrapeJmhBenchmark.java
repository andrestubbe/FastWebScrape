package fastscrape.benchmark;

import fastscrape.FastScrape;
import org.openjdk.jmh.annotations.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Standard JMH Benchmark Suite for FastScrape Native AVX2 HTML Parsers vs JDK RegEx.
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class FastScrapeJmhBenchmark {

    private static final Pattern HREF_PATTERN = Pattern.compile("href=\"([^\"]+)\"");
    private static final Pattern TAG_PATTERN = Pattern.compile("<[^>]+>");

    private FastScrape scraper;
    private byte[] sampleHtmlBytes;
    private String sampleHtmlText;

    @Setup
    public void setup() {
        scraper = FastScrape.open();
        try {
            HttpClient http = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://en.wikipedia.org/wiki/Java_(programming_language)"))
                    .header("User-Agent", "FastScrape-JMH/0.1.1")
                    .build();
            sampleHtmlBytes = http.send(req, HttpResponse.BodyHandlers.ofByteArray()).body();
        } catch (Exception e) {
            sampleHtmlBytes = buildSyntheticHtml();
        }
        sampleHtmlText = new String(sampleHtmlBytes, StandardCharsets.UTF_8);
    }

    @Benchmark
    public String benchmarkJdkTagStripping() {
        return TAG_PATTERN.matcher(sampleHtmlText).replaceAll("");
    }

    @Benchmark
    public String benchmarkFastScrapeCleanText() {
        return scraper.extractReadableText(sampleHtmlBytes);
    }

    @Benchmark
    public int benchmarkJdkPatternExtraction() {
        Matcher matcher = HREF_PATTERN.matcher(sampleHtmlText);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    @Benchmark
    public int benchmarkFastScrapeLinkExtraction() {
        List<String> links = scraper.extractLinks(sampleHtmlBytes);
        return links.size();
    }

    private static byte[] buildSyntheticHtml() {
        StringBuilder sb = new StringBuilder(500_000);
        sb.append("<!DOCTYPE html><html><body><h1>Benchmark Document</h1>");
        for (int i = 0; i < 2000; i++) {
            sb.append("<p>Paragraph containing references to <a href=\"/wiki/Article_")
              .append(i)
              .append("\">Link ")
              .append(i)
              .append("</a> and some text describing vector hardware acceleration.</p>");
        }
        sb.append("</body></html>");
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
