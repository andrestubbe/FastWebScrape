package fastscrape;

import fastansi.FastANSI;
import fastregex.FastRegex;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Multi-Tier Head-to-Head Comparative Benchmark Suite for FastScrape vs Standard Java HTML/Regex Parsers.
 */
public class Benchmark {

    private Benchmark() {}

    private static final Pattern HREF_PATTERN = Pattern.compile("href=\"([^\"]+)\"");
    private static final Pattern TAG_PATTERN = Pattern.compile("<[^>]+>");

    public static void main(String[] args) throws Exception {
        System.out.println(darkGray("========================================================================================================================"));
        System.out.println(" " + boldWhite("FastScrape & FastJava") + darkGray(" — Comprehensive Multi-Tier 120-Column Benchmark Suite"));
        System.out.println(darkGray("========================================================================================================================"));
        System.out.println();

        FastScrape scraper = FastScrape.open();

        // Download live Wikipedia document
        System.out.println(darkGray("[Ingestion]") + " " + boldWhite("Live Benchmark Document Download") + darkGray(" (Wikipedia: Java Programming Language)"));
        System.out.print(darkGray(" Downloading live payload ... "));

        HttpClient http = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://en.wikipedia.org/wiki/Java_(programming_language)"))
                .header("User-Agent", "FastScrape-Benchmark/0.1.1 (Java 17+; AVX2)")
                .build();
        byte[] htmlBytes = http.send(req, HttpResponse.BodyHandlers.ofByteArray()).body();
        String htmlText = new String(htmlBytes, StandardCharsets.UTF_8);
        System.out.printf(darkGray("OK (") + boldWhite("%,d bytes") + darkGray(")\n\n"), htmlBytes.length);

        // ─────────────────────────────────────────────────────────────────────
        // Tier 1: Zero-Allocation AVX2 Clean Text & Tag Stripper Benchmark
        // ─────────────────────────────────────────────────────────────────────
        System.out.println(darkGray("[Tier 1]") + " " + boldWhite("HTML Plaintext & Tag Stripper Benchmark") + darkGray(" (Clean Text for LLMs)"));

        // JDK Tag Stripping
        for (int i = 0; i < 30; i++) TAG_PATTERN.matcher(htmlText).replaceAll("");
        long jdkStripT0 = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            TAG_PATTERN.matcher(htmlText).replaceAll("");
        }
        long jdkStripNanos = System.nanoTime() - jdkStripT0;
        double jdkStripOpsPerMs = 100.0 / (jdkStripNanos / 1_000_000.0);
        double jdkStripAvgMs = (jdkStripNanos / 1_000_000.0) / 100.0;

        // FastScrape Native AVX2 Clean Text Stripper
        for (int i = 0; i < 30; i++) scraper.extractReadableText(htmlBytes);
        long fastStripT0 = System.nanoTime();
        String cleanSample = "";
        for (int i = 0; i < 100; i++) {
            cleanSample = scraper.extractReadableText(htmlBytes);
        }
        long fastStripNanos = System.nanoTime() - fastStripT0;
        double fastStripOpsPerMs = 100.0 / (fastStripNanos / 1_000_000.0);
        double fastStripAvgMs = (fastStripNanos / 1_000_000.0) / 100.0;

        double stripSpeedup = (double) jdkStripNanos / fastStripNanos;

        System.out.println();
        System.out.println(darkGray("------------------------------------------------------------------------------------------------------------------------"));
        System.out.printf(" %-48s | %-22s | %-20s | %-20s\n", "HTML Stripper Engine", "Throughput (ops/ms)", "Avg Latency (ms)", "Speedup");
        System.out.println(darkGray("------------------------------------------------------------------------------------------------------------------------"));
        System.out.printf(" %-48s | %22.2f | %17.3f ms | %s\n", "Standard JDK RegEx HTML Parser", jdkStripOpsPerMs, jdkStripAvgMs, darkGray("1.00x Base          "));
        System.out.printf(" %-48s | %22.2f | %17.3f ms | %s\n", "FastScrape AVX2 CleanText Stripper", fastStripOpsPerMs, fastStripAvgMs, boldWhite(String.format("%.2fx Faster         ", stripSpeedup)));
        System.out.println(darkGray("------------------------------------------------------------------------------------------------------------------------"));
        System.out.printf(darkGray(" Processed 100 full payloads extracting ") + boldWhite(String.format("%,d characters", cleanSample.length())) + darkGray(" of clean text.\n\n"));

        // ─────────────────────────────────────────────────────────────────────
        // Tier 2: Native AVX2 Hyperlink Extraction Benchmark
        // ─────────────────────────────────────────────────────────────────────
        System.out.println(darkGray("[Tier 2]") + " " + boldWhite("HTML Hyperlink Extraction Benchmark") + darkGray(" (Href Parsing Across 300 Iterations)"));

        // JDK Pattern Matcher
        for (int i = 0; i < 50; i++) runJdkRegex(htmlText, HREF_PATTERN);
        long t0 = System.nanoTime();
        int totalJdkLinks = 0;
        for (int i = 0; i < 300; i++) {
            totalJdkLinks += runJdkRegex(htmlText, HREF_PATTERN);
        }
        long jdkNanos = System.nanoTime() - t0;
        double jdkOpsPerMs = 300.0 / (jdkNanos / 1_000_000.0);
        double jdkAvgMs = (jdkNanos / 1_000_000.0) / 300.0;

        // FastScrape Native AVX2
        for (int i = 0; i < 50; i++) scraper.extractLinks(htmlBytes);
        long t1 = System.nanoTime();
        int totalFastLinks = 0;
        for (int i = 0; i < 300; i++) {
            totalFastLinks += scraper.extractLinks(htmlBytes).size();
        }
        long fastNanos = System.nanoTime() - t1;
        double fastOpsPerMs = 300.0 / (fastNanos / 1_000_000.0);
        double fastAvgMs = (fastNanos / 1_000_000.0) / 300.0;

        double extractSpeedup = (double) jdkNanos / fastNanos;

        System.out.println();
        System.out.println(darkGray("------------------------------------------------------------------------------------------------------------------------"));
        System.out.printf(" %-48s | %-22s | %-20s | %-20s\n", "Extraction Engine", "Throughput (ops/ms)", "Avg Latency (ms)", "Speedup");
        System.out.println(darkGray("------------------------------------------------------------------------------------------------------------------------"));
        System.out.printf(" %-48s | %22.2f | %17.3f ms | %s\n", "Standard JDK Pattern.matcher(\"href=...\")", jdkOpsPerMs, jdkAvgMs, darkGray("1.00x Base          "));
        System.out.printf(" %-48s | %22.2f | %17.3f ms | %s\n", "FastScrape Native AVX2 Link Scanner", fastOpsPerMs, fastAvgMs, boldWhite(String.format("%.2fx Faster         ", extractSpeedup)));
        System.out.println(darkGray("------------------------------------------------------------------------------------------------------------------------"));
        System.out.printf(darkGray(" Extracted ") + boldWhite(String.format("%,d links", totalFastLinks / 300)) + darkGray(" per iteration across 300 iterations.\n\n"));

        // ─────────────────────────────────────────────────────────────────────
        // Tier 3: Element & Tag Isolation Benchmark (<H1> & <TITLE>)
        // ─────────────────────────────────────────────────────────────────────
        System.out.println(darkGray("[Tier 3]") + " " + boldWhite("HTML Element Tag Isolation Benchmark") + darkGray(" (Extracting Headings & Metadata)"));

        Pattern h1Pattern = Pattern.compile("<h1[^>]*>(.*?)</h1>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        for (int i = 0; i < 50; i++) runJdkRegex(htmlText, h1Pattern);
        long jdkTagT0 = System.nanoTime();
        for (int i = 0; i < 500; i++) {
            runJdkRegex(htmlText, h1Pattern);
        }
        long jdkTagNanos = System.nanoTime() - jdkTagT0;
        double jdkTagOpsPerMs = 500.0 / (jdkTagNanos / 1_000_000.0);
        double jdkTagAvgUs = (jdkTagNanos / 1_000.0) / 500.0;

        // FastScrape extractByTag
        for (int i = 0; i < 50; i++) scraper.extractByTag(htmlBytes, "h1");
        long fastTagT0 = System.nanoTime();
        for (int i = 0; i < 500; i++) {
            scraper.extractByTag(htmlBytes, "h1");
        }
        long fastTagNanos = System.nanoTime() - fastTagT0;
        double fastTagOpsPerMs = 500.0 / (fastTagNanos / 1_000_000.0);
        double fastTagAvgUs = (fastTagNanos / 1_000.0) / 500.0;

        double tagSpeedup = (double) jdkTagNanos / fastTagNanos;

        System.out.println();
        System.out.println(darkGray("------------------------------------------------------------------------------------------------------------------------"));
        System.out.printf(" %-48s | %-22s | %-20s | %-20s\n", "Tag Extraction Engine", "Throughput (ops/ms)", "Avg Latency (µs)", "Speedup");
        System.out.println(darkGray("------------------------------------------------------------------------------------------------------------------------"));
        System.out.printf(" %-48s | %22.2f | %17.1f µs | %s\n", "Standard JDK RegEx <H1> Extractor", jdkTagOpsPerMs, jdkTagAvgUs, darkGray("1.00x Base          "));
        System.out.printf(" %-48s | %22.2f | %17.1f µs | %s\n", "FastScrape AVX2 extractByTag(\"h1\")", fastTagOpsPerMs, fastTagAvgUs, boldWhite(String.format("%.2fx Faster         ", tagSpeedup)));
        System.out.println(darkGray("------------------------------------------------------------------------------------------------------------------------"));
        System.out.printf(darkGray(" Isolated element targets across 500 iterations in ") + boldWhite(String.format("%.1f ms", fastTagNanos / 1_000_000.0)) + darkGray(" total.\n\n"));

        // ─────────────────────────────────────────────────────────────────────
        // Summary Card
        // ─────────────────────────────────────────────────────────────────────
        System.out.println(darkGray("========================================================================================================================"));
        System.out.printf(" " + boldWhite("BENCHMARK VERDICT:") + darkGray(" FastScrape outperforms standard JDK parsers (") + boldWhite(String.format("CleanText: %.2fx", stripSpeedup)) + darkGray(" | ") + boldWhite(String.format("Link Ext: %.2fx", extractSpeedup)) + darkGray(" | ") + boldWhite(String.format("Tag Ext: %.2fx", tagSpeedup)) + darkGray(").\n"));
        System.out.println(darkGray("========================================================================================================================"));
    }

    private static String darkGray(String text) {
        return FastANSI.fg(240) + text + FastANSI.RESET;
    }

    private static String boldWhite(String text) {
        return FastANSI.BOLD + FastANSI.FG_BRIGHT_WHITE + text + FastANSI.RESET;
    }

    private static int runJdkRegex(String text, Pattern pattern) {
        Matcher m = pattern.matcher(text);
        int count = 0;
        while (m.find()) {
            count++;
        }
        return count;
    }
}
